package de.bausdorf.simcacing.tt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SessionData {

	private String sessionId;
	private String teamName;
	private String carName;
	private String sessionLaps;
	private String sessionTime;
	private double maxFuel;
	private String trackName;
	private String sessionType;
	private double greenFlagTime;

	private List<Stint> stints = new ArrayList<>();
	private List<Lap> laps = new ArrayList<>();
	private RunData runData = new RunData();

	public void addLap(Map<String, Object> lapData) {

		Integer lapNo = (Integer) lapData.get("lap");
		Integer stintNo = (Integer) lapData.get("stintCount");
		Integer stintLap = (Integer) lapData.get("stintLap");
		Double fuelLevel = (Double) lapData.get("fuelLevel");
		Lap lap = ((laps == null) || (laps.size() == 0)) ? null : laps.get(0);
		if (lap != null && lap.getNo() == lapNo ) {
			throw new IllegalArgumentException("Lap " + lap.getNo() + " already there");
		}
		Double laptime = (Double) lapData.get("laptime");
		double lastLapFuel = 0.0;
		if( laps.size() > 1 ) {
			lastLapFuel = laps.get(1).getFuelLevel() - fuelLevel;
		}

		lap = Lap.builder()
				.stintLap(stintLap)
				.lapTime(laptime)
				.driver((String) lapData.get("driver"))
				.fuelLevel(fuelLevel)
				.lastLapFuelUsage(lastLapFuel - fuelLevel)
				.no(lapNo)
				.stint(stintNo)
				.trackTemp((Double) lapData.get("trackTemp"))
				.sessionTime((Double) lapData.get("sessionTime"))
				.build();
		laps.add(0, lap);

		Stint stint = getCurrentStint();
		boolean newStint = false;
		if( stint == null ) {
			newStint = true;
		} else {
			if( stint.getNo() != stintNo ) {
				newStint = true;
			}
		}
		if( newStint ) {
			stint = Stint.builder()
					.no(stintNo)
					.laps(stintLap)
					.lastLaptime(laptime)
					.avgLapTime(laptime)
					.lastLapFuel(lastLapFuel)
					.avgFuelPerLap(lastLapFuel)
					.driver((String) lapData.get("driver"))
					.build();
			stints.add(stint);
		} else {
			stint.setLastLaptime(laptime > 0 ? laptime : 0.0);
			stint.setLastLapFuel(lastLapFuel);
			stint.setAvgLapTime(laps.stream()
					.filter(s -> s.getStint() == stintNo)
					.filter(s -> s.getLapTime() > 0)
					.mapToDouble(s -> s.getLapTime())
					.average().getAsDouble()
			);
			stint.setAvgFuelPerLap(laps.stream()
					.filter(s -> s.getNo() == stintNo)
					.filter(s -> s.getLastLapFuelUsage() > 0)
					.mapToDouble(s -> s.getLastLapFuelUsage())
					.average().getAsDouble()
			);
		}
	}

	public Stint getCurrentStint() {
		return stints.size() > 0 ? stints.get(stints.size() - 1) : null;
	}

	public void setRunData(Map<String, Object> data) {
//		_dict['fuelLevel'] = self.fuelLevel
//		_dict['flags'] = iracing.checkSessionFlags(self.flags)
//		_dict['sessionTime'] = self.sessionTime
//		_dict['estLaptime'] = self.estLaptime / 86400
		runData.setEstLapTime((Double)data.get("estLapTime"));
		runData.setFlags((String[])data.get("flags"));
		runData.setFuelLevel((Double)data.get("fuelLevel"));
		runData.setSessionTime((double)data.get("sessionTime"));

		if( runData.isGreenFlag() && this.greenFlagTime == 0.0 ) {
			this.greenFlagTime = runData.getSessionTime();
		}
	}

	public void addPitstopData(Map<String, Object> pitstopData) {
//		_pitstopData['stint'] = self.stintCount
//		_pitstopData['lap'] = self.lap
//		_pitstopData['driver'] = self.currentDriver
//		_pitstopData['enterPits'] = self.enterPits
//		_pitstopData['stopMoving'] = self.stopMoving
//		_pitstopData['startMoving'] = self.startMoving
//		_pitstopData['exitPits'] = self.exitPits
//		_pitstopData['serviceFlags'] = self.serviceFlags
//		_pitstopData['repairLeft'] = self.pitRepairLeft / 86400
//		_pitstopData['optRepairLeft'] = self.pitOptRepairLeft / 86400
//		_pitstopData['towTime'] = self.towTime / 86400

	}

	public double getRemainingSessionTime() {
		if( !sessionTime.equalsIgnoreCase("unlimited")) {
			if( greenFlagTime > 0 ) {
				return runData.getSessionTime() - greenFlagTime;
			}
			return Double.parseDouble(sessionTime) - runData.getSessionTime();
		}
		return 0.0;
	}

	private void updateStintPredictions(Stint current) {
		if( current.getAvgFuelPerLap() > 0.0 ) {
			current.setMaxLaps(maxFuel / current.getAvgFuelPerLap());
			current.setAvailableLaps(runData.getFuelLevel() / current.getAvgFuelPerLap());
		}
		current.setStintDuration(current.getMaxLaps() * current.getAvgLapTime());
	}
}
