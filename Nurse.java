
public class Nurse {
		//double arrivalTime;
		//double departureTime;
		//double serviceStartTime;
		int patientid;
		int nurseid;
		int nurseStatus; // Available or not

		int noOfTrueAlarm;
		int noOfFalseAlarm;
		//int customerclass;
		//double timeinqueue;
		double servicetime;
		//double timeinsystem;
		//int shiftends; //indicator 
		public Nurse(int nid)
		{
			nurseid = nid;
			noOfTrueAlarm = 0;
			noOfFalseAlarm = 0;
			servicetime = 0;
			nurseStatus = 0;
			patientid = 0;
		}
}
