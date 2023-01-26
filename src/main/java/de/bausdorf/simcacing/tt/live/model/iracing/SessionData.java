package de.bausdorf.simcacing.tt.live.model.iracing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class SessionData{
    @JsonProperty("InfoUpdate")
    private int infoUpdate;
    @JsonProperty("WeekendInfo")
    private WeekendInfo weekendInfo;
    @JsonProperty("SessionInfo")
    private SessionInfo sessionInfo;
    @JsonProperty("CameraInfo")
    private CameraInfo cameraInfo;
    @JsonProperty("RadioInfo")
    private RadioInfo radioInfo;
    @JsonProperty("DriverInfo")
    private DriverInfo driverInfo;
    @JsonProperty("SplitTimeInfo")
    private SplitTimeInfo splitTimeInfo;
    @JsonProperty("CarSetup")
    private CarSetup carSetup;
    @JsonProperty("Dictionnary")
    private Dictionnary dictionnary;

    @Data
    @NoArgsConstructor
    public  static class AeroCalculator{
        @JsonProperty("FrontRhAtSpeed")
        private String frontRhAtSpeed;
        @JsonProperty("RearRhAtSpeed")
        private String rearRhAtSpeed;
        @JsonProperty("DownforceDelta_Points")
        private String downforceDelta_Points;
        @JsonProperty("DragDelta_Points")
        private String dragDelta_Points;
        @JsonProperty("DownforceBalance")
        private String downforceBalance;
        @JsonProperty("LD")
        private String lD;
    }

    @Data
    @NoArgsConstructor
    public  static class AeroSettings{
        @JsonProperty("RearWingAngle")
        private String rearWingAngle;
    }

    @Data
    @NoArgsConstructor
    public  static class BrakesDriveUnit{
        @JsonProperty("Lighting")
        private Lighting lighting;
        @JsonProperty("BrakeSpec")
        private BrakeSpec brakeSpec;
        @JsonProperty("HybridConfig")
        private HybridConfig hybridConfig;
        @JsonProperty("Fuel")
        private Fuel fuel;
        @JsonProperty("TractionControl")
        private TractionControl tractionControl;
        @JsonProperty("GearRatios")
        private GearRatios gearRatios;
        @JsonProperty("RearDiffSpec")
        private RearDiffSpec rearDiffSpec;
    }

    @Data
    @NoArgsConstructor
    public  static class BrakeSpec{
        @JsonProperty("PadCompound")
        private String padCompound;
        @JsonProperty("FrontMasterCyl")
        private String frontMasterCyl;
        @JsonProperty("RearMasterCyl")
        private String rearMasterCyl;
        @JsonProperty("BrakePressureBias")
        private String brakePressureBias;
    }

    @Data
    @NoArgsConstructor
    public  static class Camera{
        @JsonProperty("CameraNum")
        private int cameraNum;
        @JsonProperty("CameraName")
        private String cameraName;
    }

    @Data
    @NoArgsConstructor
    public  static class CameraInfo{
        @JsonProperty("Groups")
        private ArrayList<Group> groups;
    }

    @Data
    @NoArgsConstructor
    public  static class CarSetup{
        @JsonProperty("Chassis")
        private Chassis chassis;
        @JsonProperty("UpdateCount")
        private String updateCount;
        @JsonProperty("TiresAero")
        private TiresAero tiresAero;
        @JsonProperty("BrakesDriveUnit")
        private BrakesDriveUnit brakesDriveUnit;
    }

    @Data
    @NoArgsConstructor
    public  static class Chassis{
        @JsonProperty("InCarDials")
        private Object inCarDials;
        @JsonProperty("Front")
        private Front front;
        @JsonProperty("LeftFront")
        private LeftFront leftFront;
        @JsonProperty("LeftRear")
        private LeftRear leftRear;
        @JsonProperty("RightFront")
        private RightFront rightFront;
        @JsonProperty("RightRear")
        private RightRear rightRear;
        @JsonProperty("Rear")
        private Rear rear;
    }

    @Data
    @NoArgsConstructor
    public  static class CompetingDriver{
        @JsonProperty("ClubName")
        private String clubName;
        @JsonProperty("CarIdx")
        private int carIdx;
        @JsonProperty("UserName")
        private String userName;
        @JsonProperty("AbbrevName")
        private String abbrevName;
        @JsonProperty("Initials")
        private String initials;
        @JsonProperty("UserID")
        private int userID;
        @JsonProperty("TeamID")
        private int teamID;
        @JsonProperty("TeamName")
        private String teamName;
        @JsonProperty("CarNumber")
        private String carNumber;
        @JsonProperty("CarNumberRaw")
        private int carNumberRaw;
        @JsonProperty("CarPath")
        private String carPath;
        @JsonProperty("CarClassID")
        private int carClassID;
        @JsonProperty("CarID")
        private int carID;
        @JsonProperty("CarIsPaceCar")
        private int carIsPaceCar;
        @JsonProperty("CarIsAI")
        private int carIsAI;
        @JsonProperty("CarScreenName")
        private String carScreenName;
        @JsonProperty("CarScreenNameShort")
        private String carScreenNameShort;
        @JsonProperty("CarClassShortName")
        private String carClassShortName;
        @JsonProperty("CarClassRelSpeed")
        private int carClassRelSpeed;
        @JsonProperty("CarClassLicenseLevel")
        private int carClassLicenseLevel;
        @JsonProperty("CarClassMaxFuel")
        private String carClassMaxFuel;
        @JsonProperty("CarClassMaxFuelPct")
        private String carClassMaxFuelPct;
        @JsonProperty("CarClassWeightPenalty")
        private String carClassWeightPenalty;
        @JsonProperty("CarClassColor")
        private String carClassColor;
        @JsonProperty("CarClassEstLapTime")
        private double carClassEstLapTime;
        @JsonProperty("IRating")
        private int iRating;
        @JsonProperty("LicLevel")
        private int licLevel;
        @JsonProperty("LicSubLevel")
        private int licSubLevel;
        @JsonProperty("LicString")
        private String licString;
        @JsonProperty("LicColor")
        private String licColor;
        @JsonProperty("IsSpectator")
        private int isSpectator;
        @JsonProperty("CarDesignStr")
        private String carDesignStr;
        @JsonProperty("HelmetDesignStr")
        private String helmetDesignStr;
        @JsonProperty("SuitDesignStr")
        private String suitDesignStr;
        @JsonProperty("CarNumberDesignStr")
        private String carNumberDesignStr;
        @JsonProperty("CarSponsor_1")
        private int carSponsor_1;
        @JsonProperty("CarSponsor_2")
        private int carSponsor_2;
        @JsonProperty("IsPaceCar")
        private boolean isPaceCar;
    }

    @Data
    @NoArgsConstructor
    public  static class Dictionnary{
        @JsonProperty("WeekendInfo")
        private WeekendInfo weekendInfo;
        @JsonProperty("SessionInfo")
        private SessionInfo sessionInfo;
        @JsonProperty("CameraInfo")
        private CameraInfo cameraInfo;
        @JsonProperty("RadioInfo")
        private RadioInfo radioInfo;
        @JsonProperty("DriverInfo")
        private DriverInfo driverInfo;
        @JsonProperty("SplitTimeInfo")
        private SplitTimeInfo splitTimeInfo;
        @JsonProperty("CarSetup")
        private CarSetup carSetup;
    }

    @Data
    @NoArgsConstructor
    public  static class Driver{
        @JsonProperty("ClubName")
        private String clubName;
        @JsonProperty("CarIdx")
        private int carIdx;
        @JsonProperty("UserName")
        private String userName;
        @JsonProperty("AbbrevName")
        private String abbrevName;
        @JsonProperty("Initials")
        private String initials;
        @JsonProperty("UserID")
        private int userID;
        @JsonProperty("TeamID")
        private int teamID;
        @JsonProperty("TeamName")
        private String teamName;
        @JsonProperty("CarNumber")
        private String carNumber;
        @JsonProperty("CarNumberRaw")
        private int carNumberRaw;
        @JsonProperty("CarPath")
        private String carPath;
        @JsonProperty("CarClassID")
        private int carClassID;
        @JsonProperty("CarID")
        private int carID;
        @JsonProperty("CarIsPaceCar")
        private int carIsPaceCar;
        @JsonProperty("CarIsAI")
        private int carIsAI;
        @JsonProperty("CarScreenName")
        private String carScreenName;
        @JsonProperty("CarScreenNameShort")
        private String carScreenNameShort;
        @JsonProperty("CarClassShortName")
        private String carClassShortName;
        @JsonProperty("CarClassRelSpeed")
        private int carClassRelSpeed;
        @JsonProperty("CarClassLicenseLevel")
        private int carClassLicenseLevel;
        @JsonProperty("CarClassMaxFuel")
        private Object carClassMaxFuel;
        @JsonProperty("CarClassMaxFuelPct")
        private String carClassMaxFuelPct;
        @JsonProperty("CarClassWeightPenalty")
        private String carClassWeightPenalty;
        @JsonProperty("CarClassColor")
        private String carClassColor;
        @JsonProperty("CarClassEstLapTime")
        private double carClassEstLapTime;
        @JsonProperty("IRating")
        private int iRating;
        @JsonProperty("LicLevel")
        private int licLevel;
        @JsonProperty("LicSubLevel")
        private int licSubLevel;
        @JsonProperty("LicString")
        private String licString;
        @JsonProperty("LicColor")
        private String licColor;
        @JsonProperty("IsSpectator")
        private int isSpectator;
        @JsonProperty("CarDesignStr")
        private String carDesignStr;
        @JsonProperty("HelmetDesignStr")
        private String helmetDesignStr;
        @JsonProperty("SuitDesignStr")
        private String suitDesignStr;
        @JsonProperty("CarNumberDesignStr")
        private String carNumberDesignStr;
        @JsonProperty("CarSponsor_1")
        private int carSponsor_1;
        @JsonProperty("CarSponsor_2")
        private int carSponsor_2;
        @JsonProperty("IsPaceCar")
        private boolean isPaceCar;
        @JsonProperty("CarIsElectric")
        private String carIsElectric;
        @JsonProperty("CarClassPowerAdjust")
        private String carClassPowerAdjust;
        @JsonProperty("CarClassDryTireSetLimit")
        private String carClassDryTireSetLimit;
        @JsonProperty("ClubID")
        private String clubID;
        @JsonProperty("DivisionName")
        private String divisionName;
        @JsonProperty("DivisionID")
        private String divisionID;
        @JsonProperty("CurDriverIncidentCount")
        private String curDriverIncidentCount;
        @JsonProperty("TeamIncidentCount")
        private String teamIncidentCount;
    }

    @Data
    @NoArgsConstructor
    public  static class DriverInfo{
        @JsonProperty("DriverCarIdx")
        private int driverCarIdx;
        @JsonProperty("PaceCarIdx")
        private int paceCarIdx;
        @JsonProperty("DriverHeadPosX")
        private double driverHeadPosX;
        @JsonProperty("DriverHeadPosY")
        private double driverHeadPosY;
        @JsonProperty("DriverHeadPosZ")
        private double driverHeadPosZ;
        @JsonProperty("DriverCarIdleRPM")
        private double driverCarIdleRPM;
        @JsonProperty("DriverCarRedLine")
        private double driverCarRedLine;
        @JsonProperty("DriverCarFuelKgPerLtr")
        private double driverCarFuelKgPerLtr;
        @JsonProperty("DriverCarFuelMaxLtr")
        private double driverCarFuelMaxLtr;
        @JsonProperty("DriverCarMaxFuelPct")
        private double driverCarMaxFuelPct;
        @JsonProperty("DriverCarSLFirstRPM")
        private double driverCarSLFirstRPM;
        @JsonProperty("DriverCarSLShiftRPM")
        private double driverCarSLShiftRPM;
        @JsonProperty("DriverCarSLLastRPM")
        private double driverCarSLLastRPM;
        @JsonProperty("DriverCarSLBlinkRPM")
        private double driverCarSLBlinkRPM;
        @JsonProperty("DriverPitTrkPct")
        private double driverPitTrkPct;
        @JsonProperty("DriverCarEstLapTime")
        private double driverCarEstLapTime;
        @JsonProperty("DriverSetupName")
        private String driverSetupName;
        @JsonProperty("DriverSetupIsModified")
        private int driverSetupIsModified;
        @JsonProperty("DriverSetupLoadTypeName")
        private String driverSetupLoadTypeName;
        @JsonProperty("DriverSetupPassedTech")
        private int driverSetupPassedTech;
        @JsonProperty("Drivers")
        private ArrayList<Driver> drivers;
        @JsonProperty("CompetingDrivers")
        private ArrayList<CompetingDriver> competingDrivers;
        @JsonProperty("DriverUserID")
        private String driverUserID;
        @JsonProperty("DriverCarIsElectric")
        private String driverCarIsElectric;
        @JsonProperty("DriverCarEngCylinderCount")
        private String driverCarEngCylinderCount;
        @JsonProperty("DriverCarGearNumForward")
        private String driverCarGearNumForward;
        @JsonProperty("DriverCarGearNeutral")
        private String driverCarGearNeutral;
        @JsonProperty("DriverCarGearReverse")
        private String driverCarGearReverse;
        @JsonProperty("DriverCarVersion")
        private String driverCarVersion;
        @JsonProperty("DriverIncidentCount")
        private String driverIncidentCount;
    }

    @Data
    @NoArgsConstructor
    public  static class Frequency{
        @JsonProperty("FrequencyNum")
        private int frequencyNum;
        @JsonProperty("FrequencyName")
        private String frequencyName;
        @JsonProperty("Priority")
        private int priority;
        @JsonProperty("CarIdx")
        private int carIdx;
        @JsonProperty("EntryIdx")
        private int entryIdx;
        @JsonProperty("ClubID")
        private int clubID;
        @JsonProperty("CanScan")
        private int canScan;
        @JsonProperty("CanSquawk")
        private int canSquawk;
        @JsonProperty("Muted")
        private int muted;
        @JsonProperty("IsMutable")
        private int isMutable;
        @JsonProperty("IsDeletable")
        private int isDeletable;
    }

    @Data
    @NoArgsConstructor
    public  static class Front{
        @JsonProperty("HeaveSpring")
        private String heaveSpring;
        @JsonProperty("HeavePerchOffset")
        private String heavePerchOffset;
        @JsonProperty("HeaveSpringDefl")
        private String heaveSpringDefl;
        @JsonProperty("HeaveSliderDefl")
        private String heaveSliderDefl;
        @JsonProperty("ArbSize")
        private String arbSize;
        @JsonProperty("ArbBlades")
        private String arbBlades;
        @JsonProperty("ToeIn")
        private String toeIn;
        @JsonProperty("PushrodLengthOffset")
        private String pushrodLengthOffset;
    }

    @Data
    @NoArgsConstructor
    public  static class Fuel{
        @JsonProperty("FuelLevel")
        private String fuelLevel;
    }

    @Data
    @NoArgsConstructor
    public  static class GearRatios{
        @JsonProperty("GearStack")
        private String gearStack;
        @JsonProperty("SpeedInFirst")
        private String speedInFirst;
        @JsonProperty("SpeedInSecond")
        private String speedInSecond;
        @JsonProperty("SpeedInThird")
        private String speedInThird;
        @JsonProperty("SpeedInFourth")
        private String speedInFourth;
        @JsonProperty("SpeedInFifth")
        private String speedInFifth;
        @JsonProperty("SpeedInSixth")
        private String speedInSixth;
        @JsonProperty("SpeedInSeventh")
        private String speedInSeventh;
    }

    @Data
    @NoArgsConstructor
    public  static class Group{
        @JsonProperty("GroupNum")
        private int groupNum;
        @JsonProperty("GroupName")
        private String groupName;
        @JsonProperty("Cameras")
        private ArrayList<Camera> cameras;
    }

    @Data
    @NoArgsConstructor
    public  static class HybridConfig{
        @JsonProperty("MguKDeployMode")
        private String mguKDeployMode;
    }

    @Data
    @NoArgsConstructor
    public  static class LeftFront{
        @JsonProperty("CornerWeight")
        private String cornerWeight;
        @JsonProperty("RideHeight")
        private String rideHeight;
        @JsonProperty("ShockDefl")
        private String shockDefl;
        @JsonProperty("TorsionBarDefl")
        private String torsionBarDefl;
        @JsonProperty("TorsionBarTurns")
        private String torsionBarTurns;
        @JsonProperty("TorsionBarOD")
        private String torsionBarOD;
        @JsonProperty("LsCompDamping")
        private String lsCompDamping;
        @JsonProperty("HsCompDamping")
        private String hsCompDamping;
        @JsonProperty("HsCompDampSlope")
        private String hsCompDampSlope;
        @JsonProperty("LsRbdDamping")
        private String lsRbdDamping;
        @JsonProperty("HsRbdDamping")
        private String hsRbdDamping;
        @JsonProperty("Camber")
        private String camber;
    }

    @Data
    @NoArgsConstructor
    public  static class LeftFrontTire{
        @JsonProperty("StartingPressure")
        private String startingPressure;
        @JsonProperty("LastHotPressure")
        private String lastHotPressure;
        @JsonProperty("LastTempsOMI")
        private String lastTempsOMI;
        @JsonProperty("TreadRemaining")
        private String treadRemaining;
    }

    @Data
    @NoArgsConstructor
    public  static class LeftRear{
        @JsonProperty("CornerWeight")
        private String cornerWeight;
        @JsonProperty("RideHeight")
        private String rideHeight;
        @JsonProperty("ShockDefl")
        private String shockDefl;
        @JsonProperty("SpringDefl")
        private String springDefl;
        @JsonProperty("SpringPerchOffset")
        private String springPerchOffset;
        @JsonProperty("SpringRate")
        private String springRate;
        @JsonProperty("LsCompDamping")
        private String lsCompDamping;
        @JsonProperty("HsCompDamping")
        private String hsCompDamping;
        @JsonProperty("HsCompDampSlope")
        private String hsCompDampSlope;
        @JsonProperty("LsRbdDamping")
        private String lsRbdDamping;
        @JsonProperty("HsRbdDamping")
        private String hsRbdDamping;
        @JsonProperty("Camber")
        private String camber;
        @JsonProperty("ToeIn")
        private String toeIn;
    }

    @Data
    @NoArgsConstructor
    public  static class LeftRearTire{
        @JsonProperty("StartingPressure")
        private String startingPressure;
        @JsonProperty("LastHotPressure")
        private String lastHotPressure;
        @JsonProperty("LastTempsOMI")
        private String lastTempsOMI;
        @JsonProperty("TreadRemaining")
        private String treadRemaining;
    }

    @Data
    @NoArgsConstructor
    public  static class Lighting{
        @JsonProperty("RoofIdLightColor")
        private String roofIdLightColor;
    }

    @Data
    @NoArgsConstructor
    public  static class Radio{
        @JsonProperty("RadioNum")
        private int radioNum;
        @JsonProperty("HopCount")
        private int hopCount;
        @JsonProperty("NumFrequencies")
        private int numFrequencies;
        @JsonProperty("TunedToFrequencyNum")
        private int tunedToFrequencyNum;
        @JsonProperty("ScanningIsOn")
        private int scanningIsOn;
        @JsonProperty("Frequencies")
        private ArrayList<Frequency> frequencies;
    }

    @Data
    @NoArgsConstructor
    public  static class RadioInfo{
        @JsonProperty("SelectedRadioNum")
        private int selectedRadioNum;
        @JsonProperty("Radios")
        private ArrayList<Radio> radios;
    }

    @Data
    @NoArgsConstructor
    public  static class Rear{
        @JsonProperty("ThirdSpring")
        private String thirdSpring;
        @JsonProperty("ThirdPerchOffset")
        private String thirdPerchOffset;
        @JsonProperty("ThirdSpringDefl")
        private String thirdSpringDefl;
        @JsonProperty("ThirdSliderDefl")
        private String thirdSliderDefl;
        @JsonProperty("ArbSize")
        private String arbSize;
        @JsonProperty("ArbBlades")
        private String arbBlades;
        @JsonProperty("PushrodLengthOffset")
        private String pushrodLengthOffset;
        @JsonProperty("CrossWeight")
        private String crossWeight;
    }

    @Data
    @NoArgsConstructor
    public  static class RearDiffSpec{
        @JsonProperty("CoastDriveRampAngles")
        private String coastDriveRampAngles;
        @JsonProperty("ClutchFrictionPlates")
        private String clutchFrictionPlates;
        @JsonProperty("Preload")
        private String preload;
    }

    @Data
    @NoArgsConstructor
    public  static class ResultsFastestLap{
        @JsonProperty("CarIdx")
        private int carIdx;
        @JsonProperty("FastestLap")
        private int fastestLap;
        @JsonProperty("FastestTime")
        private double fastestTime;
    }

    @Data
    @NoArgsConstructor
    public  static class ResultsPosition{
        @JsonProperty("Position")
        private int position;
        @JsonProperty("ClassPosition")
        private int classPosition;
        @JsonProperty("CarIdx")
        private int carIdx;
        @JsonProperty("Lap")
        private int lap;
        @JsonProperty("Time")
        private double time;
        @JsonProperty("FastestLap")
        private int fastestLap;
        @JsonProperty("FastestTime")
        private double fastestTime;
        @JsonProperty("LastTime")
        private double lastTime;
        @JsonProperty("LapsLed")
        private int lapsLed;
        @JsonProperty("LapsComplete")
        private int lapsComplete;
        @JsonProperty("LapsDriven")
        private double lapsDriven;
        @JsonProperty("Incidents")
        private int incidents;
        @JsonProperty("ReasonOutId")
        private int reasonOutId;
        @JsonProperty("ReasonOutStr")
        private String reasonOutStr;
        @JsonProperty("JokerLapsComplete")
        private int jokerLapsComplete;
    }

    @Data
    @NoArgsConstructor
    public  static class RightFront{
        @JsonProperty("CornerWeight")
        private String cornerWeight;
        @JsonProperty("RideHeight")
        private String rideHeight;
        @JsonProperty("ShockDefl")
        private String shockDefl;
        @JsonProperty("TorsionBarDefl")
        private String torsionBarDefl;
        @JsonProperty("TorsionBarTurns")
        private String torsionBarTurns;
        @JsonProperty("TorsionBarOD")
        private String torsionBarOD;
        @JsonProperty("LsCompDamping")
        private String lsCompDamping;
        @JsonProperty("HsCompDamping")
        private String hsCompDamping;
        @JsonProperty("HsCompDampSlope")
        private String hsCompDampSlope;
        @JsonProperty("LsRbdDamping")
        private String lsRbdDamping;
        @JsonProperty("HsRbdDamping")
        private String hsRbdDamping;
        @JsonProperty("Camber")
        private String camber;
    }

    @Data
    @NoArgsConstructor
    public  static class RightFrontTire{
        @JsonProperty("StartingPressure")
        private String startingPressure;
        @JsonProperty("LastHotPressure")
        private String lastHotPressure;
        @JsonProperty("LastTempsIMO")
        private String lastTempsIMO;
        @JsonProperty("TreadRemaining")
        private String treadRemaining;
    }

    @Data
    @NoArgsConstructor
    public  static class RightRear{
        @JsonProperty("CornerWeight")
        private String cornerWeight;
        @JsonProperty("RideHeight")
        private String rideHeight;
        @JsonProperty("ShockDefl")
        private String shockDefl;
        @JsonProperty("SpringDefl")
        private String springDefl;
        @JsonProperty("SpringPerchOffset")
        private String springPerchOffset;
        @JsonProperty("SpringRate")
        private String springRate;
        @JsonProperty("LsCompDamping")
        private String lsCompDamping;
        @JsonProperty("HsCompDamping")
        private String hsCompDamping;
        @JsonProperty("HsCompDampSlope")
        private String hsCompDampSlope;
        @JsonProperty("LsRbdDamping")
        private String lsRbdDamping;
        @JsonProperty("HsRbdDamping")
        private String hsRbdDamping;
        @JsonProperty("Camber")
        private String camber;
        @JsonProperty("ToeIn")
        private String toeIn;
    }

    @Data
    @NoArgsConstructor
    public  static class RightRearTire{
        @JsonProperty("StartingPressure")
        private String startingPressure;
        @JsonProperty("LastHotPressure")
        private String lastHotPressure;
        @JsonProperty("LastTempsIMO")
        private String lastTempsIMO;
        @JsonProperty("TreadRemaining")
        private String treadRemaining;
    }

    @Data
    @NoArgsConstructor
    public  static class Sector{
        @JsonProperty("SectorNum")
        private int sectorNum;
        @JsonProperty("SectorStartPct")
        private double sectorStartPct;
    }

    @Data
    @NoArgsConstructor
    public  static class Session{
        @JsonProperty("SessionNum")
        private int sessionNum;
        @JsonProperty("SessionLaps")
        private String sessionLaps;
        @JsonProperty("SessionTime")
        private String sessionTime;
        @JsonProperty("SessionNumLapsToAvg")
        private int sessionNumLapsToAvg;
        @JsonProperty("SessionType")
        private String sessionType;
        @JsonProperty("SessionTrackRubberState")
        private String sessionTrackRubberState;
        @JsonProperty("ResultsPositions")
        private ArrayList<ResultsPosition> resultsPositions;
        @JsonProperty("ResultsFastestLap")
        private ArrayList<ResultsFastestLap> resultsFastestLap;
        @JsonProperty("ResultsAverageLapTime")
        private double resultsAverageLapTime;
        @JsonProperty("ResultsNumCautionFlags")
        private int resultsNumCautionFlags;
        @JsonProperty("ResultsNumCautionLaps")
        private int resultsNumCautionLaps;
        @JsonProperty("ResultsNumLeadChanges")
        private int resultsNumLeadChanges;
        @JsonProperty("ResultsLapsComplete")
        private int resultsLapsComplete;
        @JsonProperty("ResultsOfficial")
        private int resultsOfficial;
        @JsonProperty("IsRace")
        private boolean isRace;
        private int _SessionLaps;
        private double _SessionTime;
        @JsonProperty("IsLimitedSessionLaps")
        private boolean isLimitedSessionLaps;
        @JsonProperty("IsLimitedTime")
        private boolean isLimitedTime;
        @JsonProperty("SessionName")
        private String sessionName;
        @JsonProperty("SessionSubType")
        private Object sessionSubType;
        @JsonProperty("SessionSkipped")
        private String sessionSkipped;
        @JsonProperty("SessionRunGroupsUsed")
        private String sessionRunGroupsUsed;
        @JsonProperty("SessionEnforceTireCompoundChange")
        private String sessionEnforceTireCompoundChange;
    }

    @Data
    @NoArgsConstructor
    public  static class SessionInfo{
        @JsonProperty("Sessions")
        private ArrayList<Session> sessions;
    }

    @Data
    @NoArgsConstructor
    public  static class SplitTimeInfo{
        @JsonProperty("Sectors")
        private ArrayList<Sector> sectors;
    }

    @Data
    @NoArgsConstructor
    public  static class TelemetryOptions{
        @JsonProperty("TelemetryDiskFile")
        private String telemetryDiskFile;
    }

    @Data
    @NoArgsConstructor
    public  static class TiresAero{
        @JsonProperty("LeftFrontTire")
        private LeftFrontTire leftFrontTire;
        @JsonProperty("LeftRearTire")
        private LeftRearTire leftRearTire;
        @JsonProperty("RightFrontTire")
        private RightFrontTire rightFrontTire;
        @JsonProperty("RightRearTire")
        private RightRearTire rightRearTire;
        @JsonProperty("AeroSettings")
        private AeroSettings aeroSettings;
        @JsonProperty("AeroCalculator")
        private AeroCalculator aeroCalculator;
    }

    @Data
    @NoArgsConstructor
    public  static class TractionControl{
        @JsonProperty("TractionControlGain")
        private String tractionControlGain;
        @JsonProperty("TractionControlSlip")
        private String tractionControlSlip;
    }

    @Data
    @NoArgsConstructor
    public  static class WeekendInfo{
        @JsonProperty("TrackName")
        private String trackName;
        @JsonProperty("TrackID")
        private int trackID;
        @JsonProperty("TrackLength")
        private String trackLength;
        @JsonProperty("TrackDisplayName")
        private String trackDisplayName;
        @JsonProperty("TrackDisplayShortName")
        private String trackDisplayShortName;
        @JsonProperty("TrackConfigName")
        private String trackConfigName;
        @JsonProperty("TrackCity")
        private String trackCity;
        @JsonProperty("TrackCountry")
        private String trackCountry;
        @JsonProperty("TrackAltitude")
        private String trackAltitude;
        @JsonProperty("TrackLatitude")
        private String trackLatitude;
        @JsonProperty("TrackLongitude")
        private String trackLongitude;
        @JsonProperty("TrackNorthOffset")
        private String trackNorthOffset;
        @JsonProperty("TrackNumTurns")
        private int trackNumTurns;
        @JsonProperty("TrackPitSpeedLimit")
        private String trackPitSpeedLimit;
        @JsonProperty("TrackType")
        private String trackType;
        @JsonProperty("TrackWeatherType")
        private String trackWeatherType;
        @JsonProperty("TrackSkies")
        private String trackSkies;
        @JsonProperty("TrackSurfaceTemp")
        private String trackSurfaceTemp;
        @JsonProperty("TrackAirTemp")
        private String trackAirTemp;
        @JsonProperty("TrackAirPressure")
        private String trackAirPressure;
        @JsonProperty("TrackWindVel")
        private String trackWindVel;
        @JsonProperty("TrackWindDir")
        private String trackWindDir;
        @JsonProperty("TrackRelativeHumidity")
        private String trackRelativeHumidity;
        @JsonProperty("TrackFogLevel")
        private String trackFogLevel;
        @JsonProperty("TrackCleanup")
        private int trackCleanup;
        @JsonProperty("TrackDynamicTrack")
        private int trackDynamicTrack;
        @JsonProperty("SeriesID")
        private int seriesID;
        @JsonProperty("SeasonID")
        private int seasonID;
        @JsonProperty("SessionID")
        private int sessionID;
        @JsonProperty("SubSessionID")
        private int subSessionID;
        @JsonProperty("LeagueID")
        private int leagueID;
        @JsonProperty("Official")
        private int official;
        @JsonProperty("RaceWeek")
        private int raceWeek;
        @JsonProperty("EventType")
        private String eventType;
        @JsonProperty("Category")
        private String category;
        @JsonProperty("SimMode")
        private String simMode;
        @JsonProperty("TeamRacing")
        private int teamRacing;
        @JsonProperty("MinDrivers")
        private int minDrivers;
        @JsonProperty("MaxDrivers")
        private int maxDrivers;
        @JsonProperty("DCRuleSet")
        private String dCRuleSet;
        @JsonProperty("QualifierMustStartRace")
        private int qualifierMustStartRace;
        @JsonProperty("NumCarClasses")
        private int numCarClasses;
        @JsonProperty("NumCarTypes")
        private int numCarTypes;
        @JsonProperty("WeekendOptions")
        private WeekendOptions weekendOptions;
        @JsonProperty("TelemetryOptions")
        private TelemetryOptions telemetryOptions;
        @JsonProperty("TrackLengthOfficial")
        private String trackLengthOfficial;
        @JsonProperty("TrackDirection")
        private String trackDirection;
        @JsonProperty("TrackVersion")
        private String trackVersion;
        @JsonProperty("HeatRacing")
        private String heatRacing;
        @JsonProperty("BuildType")
        private String buildType;
        @JsonProperty("BuildTarget")
        private String buildTarget;
        @JsonProperty("BuildVersion")
        private String buildVersion;
    }

    @Data
    @NoArgsConstructor
    public  static class WeekendOptions{
        @JsonProperty("NumStarters")
        private int numStarters;
        @JsonProperty("StartingGrid")
        private String startingGrid;
        @JsonProperty("QualifyScoring")
        private String qualifyScoring;
        @JsonProperty("CourseCautions")
        private String courseCautions;
        @JsonProperty("StandingStart")
        private int standingStart;
        @JsonProperty("Restarts")
        private String restarts;
        @JsonProperty("WeatherType")
        private String weatherType;
        @JsonProperty("Skies")
        private String skies;
        @JsonProperty("WindDirection")
        private String windDirection;
        @JsonProperty("WindSpeed")
        private String windSpeed;
        @JsonProperty("WeatherTemp")
        private String weatherTemp;
        @JsonProperty("RelativeHumidity")
        private String relativeHumidity;
        @JsonProperty("FogLevel")
        private String fogLevel;
        @JsonProperty("Unofficial")
        private int unofficial;
        @JsonProperty("CommercialMode")
        private String commercialMode;
        @JsonProperty("NightMode")
        private String nightMode;
        @JsonProperty("IsFixedSetup")
        private int isFixedSetup;
        @JsonProperty("StrictLapsChecking")
        private String strictLapsChecking;
        @JsonProperty("HasOpenRegistration")
        private int hasOpenRegistration;
        @JsonProperty("HardcoreLevel")
        private int hardcoreLevel;
        @JsonProperty("ShortParadeLap")
        private String shortParadeLap;
        @JsonProperty("TimeOfDay")
        private String timeOfDay;
        @JsonProperty("Date")
        private String date;
        @JsonProperty("EarthRotationSpeedupFactor")
        private String earthRotationSpeedupFactor;
        @JsonProperty("NumJokerLaps")
        private String numJokerLaps;
        @JsonProperty("IncidentLimit")
        private String incidentLimit;
        @JsonProperty("FastRepairsLimit")
        private String fastRepairsLimit;
        @JsonProperty("GreenWhiteCheckeredLimit")
        private String greenWhiteCheckeredLimit;
    }
}
