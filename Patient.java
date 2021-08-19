
public class Patient {
	double arrivalTime;
	double departureTime;
	//double abandonTime;
	double serviceStartTime;
	double timeinsystem;
	int patientid;
	int nurseid;
	int technicianid;
	int nurseStatus;
	int alarmStatus; //if alarm on or off - 0 on 1 off
	int technicianAlarmStatus; //to track technician fatigue; 0 - on and 1 - off
	int alarmType; // 0 or 1, true or false alarm
	int noOfTrueAlarm;
	int noOfFalseAlarm;
	int noOfFlipsToOn;
	int noOfFlipsToOff;
	int whoProcessed = 1000; //if 1 - technician processed the alarm, 0 - nurse processed
	
	int patientStatus; //already present in queue or not
	int AlarmsServiced; //count of number of alarms serviced for a patient
	//int customerclass;
	double timeinqueue;
	//int arrivalday;
	//int arrivalshift;
	int noOfTimesInQueue;
	double SwitchOffTime; // when alarm is switched off
	double technicianSwitchOffTime; // when technician alarm is switched off
	double TimeTurnedOff; // how long alarm been off
	
	String alarmMainType; // added to track technical/non technical alarm
	int noOfTrueAlarmTech;
	int noOfFalseAlarmTech;
	int noOfTrueAlarmNonTech;
	int noOfFalseAlarmNonTech;
	
	int NurseVisibleTA;
	int NurseVisibleFA;
	
	int alarmSubType;
	
	String ProcessDecision;
	
	String rerouteDecision;
	
	String countDecision;
	
	//double firstArrivalTime;
	public Patient(int pid)
	{
		patientid = pid;
		timeinqueue = 0;
		patientStatus = 0;
		AlarmsServiced = 0;
		noOfTrueAlarm = 0;
		noOfFalseAlarm = 0;
		noOfFlipsToOn = 0;
		noOfFlipsToOff = 0;
		noOfTimesInQueue = 0;
		//firstArrivalTime = 1000;
		alarmMainType = "Non Technical";
		noOfTrueAlarmTech = 0;
		noOfFalseAlarmTech = 0;
		noOfTrueAlarmNonTech = 0;
		noOfFalseAlarmNonTech = 0;
		NurseVisibleTA = 0;
		NurseVisibleFA = 0;
		arrivalTime = 0;
		departureTime = 0;
		serviceStartTime = 0;
		timeinsystem = 0;
		nurseid = 0;
		nurseStatus = 0;
		alarmStatus = 0;
		technicianAlarmStatus = 0;
		alarmType = 0;
		SwitchOffTime = 0;
		TimeTurnedOff = 0;
		alarmSubType = 0;
	}
}
