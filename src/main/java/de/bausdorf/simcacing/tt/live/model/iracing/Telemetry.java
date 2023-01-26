package de.bausdorf.simcacing.tt.live.model.iracing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Telemetry{
    @JsonProperty("SessionTime")
    private double sessionTime;
    @JsonProperty("SessionTick")
    private int sessionTick;
    @JsonProperty("SessionNum")
    private int sessionNum;
    @JsonProperty("SessionState")
    private int sessionState;
    @JsonProperty("SessionUniqueID")
    private int sessionUniqueID;
    @JsonProperty("SessionFlags")
    private int sessionFlags;
    @JsonProperty("SessionTimeRemain")
    private double sessionTimeRemain;
    @JsonProperty("SessionLapsRemain")
    private int sessionLapsRemain;
    @JsonProperty("SessionLapsRemainEx")
    private int sessionLapsRemainEx;
    @JsonProperty("SessionTimeTotal")
    private double sessionTimeTotal;
    @JsonProperty("SessionLapsTotal")
    private int sessionLapsTotal;
    @JsonProperty("SessionJokerLapsRemain")
    private int sessionJokerLapsRemain;
    @JsonProperty("SessionOnJokerLap")
    private boolean sessionOnJokerLap;
    @JsonProperty("SessionTimeOfDay")
    private double sessionTimeOfDay;
    @JsonProperty("RadioTransmitCarIdx")
    private int radioTransmitCarIdx;
    @JsonProperty("RadioTransmitRadioIdx")
    private int radioTransmitRadioIdx;
    @JsonProperty("RadioTransmitFrequencyIdx")
    private int radioTransmitFrequencyIdx;
    @JsonProperty("DisplayUnits")
    private int displayUnits;
    @JsonProperty("DriverMarker")
    private boolean driverMarker;
    @JsonProperty("PushToPass")
    private boolean pushToPass;
    @JsonProperty("ManualBoost")
    private boolean manualBoost;
    @JsonProperty("ManualNoBoost")
    private boolean manualNoBoost;
    @JsonProperty("IsOnTrack")
    private boolean isOnTrack;
    @JsonProperty("IsReplayPlaying")
    private boolean isReplayPlaying;
    @JsonProperty("ReplayFrameNum")
    private int replayFrameNum;
    @JsonProperty("ReplayFrameNumEnd")
    private int replayFrameNumEnd;
    @JsonProperty("IsDiskLoggingEnabled")
    private boolean isDiskLoggingEnabled;
    @JsonProperty("IsDiskLoggingActive")
    private boolean isDiskLoggingActive;
    @JsonProperty("FrameRate")
    private double frameRate;
    @JsonProperty("CpuUsageFG")
    private double cpuUsageFG;
    @JsonProperty("GpuUsage")
    private double gpuUsage;
    @JsonProperty("ChanAvgLatency")
    private double chanAvgLatency;
    @JsonProperty("ChanLatency")
    private double chanLatency;
    @JsonProperty("ChanQuality")
    private double chanQuality;
    @JsonProperty("ChanPartnerQuality")
    private double chanPartnerQuality;
    @JsonProperty("CpuUsageBG")
    private double cpuUsageBG;
    @JsonProperty("ChanClockSkew")
    private double chanClockSkew;
    @JsonProperty("MemPageFaultSec")
    private double memPageFaultSec;
    @JsonProperty("MemSoftPageFaultSec")
    private double memSoftPageFaultSec;
    @JsonProperty("PlayerCarPosition")
    private int playerCarPosition;
    @JsonProperty("PlayerCarClassPosition")
    private int playerCarClassPosition;
    @JsonProperty("PlayerCarClass")
    private int playerCarClass;
    @JsonProperty("PlayerTrackSurface")
    private int playerTrackSurface;
    @JsonProperty("PlayerTrackSurfaceMaterial")
    private int playerTrackSurfaceMaterial;
    @JsonProperty("PlayerCarIdx")
    private int playerCarIdx;
    @JsonProperty("PlayerCarTeamIncidentCount")
    private int playerCarTeamIncidentCount;
    @JsonProperty("PlayerCarMyIncidentCount")
    private int playerCarMyIncidentCount;
    @JsonProperty("PlayerCarDriverIncidentCount")
    private int playerCarDriverIncidentCount;
    @JsonProperty("PlayerCarWeightPenalty")
    private double playerCarWeightPenalty;
    @JsonProperty("PlayerCarPowerAdjust")
    private double playerCarPowerAdjust;
    @JsonProperty("PlayerCarDryTireSetLimit")
    private int playerCarDryTireSetLimit;
    @JsonProperty("PlayerCarTowTime")
    private double playerCarTowTime;
    @JsonProperty("PlayerCarInPitStall")
    private boolean playerCarInPitStall;
    @JsonProperty("PlayerCarPitSvStatus")
    private int playerCarPitSvStatus;
    @JsonProperty("PlayerTireCompound")
    private int playerTireCompound;
    @JsonProperty("PlayerFastRepairsUsed")
    private int playerFastRepairsUsed;
    @JsonProperty("CarIdxLap")
    private ArrayList<Long> carIdxLap;
    @JsonProperty("CarIdxLapCompleted")
    private ArrayList<Long> carIdxLapCompleted;
    @JsonProperty("CarIdxLapDistPct")
    private ArrayList<Double> carIdxLapDistPct;
    @JsonProperty("CarIdxTrackSurface")
    private ArrayList<Long> carIdxTrackSurface;
    @JsonProperty("CarIdxTrackSurfaceMaterial")
    private ArrayList<Long> carIdxTrackSurfaceMaterial;
    @JsonProperty("CarIdxOnPitRoad")
    private ArrayList<Boolean> carIdxOnPitRoad;
    @JsonProperty("CarIdxPosition")
    private ArrayList<Long> carIdxPosition;
    @JsonProperty("CarIdxClassPosition")
    private ArrayList<Long> carIdxClassPosition;
    @JsonProperty("CarIdxClass")
    private ArrayList<Long> carIdxClass;
    @JsonProperty("CarIdxF2Time")
    private ArrayList<Double> carIdxF2Time;
    @JsonProperty("CarIdxEstTime")
    private ArrayList<Double> carIdxEstTime;
    @JsonProperty("CarIdxLastLapTime")
    private ArrayList<Double> carIdxLastLapTime;
    @JsonProperty("CarIdxBestLapTime")
    private ArrayList<Double> carIdxBestLapTime;
    @JsonProperty("CarIdxBestLapNum")
    private ArrayList<Long> carIdxBestLapNum;
    @JsonProperty("CarIdxTireCompound")
    private ArrayList<Long> carIdxTireCompound;
    @JsonProperty("CarIdxQualTireCompound")
    private ArrayList<Long> carIdxQualTireCompound;
    @JsonProperty("CarIdxQualTireCompoundLocked")
    private ArrayList<Boolean> carIdxQualTireCompoundLocked;
    @JsonProperty("CarIdxFastRepairsUsed")
    private ArrayList<Long> carIdxFastRepairsUsed;
    @JsonProperty("CarIdxSessionFlags")
    private ArrayList<Long> carIdxSessionFlags;
    @JsonProperty("PaceMode")
    private int paceMode;
    @JsonProperty("CarIdxPaceLine")
    private ArrayList<Long> carIdxPaceLine;
    @JsonProperty("CarIdxPaceRow")
    private ArrayList<Long> carIdxPaceRow;
    @JsonProperty("CarIdxPaceFlags")
    private ArrayList<Long> carIdxPaceFlags;
    @JsonProperty("OnPitRoad")
    private boolean onPitRoad;
    @JsonProperty("CarIdxSteer")
    private ArrayList<Double> carIdxSteer;
    @JsonProperty("CarIdxRPM")
    private ArrayList<Double> carIdxRPM;
    @JsonProperty("CarIdxGear")
    private ArrayList<Long> carIdxGear;
    @JsonProperty("SteeringWheelAngle")
    private double steeringWheelAngle;
    @JsonProperty("Throttle")
    private double throttle;
    @JsonProperty("Brake")
    private double brake;
    @JsonProperty("Clutch")
    private double clutch;
    @JsonProperty("Gear")
    private int gear;
    @JsonProperty("RPM")
    private double rPM;
    @JsonProperty("Lap")
    private int lap;
    @JsonProperty("LapCompleted")
    private int lapCompleted;
    @JsonProperty("LapDist")
    private double lapDist;
    @JsonProperty("LapDistPct")
    private double lapDistPct;
    @JsonProperty("RaceLaps")
    private int raceLaps;
    @JsonProperty("LapBestLap")
    private int lapBestLap;
    @JsonProperty("LapBestLapTime")
    private double lapBestLapTime;
    @JsonProperty("LapLastLapTime")
    private double lapLastLapTime;
    @JsonProperty("LapCurrentLapTime")
    private double lapCurrentLapTime;
    @JsonProperty("LapLasNLapSeq")
    private int lapLasNLapSeq;
    @JsonProperty("LapLastNLapTime")
    private double lapLastNLapTime;
    @JsonProperty("LapBestNLapLap")
    private int lapBestNLapLap;
    @JsonProperty("LapBestNLapTime")
    private double lapBestNLapTime;
    @JsonProperty("LapDeltaToBestLap")
    private double lapDeltaToBestLap;
    @JsonProperty("LapDeltaToBestLap_DD")
    private double lapDeltaToBestLap_DD;
    @JsonProperty("LapDeltaToBestLap_OK")
    private boolean lapDeltaToBestLap_OK;
    @JsonProperty("LapDeltaToOptimalLap")
    private double lapDeltaToOptimalLap;
    @JsonProperty("LapDeltaToOptimalLap_DD")
    private double lapDeltaToOptimalLap_DD;
    @JsonProperty("LapDeltaToOptimalLap_OK")
    private boolean lapDeltaToOptimalLap_OK;
    @JsonProperty("LapDeltaToSessionBestLap")
    private double lapDeltaToSessionBestLap;
    @JsonProperty("LapDeltaToSessionBestLap_DD")
    private double lapDeltaToSessionBestLap_DD;
    @JsonProperty("LapDeltaToSessionBestLap_OK")
    private boolean lapDeltaToSessionBestLap_OK;
    @JsonProperty("LapDeltaToSessionOptimalLap")
    private double lapDeltaToSessionOptimalLap;
    @JsonProperty("LapDeltaToSessionOptimalLap_DD")
    private double lapDeltaToSessionOptimalLap_DD;
    @JsonProperty("LapDeltaToSessionOptimalLap_OK")
    private boolean lapDeltaToSessionOptimalLap_OK;
    @JsonProperty("LapDeltaToSessionLastlLap")
    private double lapDeltaToSessionLastlLap;
    @JsonProperty("LapDeltaToSessionLastlLap_DD")
    private double lapDeltaToSessionLastlLap_DD;
    @JsonProperty("LapDeltaToSessionLastlLap_OK")
    private boolean lapDeltaToSessionLastlLap_OK;
    @JsonProperty("Speed")
    private double speed;
    @JsonProperty("Yaw")
    private double yaw;
    @JsonProperty("YawNorth")
    private double yawNorth;
    @JsonProperty("Pitch")
    private double pitch;
    @JsonProperty("Roll")
    private double roll;
    @JsonProperty("EnterExitReset")
    private int enterExitReset;
    @JsonProperty("TrackTemp")
    private double trackTemp;
    @JsonProperty("TrackTempCrew")
    private double trackTempCrew;
    @JsonProperty("AirTemp")
    private double airTemp;
    @JsonProperty("WeatherType")
    private int weatherType;
    @JsonProperty("Skies")
    private int skies;
    @JsonProperty("AirDensity")
    private double airDensity;
    @JsonProperty("AirPressure")
    private double airPressure;
    @JsonProperty("WindVel")
    private double windVel;
    @JsonProperty("WindDir")
    private double windDir;
    @JsonProperty("RelativeHumidity")
    private double relativeHumidity;
    @JsonProperty("FogLevel")
    private double fogLevel;
    @JsonProperty("SolarAltitude")
    private double solarAltitude;
    @JsonProperty("SolarAzimuth")
    private double solarAzimuth;
    @JsonProperty("DCLapStatus")
    private int dCLapStatus;
    @JsonProperty("DCDriversSoFar")
    private int dCDriversSoFar;
    @JsonProperty("OkToReloadTextures")
    private boolean okToReloadTextures;
    @JsonProperty("LoadNumTextures")
    private boolean loadNumTextures;
    @JsonProperty("CarLeftRight")
    private int carLeftRight;
    @JsonProperty("PitsOpen")
    private boolean pitsOpen;
    @JsonProperty("VidCapEnabled")
    private boolean vidCapEnabled;
    @JsonProperty("VidCapActive")
    private boolean vidCapActive;
    @JsonProperty("PitRepairLeft")
    private double pitRepairLeft;
    @JsonProperty("PitOptRepairLeft")
    private double pitOptRepairLeft;
    @JsonProperty("PitstopActive")
    private boolean pitstopActive;
    @JsonProperty("FastRepairUsed")
    private int fastRepairUsed;
    @JsonProperty("FastRepairAvailable")
    private int fastRepairAvailable;
    @JsonProperty("LFTiresUsed")
    private int lFTiresUsed;
    @JsonProperty("RFTiresUsed")
    private int rFTiresUsed;
    @JsonProperty("LRTiresUsed")
    private int lRTiresUsed;
    @JsonProperty("RRTiresUsed")
    private int rRTiresUsed;
    @JsonProperty("LeftTireSetsUsed")
    private int leftTireSetsUsed;
    @JsonProperty("RightTireSetsUsed")
    private int rightTireSetsUsed;
    @JsonProperty("FrontTireSetsUsed")
    private int frontTireSetsUsed;
    @JsonProperty("RearTireSetsUsed")
    private int rearTireSetsUsed;
    @JsonProperty("TireSetsUsed")
    private int tireSetsUsed;
    @JsonProperty("LFTiresAvailable")
    private int lFTiresAvailable;
    @JsonProperty("RFTiresAvailable")
    private int rFTiresAvailable;
    @JsonProperty("LRTiresAvailable")
    private int lRTiresAvailable;
    @JsonProperty("RRTiresAvailable")
    private int rRTiresAvailable;
    @JsonProperty("LeftTireSetsAvailable")
    private int leftTireSetsAvailable;
    @JsonProperty("RightTireSetsAvailable")
    private int rightTireSetsAvailable;
    @JsonProperty("FrontTireSetsAvailable")
    private int frontTireSetsAvailable;
    @JsonProperty("RearTireSetsAvailable")
    private int rearTireSetsAvailable;
    @JsonProperty("TireSetsAvailable")
    private int tireSetsAvailable;
    @JsonProperty("CamCarIdx")
    private int camCarIdx;
    @JsonProperty("CamCameraNumber")
    private int camCameraNumber;
    @JsonProperty("CamGroupNumber")
    private int camGroupNumber;
    @JsonProperty("CamCameraState")
    private int camCameraState;
    @JsonProperty("IsOnTrackCar")
    private boolean isOnTrackCar;
    @JsonProperty("IsInGarage")
    private boolean isInGarage;
    @JsonProperty("SteeringWheelAngleMax")
    private double steeringWheelAngleMax;
    @JsonProperty("ShiftPowerPct")
    private double shiftPowerPct;
    @JsonProperty("ShiftGrindRPM")
    private double shiftGrindRPM;
    @JsonProperty("ThrottleRaw")
    private double throttleRaw;
    @JsonProperty("BrakeRaw")
    private double brakeRaw;
    @JsonProperty("HandbrakeRaw")
    private double handbrakeRaw;
    @JsonProperty("BrakeABSactive")
    private boolean brakeABSactive;
    @JsonProperty("EngineWarnings")
    private int engineWarnings;
    @JsonProperty("FuelLevelPct")
    private double fuelLevelPct;
    @JsonProperty("PitSvFlags")
    private int pitSvFlags;
    @JsonProperty("PitSvLFP")
    private double pitSvLFP;
    @JsonProperty("PitSvRFP")
    private double pitSvRFP;
    @JsonProperty("PitSvLRP")
    private double pitSvLRP;
    @JsonProperty("PitSvRRP")
    private double pitSvRRP;
    @JsonProperty("PitSvFuel")
    private double pitSvFuel;
    @JsonProperty("PitSvTireCompound")
    private int pitSvTireCompound;
    @JsonProperty("CarIdxP2P_Status")
    private ArrayList<Boolean> carIdxP2P_Status;
    @JsonProperty("CarIdxP2P_Count")
    private ArrayList<Long> carIdxP2P_Count;
    @JsonProperty("SteeringWheelPctTorque")
    private double steeringWheelPctTorque;
    @JsonProperty("SteeringWheelPctTorqueSign")
    private double steeringWheelPctTorqueSign;
    @JsonProperty("SteeringWheelPctTorqueSignStops")
    private double steeringWheelPctTorqueSignStops;
    @JsonProperty("SteeringWheelPctSmoothing")
    private double steeringWheelPctSmoothing;
    @JsonProperty("SteeringWheelPctDamper")
    private double steeringWheelPctDamper;
    @JsonProperty("SteeringWheelLimiter")
    private double steeringWheelLimiter;
    @JsonProperty("SteeringWheelMaxForceNm")
    private double steeringWheelMaxForceNm;
    @JsonProperty("SteeringWheelPeakForceNm")
    private double steeringWheelPeakForceNm;
    @JsonProperty("SteeringWheelUseLinear")
    private boolean steeringWheelUseLinear;
    @JsonProperty("ShiftIndicatorPct")
    private double shiftIndicatorPct;
    @JsonProperty("ReplayPlaySpeed")
    private int replayPlaySpeed;
    @JsonProperty("ReplayPlaySlowMotion")
    private boolean replayPlaySlowMotion;
    @JsonProperty("ReplaySessionTime")
    private double replaySessionTime;
    @JsonProperty("ReplaySessionNum")
    private int replaySessionNum;
    @JsonProperty("TireLF_RumblePitch")
    private double tireLF_RumblePitch;
    @JsonProperty("TireRF_RumblePitch")
    private double tireRF_RumblePitch;
    @JsonProperty("TireLR_RumblePitch")
    private double tireLR_RumblePitch;
    @JsonProperty("TireRR_RumblePitch")
    private double tireRR_RumblePitch;
    @JsonProperty("SteeringWheelTorque_ST")
    private ArrayList<Double> steeringWheelTorque_ST;
    @JsonProperty("SteeringWheelTorque")
    private double steeringWheelTorque;
    @JsonProperty("VelocityZ_ST")
    private ArrayList<Double> velocityZ_ST;
    @JsonProperty("VelocityY_ST")
    private ArrayList<Double> velocityY_ST;
    @JsonProperty("VelocityX_ST")
    private ArrayList<Double> velocityX_ST;
    @JsonProperty("VelocityZ")
    private double velocityZ;
    @JsonProperty("VelocityY")
    private double velocityY;
    @JsonProperty("VelocityX")
    private double velocityX;
    @JsonProperty("YawRate_ST")
    private ArrayList<Double> yawRate_ST;
    @JsonProperty("PitchRate_ST")
    private ArrayList<Double> pitchRate_ST;
    @JsonProperty("RollRate_ST")
    private ArrayList<Double> rollRate_ST;
    @JsonProperty("YawRate")
    private double yawRate;
    @JsonProperty("PitchRate")
    private double pitchRate;
    @JsonProperty("RollRate")
    private double rollRate;
    @JsonProperty("VertAccel_ST")
    private ArrayList<Double> vertAccel_ST;
    @JsonProperty("LatAccel_ST")
    private ArrayList<Double> latAccel_ST;
    @JsonProperty("LongAccel_ST")
    private ArrayList<Double> longAccel_ST;
    @JsonProperty("VertAccel")
    private double vertAccel;
    @JsonProperty("LatAccel")
    private double latAccel;
    @JsonProperty("LongAccel")
    private double longAccel;
    private boolean dcStarter;
    private boolean dcPitSpeedLimiterToggle;
    private boolean dcTractionControlToggle;
    private boolean dcHeadlightFlash;
    private double dpWindshieldTearoff;
    private double dpRFTireChange;
    private double dpLFTireChange;
    private double dpRRTireChange;
    private double dpLRTireChange;
    private double dpFuelFill;
    private double dpFuelAddKg;
    private double dpFastRepair;
    private double dcAntiRollFront;
    private double dcAntiRollRear;
    private double dcBrakeBias;
    private double dpLFTireColdPress;
    private double dpRFTireColdPress;
    private double dpLRTireColdPress;
    private double dpRRTireColdPress;
    private double dcMGUKDeployMode;
    private double dcTractionControl2;
    private double dcTractionControl;
    @JsonProperty("RFbrakeLinePress")
    private double rFbrakeLinePress;
    @JsonProperty("RFcoldPressure")
    private double rFcoldPressure;
    @JsonProperty("RFtempCL")
    private double rFtempCL;
    @JsonProperty("RFtempCM")
    private double rFtempCM;
    @JsonProperty("RFtempCR")
    private double rFtempCR;
    @JsonProperty("RFwearL")
    private double rFwearL;
    @JsonProperty("RFwearM")
    private double rFwearM;
    @JsonProperty("RFwearR")
    private double rFwearR;
    @JsonProperty("LFbrakeLinePress")
    private double lFbrakeLinePress;
    @JsonProperty("LFcoldPressure")
    private double lFcoldPressure;
    @JsonProperty("LFtempCL")
    private double lFtempCL;
    @JsonProperty("LFtempCM")
    private double lFtempCM;
    @JsonProperty("LFtempCR")
    private double lFtempCR;
    @JsonProperty("LFwearL")
    private double lFwearL;
    @JsonProperty("LFwearM")
    private double lFwearM;
    @JsonProperty("LFwearR")
    private double lFwearR;
    @JsonProperty("HFshockDefl")
    private double hFshockDefl;
    @JsonProperty("HFshockDefl_ST")
    private ArrayList<Double> hFshockDefl_ST;
    @JsonProperty("HFshockVel")
    private double hFshockVel;
    @JsonProperty("HFshockVel_ST")
    private ArrayList<Double> hFshockVel_ST;
    @JsonProperty("FuelUsePerHour")
    private double fuelUsePerHour;
    @JsonProperty("Voltage")
    private double voltage;
    @JsonProperty("WaterTemp")
    private double waterTemp;
    @JsonProperty("WaterLevel")
    private double waterLevel;
    @JsonProperty("FuelPress")
    private double fuelPress;
    @JsonProperty("OilTemp")
    private double oilTemp;
    @JsonProperty("OilPress")
    private double oilPress;
    @JsonProperty("OilLevel")
    private double oilLevel;
    @JsonProperty("ManifoldPress")
    private double manifoldPress;
    @JsonProperty("PowerMGU_K")
    private double powerMGU_K;
    @JsonProperty("TorqueMGU_K")
    private double torqueMGU_K;
    @JsonProperty("PowerMGU_H")
    private double powerMGU_H;
    @JsonProperty("EnergyERSBattery")
    private double energyERSBattery;
    @JsonProperty("EnergyERSBatteryPct")
    private double energyERSBatteryPct;
    @JsonProperty("EnergyBatteryToMGU_KLap")
    private double energyBatteryToMGU_KLap;
    @JsonProperty("EnergyMGU_KLapDeployPct")
    private double energyMGU_KLapDeployPct;
    @JsonProperty("FuelLevel")
    private double fuelLevel;
    @JsonProperty("Engine0_RPM")
    private double engine0_RPM;
    @JsonProperty("RRbrakeLinePress")
    private double rRbrakeLinePress;
    @JsonProperty("RRcoldPressure")
    private double rRcoldPressure;
    @JsonProperty("RRtempCL")
    private double rRtempCL;
    @JsonProperty("RRtempCM")
    private double rRtempCM;
    @JsonProperty("RRtempCR")
    private double rRtempCR;
    @JsonProperty("RRwearL")
    private double rRwearL;
    @JsonProperty("RRwearM")
    private double rRwearM;
    @JsonProperty("RRwearR")
    private double rRwearR;
    @JsonProperty("LRbrakeLinePress")
    private double lRbrakeLinePress;
    @JsonProperty("LRcoldPressure")
    private double lRcoldPressure;
    @JsonProperty("LRtempCL")
    private double lRtempCL;
    @JsonProperty("LRtempCM")
    private double lRtempCM;
    @JsonProperty("LRtempCR")
    private double lRtempCR;
    @JsonProperty("LRwearL")
    private double lRwearL;
    @JsonProperty("LRwearM")
    private double lRwearM;
    @JsonProperty("LRwearR")
    private double lRwearR;
    @JsonProperty("RRshockDefl")
    private double rRshockDefl;
    @JsonProperty("RRshockDefl_ST")
    private ArrayList<Double> rRshockDefl_ST;
    @JsonProperty("RRshockVel")
    private double rRshockVel;
    @JsonProperty("RRshockVel_ST")
    private ArrayList<Double> rRshockVel_ST;
    @JsonProperty("LRshockDefl")
    private double lRshockDefl;
    @JsonProperty("LRshockDefl_ST")
    private ArrayList<Double> lRshockDefl_ST;
    @JsonProperty("LRshockVel")
    private double lRshockVel;
    @JsonProperty("LRshockVel_ST")
    private ArrayList<Double> lRshockVel_ST;
    @JsonProperty("RFshockDefl")
    private double rFshockDefl;
    @JsonProperty("RFshockDefl_ST")
    private ArrayList<Double> rFshockDefl_ST;
    @JsonProperty("RFshockVel")
    private double rFshockVel;
    @JsonProperty("RFshockVel_ST")
    private ArrayList<Double> rFshockVel_ST;
    @JsonProperty("LFshockDefl")
    private double lFshockDefl;
    @JsonProperty("LFshockDefl_ST")
    private ArrayList<Double> lFshockDefl_ST;
    @JsonProperty("LFshockVel")
    private double lFshockVel;
    @JsonProperty("LFshockVel_ST")
    private ArrayList<Double> lFshockVel_ST;
    @JsonProperty("TickCount")
    private int tickCount;
    @JsonProperty("TractionControlSwitch")
    private int tractionControlSwitch;
    @JsonProperty("TractionControlSetting")
    private double tractionControlSetting;
}

