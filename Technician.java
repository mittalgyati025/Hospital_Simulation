public class Technician {
	//double arrivalTime;
	//double departureTime;
	//double serviceStartTime;
	int patientid;
	int technicianid;
	int technicianStatus; // Available or not

	int noOfTrueAlarm;
	int noOfFalseAlarm;
	//int customerclass;
	//double timeinqueue;
	double servicetime;
	//double timeinsystem;
	//int shiftends; //indicator 
	public Technician(int tid)
	{
		technicianid = tid;
		noOfTrueAlarm = 0;
		noOfFalseAlarm = 0;
		servicetime = 0;
		technicianStatus = 0;
		patientid = 0;
	}
}
