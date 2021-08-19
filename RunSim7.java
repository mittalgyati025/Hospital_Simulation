import java.math.*;
import java.util.*;
import java.io.*;



/*Technician and Nurse handle the alarms
80% true alarm handled by nurses - TrueAlarms - TrueAlarmsIgnored
20% true alarm handled by technician - TrueAlarmsIgnored
90% false alarm handled by technician - FalseAlarmsIgnored
10% false alarm nurses - FalseAlarmsServiced
*/
public class RunSim7 {
	
	

double totalNoInSys = 0;
double totalNoInSysSq = 0;
double totalNoInQ = 0;
double totalNoInQSq = 0;
int noCustInSys = 0; //keep track of # of customers in system
int noCustInQ = 0;
int noServComp = 0; //keep track of # of service completions
int noServAb = 0; //keep track of # of service abandonments
double totalTimeInSys = 0; //keep track of time in system I will use it to find average time in system
double totalTimeInSysSq = 0; //keep track of second moment of time in system
double totalTimeInQ = 0;
int noMeetTarget = 0; //keep track of # of customers meeting the waiting time target
double percmeetTarget = 0;

double avgTimeInsystem;
double varTimeInsystem;
double avgTimeInQ;
double avgNumberInSystem;	
double varNumberInSystem;
double avgNumberInQ;
double varNumberInQ;

int CustomerIDCnt = 1; //an id for every customer
int ECustomerIDCnt = 1; //an id for every ext customer
//double endTime = 17; //how long to run the simulation each day
double endTime = 24; //how long to run the simulation each day
int sysStatus = 0; //to check if the single server is busy
int esysStatus = 0; //use this for H1
static double lambda = 60; //change the function in getlambda if you need to change the parameters
double extlambda=0;
int extcalls=1802; //use this only for Heuristic 1
double en=4; //number of servers for Heuristic 1
double mu = 30; //average service rate across all days. Period is hour. Assuming only 2/3 of the workers work at this speed. But allowed to vary according to time
double emu = 46.154;
double mua = 40; //rate per period (hour) assuming people abandon after 1.5 minutes But allowed to vary according to time
double extmua = 0.1;
double n = 1; //no of servers
int tweek = 0;
int endweek = 10;
int tday = 0;
double tnow = 1; //nothing is assumed to happen in the first hour 6 to 7 AM slot.
//double tnow2 = 0; //creating this to reset tnow during shift transitions. Check shift change method
int startcheck = 0; //for signaling phantom shifts
double targetWait = 0.02;
double targetPerc = 0.7;
int nperiods = 6; //No of periods in a day
double hours = 24; //No of hours in a day
double stime = 3; //No. of hours in each period
int day = 0;
int shiftno = 0;
double timeinpod = 0.7;//Average percentage of time a server is available to take calls pertaining to the pod (1-vacation time)
//double[][] nservers = new double [7][nperiods];
//double[][] marrival = new double [7][nperiods];
//int[][] nservers = { {}  };

int SwitchOnHours = 1; //nurse fatigue hour
int noOfWorkingNurse;
int noOfWorkingTechnicians;

static int noOfPatients = 100;
static int noOfNurse = 7;
static int noOfTechnician = 18;

static double[] arrRate = {199.8066667,62.5};
//static double[] arrRate = {771,62.5};
Random rand = new Random();

int TrueAlarmMissed = 0;
int DodgedFalseAlarm = 0;
int FalseAlarmsServiced = 0;
int TrueAlarms = 0;
int FalseAlarms = 0;
int FalseAlarmThreshold = 3;
int maxNoOfAlarmsInQ = 0;
int avgNoOfAlarmsInQ = 0;
int noPatInQ = 0;

int TechnicianAlarmMissed = 0; //alarms missed due to technician fatigue

//average Time taken for nurses to process the alarms-------------------------------GM
double TrueAlarmProsTime = 0.00165;
double FalseAlarmProsTime = 0.000825;

//avearge Time taken for technicians to process the alarms-----------------------------GM
double TechTrueAlarmProsTime = 0.00165/2;
double TechFalseAlarmProsTime = 0.000825;

//variables to track the workload on technicians
double TechTrueAlarmWorkload = 0;
double TechFalseAlarmWorkload = 0;

//int TechAlarms = 0;
//int NonTechAlarms = 0;

//5 Alarm sub types and probabilities of each to occur
int noOfAlarmSubType = 5;
double alarmSubTypeProb;

//I confirmed that these are not being use anywhere-----------------------------GM
//alarm sub type probabilities
double alarmSubTypeProb1 = 0.20;
double alarmSubTypeProb2 = 0.40;
double alarmSubTypeProb3 = 0.60;
double alarmSubTypeProb4 = 0.80;

//variable to count no.of times alarms are ignored
int TrueAlarmsIgnored = 0;
int FalseAlarmsIgnored = 0;

//to calculate the rate of switching off of alarms
double nurseSwitchOffRate = 0;
double technicianSwitchOffRate = 0;

//Number of days to run simulation
int noOfDays = 1;

//this is where we construct the simulator// not using anywhere----------------------------GM
public RunSim7()
{
	
}
public static void main(String args[])
{

	WritetoCSV loWriteToCSV = new WritetoCSV(); //write all the details in a CSV file for checking
	RunSim7 meclass = new RunSim7();
	//ArrayList<ArrayList<Double>> marrival = meclass.generateCsvFileArr("ArrRate.csv");
	//ArrayList<ArrayList<Double>> earrival = meclass.generateCsvFileArr("ExtCalls.csv");
	
	meclass.percmeetTarget = 0; //Reset
	File patFile1 = new File("Data1.csv");
//	String servFile = "Servers.csv";
	File patFile = meclass.initiateFile(patFile1); //Just create the first row in data file
	EventQ eventq = new EventQ(); //this is a vector of current events, I will delete past events once it is taken
	PatientQ patq = new PatientQ(); //this is a vector of queue events
	NurseQ nurseq = new NurseQ(); //this is a vector of servers for nurses
	TechnicianQ technicianq = new TechnicianQ(); //this is a vector of servers for technicians


	//for (meclass.tday=0; meclass.tday<1; meclass.tday++)
	for (meclass.tday=0; meclass.tday< meclass.noOfDays; meclass.tday++)
	{
			Patient[] pat = new Patient[noOfPatients];
			for(int i=0; i< noOfPatients; i++) {
				pat[i] = new Patient(i);
			}
		
			Nurse[] nur = new Nurse[noOfNurse];
			for(int i=0; i< noOfNurse; i++) {
				nur[i] = new Nurse(i);
			}
			
			Technician[] tech = new Technician[noOfTechnician];
			for(int i=0; i< noOfTechnician; i++) {
				tech[i] = new Technician(i);
			}
			
			meclass.noOfWorkingNurse = 0;
			meclass.noOfWorkingTechnicians = 0;
			
			meclass.generateEndEvent(eventq); //Make the end customer Id to be -1 for differentiating
			lambda = 77100/24;
			//lambda = arrRate[meclass.tday];
			System.out.println("Lambda" +lambda);
			Event curreve = meclass.generateArrivalEvent(eventq,pat); //Generate an arrival Event refer method below
			System.out.println("Current Event Type" +curreve.objsType);
			System.out.println("Current Event Time" +curreve.objdTime);
			System.out.println("Tday" +meclass.tday);
			// Decided not to use as of now---------------------------------GM
			while (meclass.startcheck == 0)
			{
				System.out.println("Inside startcheck 0");
				if (curreve != null){meclass.startcheck = 1;}
//				else if (curreve2 != null){meclass.startcheck = 1;}
				/* else {
					meclass.tnow = meclass.tnow + meclass.stime;
					meclass.ShiftChangeEvent(servq, nservers, marrival, earrival, servtime, abantime, eventq, custq);	
//					meclass.ShiftChangeEvent(servq, nservers, marrival, earrival, servtime, abantime, eventq, custq, ecustq);	
					eventq.eraseEvent();
					curreve = meclass.generateArrivalEvent(eventq); //Generate an arrival Event refer method below
//					curreve2 = meclass.generateExtArrivalEvent(eventq); //Generate an ext arrival Event refer method below
//					System.out.println("tnow = " +meclass.tnow);
					//*** Added the following for H1
					if(meclass.day==0 && meclass.shiftno==1) 
					{
					meclass.generateExtArrivalEvent(eventq); //Generate an ext arrival Event refer method below
					meclass.startcheck = 1;
					}
					//** End addition
				} */
			}

			while(meclass.tnow < meclass.endTime)
			{
				System.out.println("Inside get event");
				Event NextEvent = eventq.getfirstevent();
	//			System.out.println("tnow = " +meclass.tnow);
				eventq.eraseEvent();	
				double deltaT = NextEvent.objdTime - meclass.tnow;//how much time has passed since last event
				meclass.tnow = NextEvent.objdTime; //Update tnow
				meclass.updateStats(deltaT);//update my statistics --should check
				
				System.out.println("Object Type:" +NextEvent.objsType);
				if(NextEvent.objsType.equalsIgnoreCase("arrival"))
				{
					System.out.println("Inside arrival");
					meclass.arrivalEvent(patq, NextEvent, eventq, nur, tech, pat);	
				//	System.out.println("Someone arrived");
				}
				
				/*if(NextEvent.objsType.equalsIgnoreCase("Extarrival"))
				{
					meclass.extarrivalEvent(NextEvent, eventq, eservq);	
//					meclass.extarrivalEvent(ecustq, NextEvent, eventq, servq);	
				//	System.out.println("Someone arrived");
				}
				*/
				
				if(NextEvent.objsType.equalsIgnoreCase("departure"))
				{
					System.out.println("Inside departure");
					meclass.departureEvent(patq, NextEvent, eventq, patFile, loWriteToCSV, nur, tech, pat);
//					meclass.departureEvent(custq, ecustq, NextEvent, eventq, custFile, loWriteToCSV, servq);
				//	System.out.println("Someone departed");
				}	
				
				/*if(NextEvent.objsType.equalsIgnoreCase("abandon"))
				{
					meclass.abandonEvent(custq, NextEvent, eventq, custFile, loWriteToCSV);	
				//	System.out.println("Someone abandoned at " +meclass.tnow);
				}	
				*/
				
				if(NextEvent.objsType.equalsIgnoreCase("end"))
				{
	//				meclass.endEvent(ecustq);	//Remove for H1
					meclass.tnow = meclass.endTime; //If we reach end of vector need to quit
				}
				System.out.println("tnow is:" +meclass.tnow);
			}
			meclass.tnow=1;
			if(eventq.getsize()>0)
			{
				System.out.println("Check because the the eventq size is " +eventq.getsize() );
				Event LastEvent = eventq.getfirstevent();
				System.out.println("The event is " +LastEvent.objsType);
				System.out.println("The event is " +LastEvent.objdTime);
			}
			if(patq.size()>0){System.out.println("Check because the the custq size is " +patq.size() );}
		}
	System.out.println("Total number of Service Completions " + meclass.noServComp);
	System.out.println("Total number of True Alarms "+ meclass.TrueAlarms);
	System.out.println("Total number of False Alarms "+ meclass.FalseAlarms);
	System.out.println("Number of True Alarms Missed due to nurse fatigue "+ meclass.TrueAlarmMissed);
	System.out.println("Number of False Alarms Serviced - Nurse "+ meclass.FalseAlarmsServiced);
	System.out.println("Number of False Alarms Dodged - Nurse "+ meclass.DodgedFalseAlarm);
	
	System.out.println("Maximum Number of Alarms in Queue  "+ meclass.maxNoOfAlarmsInQ);
	System.out.println("Average Number of Alarms in Queue for 2 days  "+ meclass.noPatInQ/48);
	
	System.out.println("Number of True Alarms Handled - Technician  "+ meclass.TrueAlarmsIgnored);
	System.out.println("Number of False Alarms Handled - Technician "+ meclass.FalseAlarmsIgnored);

	System.out.println("Technicians Workload for True Alarms  "+ meclass.TechTrueAlarmWorkload);
	System.out.println("Technicians Workload for False Alarms  "+ meclass.TechFalseAlarmWorkload);
	
	System.out.println("Nurse Workload for True Alarms  "+ (meclass.TrueAlarms-meclass.TrueAlarmMissed-meclass.TrueAlarmsIgnored)*meclass.TrueAlarmProsTime);
	System.out.println("Nurse Workload for False Alarms  "+  meclass.FalseAlarmsServiced* meclass.FalseAlarmsServiced);
	
	System.out.println("Nurse Workload for False Alarms  "+ meclass.TechFalseAlarmWorkload);
	
	System.out.println("No of technician alarm missed due to fatigue  "+ meclass.TechnicianAlarmMissed);
	System.out.println("Rate of switching off the alarms - Nurse  "+ meclass.nurseSwitchOffRate/meclass.noOfDays + " times per day");
	System.out.println("Rate of switching off the alarms - Technician  "+ meclass.technicianSwitchOffRate/meclass.noOfDays + " times per day");
	
	System.out.println("Queue Size  "+ patq.size());
	
	}
	
	//Print the update status
	//meclass.StatsPrint();
//	meclass.n++;
	//Reset all values again
//	meclass.initialize();
//	}	

/**
 * This method is to initiate a new file
 * 
 * 
 * 
 */

File initiateFile(File sampleFile)
{

try
{
//Delete and create the file again
if(sampleFile.exists())
{
	sampleFile.delete();
	sampleFile.createNewFile();
}
FileWriter sampleFileWriter = new FileWriter(sampleFile);
//Creating column headers in the file
sampleFileWriter.append("Day");
sampleFileWriter.append(',');	
sampleFileWriter.append("Patient ID");
sampleFileWriter.append(',');	
sampleFileWriter.append("Patient Status");
sampleFileWriter.append(',');
sampleFileWriter.append("Arrival Time");
sampleFileWriter.append(',');	
sampleFileWriter.append("Alarm Status");
sampleFileWriter.append(',');	
sampleFileWriter.append("Alarm Type");
sampleFileWriter.append(',');	
sampleFileWriter.append("Alarm Main Type");
sampleFileWriter.append(',');
sampleFileWriter.append("Alarm Sub Type");
sampleFileWriter.append(',');
sampleFileWriter.append("Number of Technical True Alarms");
sampleFileWriter.append(',');	
sampleFileWriter.append("Number of Technical False Alarms");
sampleFileWriter.append(',');
sampleFileWriter.append("Number of Non Techinical True Alarms");
sampleFileWriter.append(',');	
sampleFileWriter.append("Number of Non Techinical False Alarms");
sampleFileWriter.append(',');
sampleFileWriter.append("Processing Decision");
sampleFileWriter.append(',');
sampleFileWriter.append("Wait Time");
sampleFileWriter.append(',');
sampleFileWriter.append("Departure Time");
sampleFileWriter.append(',');
sampleFileWriter.append("Time Alarm is Switched Off");
sampleFileWriter.append(',');
sampleFileWriter.append("Number of False Alarms");
sampleFileWriter.append(',');	
sampleFileWriter.append("Number of True Alarms");
sampleFileWriter.append(',');	
sampleFileWriter.append("Attended Nurse ID");
sampleFileWriter.append(',');
sampleFileWriter.append("Attended Technician ID");
sampleFileWriter.append(',');
sampleFileWriter.append("Nurse(0)/Technician(1)");
sampleFileWriter.append(',');
sampleFileWriter.append("No.of True Alarms visible to Nurse");
sampleFileWriter.append(',');
sampleFileWriter.append("No.of False Alarms visible to Nurse");
sampleFileWriter.append(',');
sampleFileWriter.append("No.Of Times Flipped to On");
sampleFileWriter.append(',');
sampleFileWriter.append("No.Of Times Flipped to Off");
sampleFileWriter.append(',');
sampleFileWriter.append("Time in System");
sampleFileWriter.append('\n');

sampleFileWriter.flush();
    
//Better to close the file 
sampleFileWriter.close();
}
catch(IOException e)
{
     e.printStackTrace();
} 
return sampleFile;
}

/**
 * This method is to read a csv file
 * 
 * 
 * 
*/

private ArrayList<ArrayList<Double>> generateCsvFileArr(String fileName){
	  BufferedReader br = null;
	  int lineCnt = 0;
	  String sCurrentLine;
	  ArrayList<ArrayList<Double>> arrlist = new ArrayList<ArrayList<Double>>();
	   
	  try {
	         
	        br = new BufferedReader(new FileReader(fileName));
	        while ((sCurrentLine = br.readLine()) != null) {
	        if(lineCnt >= 1)
	        {
	        String[] values = sCurrentLine.split(",");
	        ArrayList<Double> rows = new ArrayList<Double>();
	          for(int incr = 0;incr < values.length;incr++)
	          {
	        if(incr >= 1)
	        {
	        rows.add(new Double(values[incr]));
	        }
	          }
	        arrlist.add(rows);
	  //      System.out.println(values[1]);
	        }
	        else
	        {
	        //System.out.println(sCurrentLine);
	            lineCnt += 1;
	        }
	        }
//	        return servers;
	  }
	  catch(IOException e)
	  {
	  e.printStackTrace();
	  }
	  finally {
	try {
	if (br != null)
	br.close();
	} catch (IOException ex) {
	ex.printStackTrace();
	}
	}
//	  System.out.println(servers); 
	  return arrlist;
	  
	  }

/**
 * This method is to generate a new arrival event class
 * 
 * 
 * 
 */

private Event generateArrivalEvent(EventQ eq,Patient pat[])
{
	if(lambda>0)
	{
		//double TimeArrival  = (- (Math.log(1 - Math.random()) /  lambda))/25 + tnow;
		double TimeArrival  = (- (Math.log(1 - Math.random()) /  lambda)) + tnow;
	//	System.out.println("Time arrival " +TimeArrival );
	
		Event EventObj = new Event("arrival", TimeArrival);
		int  n = rand.nextInt(noOfPatients);
		
		System.out.println("Before while");
		while(1==1) {
			if (pat[n].patientStatus == 1) {
				n = rand.nextInt(noOfPatients);
			}
			else
				break;			
		}
		System.out.println("After while");
		
		System.out.println("Patient ID is:" +n);
		System.out.println("Lambda is:" +lambda);
		//Above we are populating event object and here we are populating patient object here-----------------------------GM
		// pat[n].patientStatus = 1; Commenting to change when patient is actually being treated
		pat[n].arrivalTime = TimeArrival;
		//if (pat[n].arrivalTime < pat[n].firstArrivalTime) {
		//	pat[n].firstArrivalTime = pat[n].arrivalTime;
		//}
		EventObj.PatientID = n;
		
		//pat[n].alarmSubType = (int) (Math.random() * ((noOfAlarmSubType+1)-1))+1;
		
		//Alarm sub type implementation
		alarmSubTypeProb = Math.random();
		if(alarmSubTypeProb >= 0 & alarmSubTypeProb <= alarmSubTypeProb1)
			pat[n].alarmSubType = 1;
		if(alarmSubTypeProb1 >= 0.20 & alarmSubTypeProb2 <= 0.40)
			pat[n].alarmSubType = 2;
		if(alarmSubTypeProb2 >= 0.40 & alarmSubTypeProb3 <= 0.60)
			pat[n].alarmSubType = 3;
		if(alarmSubTypeProb3 >= 0.60 & alarmSubTypeProb4 <= 0.80)
			pat[n].alarmSubType = 4;
		if(alarmSubTypeProb >= alarmSubTypeProb4)
			pat[n].alarmSubType = 5;
		
		//Alarm Main type implementation - Technical or Non-Technical Alarm
		if(Math.random() <= 0.175) {
			pat[n].alarmMainType = "Technical"; //Technical alarm
		}
		else {
			pat[n].alarmMainType = "Non Technical"; //NonTechnical alarm
		} 
		
		//Alarm Type Implementation - True or False Alarm
		if(Math.random() <= 0.05) {
				pat[n].alarmType = 0; //True alarm
				if(Math.random() <= 0.80) {
					pat[n].ProcessDecision = "Processing"; // TP
				}
				else {
					pat[n].ProcessDecision = "NotProcessing"; //FN
				}
		} 
		else {
				pat[n].alarmType = 1; //False alarm
				if(Math.random() <= 0.90) {
					pat[n].ProcessDecision = "NotProcessing"; //TN
				}
				else {
					pat[n].ProcessDecision = "Processing"; //FP
				}
		}
		if(pat[n].alarmStatus == 1) {
			//if((pat[NextEvent.PatientID].AlarmsServiced/ ((tnow-pat[NextEvent.PatientID].firstArrivalEvent) * 60 * 60)) < 0.004)
			/*if((pat[NextEvent.PatientID].AlarmsServiced/ ((tnow-1) * 60 * 60)) < 0.004) {
				pat[NextEvent.PatientID].alarmStatus = 0;
				pat[NextEvent.PatientID].noOfFlipsToOn++;
				pat[NextEvent.PatientID].TimeTurnedOff = pat[NextEvent.PatientID].TimeTurnedOff + tnow - pat[NextEvent.PatientID].SwitchOffTime;
			}*/
			//Here we are switching on the alaram if below condition is satisfied-------------------GM
			if(tnow - pat[n].SwitchOffTime >= SwitchOnHours) {
				pat[n].alarmStatus = 0;
				pat[n].noOfFlipsToOn++;
				pat[n].TimeTurnedOff = pat[n].TimeTurnedOff + tnow - pat[n].SwitchOffTime;
				pat[n].SwitchOffTime = 100000; // to reset the time alarm was switched off
				
			}
		}
		
		if(pat[n].technicianAlarmStatus == 1) {
			if(tnow - pat[n].technicianSwitchOffTime >= SwitchOnHours) {
				pat[n].technicianAlarmStatus = 0;
				pat[n].noOfFlipsToOn++;
				pat[n].TimeTurnedOff = pat[n].TimeTurnedOff + tnow - pat[n].technicianSwitchOffTime;
				pat[n].technicianSwitchOffTime = 100000; // to reset the time alarm was switched off
				
			}
		}
		pat[n].countDecision = "Count";
		pat[n].rerouteDecision = "None";
		
		System.out.println("Obj Type:" +EventObj.objsType);	
		System.out.println("Obj Time:" +EventObj.objdTime);	
		eq.addEvent(EventObj); //Add the arrival event to the queue
		return EventObj;
	}
	else {return null;}
}




/**
 * This method is generate an end event class
 * 
 * 
 * 
 */

private void generateEndEvent(EventQ eq)
{
	Event EventObj = new Event("end", endTime);
	eq.addEvent(EventObj); //Add the end event to the queue	
}


/**
 * This method is generate a new service event class but I do not use it here.
 * 
 * 
 * 
 */

private double generateServiceTime(double mu, int type)
{
	double timeService = 1000;
	if(type==1){
	timeService = - (Math.log(1 - Math.random()) /  (0.5*mu)) ;
	}
	if(type==2){
	timeService = - (Math.log(1 - Math.random()) /  mu) ;	
	}
	return timeService;
}


/**
 * This method is generate the number of servers or arrival rate for that shift.
 * 
 * 
 * 
 */

private double getn(int i, int j, ArrayList<ArrayList<Double>> ns)
{
	if(i>6){System.out.println("The Day is " +i );}
	if(j>nperiods){System.out.println("The Shiftno is " +j );}	
	return ns.get(j).get(i);
}


/**
 * This method is to initialize the system
 * 
 * 
 * 
 */

public void initialize()
{
	totalNoInSys = 0;
	totalNoInSysSq = 0;
	totalNoInQ = 0;
	totalNoInQSq = 0;
	noCustInSys = 0; //keep track of # of customers in system
	noCustInQ = 0;
	noServComp = 0; //keep track of # of service completions
	noServAb = 0; //keep track of # of service abandonments
	totalTimeInSys = 0; //keep track of time in system I will use it to find average time in system
	totalTimeInSysSq = 0; //keep track of second moment of time in system
	totalTimeInQ = 0;
	noMeetTarget = 0; //keep track of # of customers meeting the waiting time target
	tnow = 0;
	CustomerIDCnt = 1; //and id for every customer
	sysStatus = 0; //to check if the single server is busy
	
}

/**
 * This method updates the integral to find average number of customers in system
 * 
 * 
 * 
 */

public void updateStats(double delta)
{
	totalNoInSys += (double) noCustInSys*delta; //this is for avg no in Sys
	totalNoInSysSq += (double) Math.pow(noCustInSys,2)*delta; //this is for Var of no in Sys
	totalNoInQ += (double) noCustInQ*delta; //this is for avg no in Sys
	totalNoInQSq += (double) Math.pow(noCustInQ,2)*delta; //this is for Var of no in Sys
	
}

/**
 * This method is called when the next event is an arrival
 * 
 * 
 * 
 */
public void arrivalEvent(PatientQ pq, Event NextEvent, EventQ eq, Nurse nur[], Technician tech[], Patient pat[])
{
	System.out.println("Inside arrival event");
	System.out.println("Number of Working nurse:"+noOfWorkingNurse);
	System.out.println("Number of nurses:"+noOfNurse);
	System.out.println("Patient Queue Size:"+pq.size());
	
			if(pat[NextEvent.PatientID].alarmType == 0) //True Alarm
			{
				if(pat[NextEvent.PatientID].countDecision == "Count") {
					TrueAlarms++;
				}
				if(pat[NextEvent.PatientID].ProcessDecision == "Processing")
				{
					pat[NextEvent.PatientID].noOfTrueAlarm++;
					if(pat[NextEvent.PatientID].alarmMainType == "Technical") {
						pat[NextEvent.PatientID].noOfTrueAlarmTech++;
					}
					else pat[NextEvent.PatientID].noOfTrueAlarmNonTech++;
				    // It means alarm is off so no need to process the patient so we send them out
					if(pat[NextEvent.PatientID].alarmStatus == 1) //Alarm off
					{
						TrueAlarmMissed++;
						Event EventObj = new Event("departure", NextEvent.objdTime);
						//This is fake processing of event when alarm is switched off, we departure event and add it in queue------------------GM
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
					
					}
					//The alarm is not off, so go in and process
					else
					{
						pat[NextEvent.PatientID].whoProcessed = 0;
						//No nurse is availabe so we add them to queue
						if(noOfWorkingNurse < noOfNurse-1) {
							//Randomly figure out which nurse is available
							int  n = rand.nextInt(noOfNurse);			
							while(1==1) {
								if (nur[n].nurseStatus == 1) {
									n = rand.nextInt(noOfNurse);
								}
								else
									break;	
							}
							nur[n].nurseStatus = 1;
							pat[NextEvent.PatientID].NurseVisibleTA++;
							noOfWorkingNurse++;
							pat[NextEvent.PatientID].patientStatus = 1;
							pat[NextEvent.PatientID].nurseid = n;
							//pat[NextEvent.PatientID].whoProcessed = 0;
							pat[NextEvent.PatientID].AlarmsServiced++;
							pat[NextEvent.PatientID].serviceStartTime = NextEvent.objdTime;
							pat[NextEvent.PatientID].departureTime = /*(- (Math.log(1 - Math.random()) /  10))*/ TrueAlarmProsTime +  pat[NextEvent.PatientID].serviceStartTime;

							// we are making switching off the alarm if below condition is true---------------------------GM
							if(((pat[NextEvent.PatientID].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.004) {
								pat[NextEvent.PatientID].alarmStatus = 1; // set alarm to off
								pat[NextEvent.PatientID].noOfFlipsToOff++;
								nurseSwitchOffRate++;
								pat[NextEvent.PatientID].SwitchOffTime = tnow;
							}
					// this is real processing case where we add departure event to Event Queue at the end of processing the arrival event--------------GM
							Event EventObj = new Event("departure", pat[NextEvent.PatientID].departureTime);
							EventObj.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObj);
						}
						else
						{
							pq.enqueue(NextEvent);
							noPatInQ++;
						}
					}
				}
				//Since nurse is not processing, this will be processed by Technician-----------------------GM
				else
				{
					if(pat[NextEvent.PatientID].technicianAlarmStatus == 1) //Alarm off
					{
						TechnicianAlarmMissed++;
						Event EventObj = new Event("departure", NextEvent.objdTime);
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
					
					}
					else
					{
						//Check if a technician is free, if not add them to the queue
						pat[NextEvent.PatientID].whoProcessed = 1; // 1 means processed by technitian
						if(noOfWorkingTechnicians < noOfTechnician-1) {
							int  t = rand.nextInt(noOfTechnician);			
							while(1==1) {
								if (tech[t].technicianStatus == 1) {
									t = rand.nextInt(noOfTechnician);
								}
								else
									break;	
							}
						
							tech[t].technicianStatus = 1;
							noOfWorkingTechnicians++;
							pat[NextEvent.PatientID].patientStatus = 1;
							pat[NextEvent.PatientID].technicianid = t;
							// pat[NextEvent.PatientID].whoProcessed = 1;
							pat[NextEvent.PatientID].serviceStartTime = NextEvent.objdTime;
							pat[NextEvent.PatientID].departureTime = pat[NextEvent.PatientID].arrivalTime + TechTrueAlarmProsTime ;

							TrueAlarmsIgnored++; // to track missed true alarms - which nurse should have handled but technician handled------------GM
							
							if(((pat[NextEvent.PatientID].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.008) {
								pat[NextEvent.PatientID].technicianAlarmStatus = 1; // set alarm to off
								pat[NextEvent.PatientID].noOfFlipsToOff++;
								technicianSwitchOffRate++;
								pat[NextEvent.PatientID].technicianSwitchOffTime = tnow;
							}
							
							Event EventObj = new Event("departure", pat[NextEvent.PatientID].departureTime);
							TechTrueAlarmWorkload = TechTrueAlarmWorkload + TechTrueAlarmProsTime;
							EventObj.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObj);

							//Even though this was a true alarm it was handled by a technician so now we need to rerouted it to nurse---------------GM
							pat[NextEvent.PatientID].rerouteDecision = "nurseTrue";
							
							/*pat[NextEvent.PatientID].ProcessDecision = "Processing";

							Event EventObjA = new Event("arrival", pat[NextEvent.PatientID].departureTime+ (- (Math.log(1 - Math.random()) /  lambda)));
							EventObjA.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObjA);*/
						}
						else
						{
							pq.enqueue(NextEvent);
							noPatInQ++;
						}
					
					}
				}
			}
			else if(pat[NextEvent.PatientID].alarmType == 1) //False Alarm
			{
				if(pat[NextEvent.PatientID].countDecision == "Count") {
					FalseAlarms++;
				}
				if(pat[NextEvent.PatientID].ProcessDecision == "Processing")
				{
					pat[NextEvent.PatientID].noOfFalseAlarm++;
				
					if(pat[NextEvent.PatientID].alarmMainType == "Technical") {
					pat[NextEvent.PatientID].noOfFalseAlarmTech++;
					}
					else pat[NextEvent.PatientID].noOfFalseAlarmNonTech++;
				
				
					if(pat[NextEvent.PatientID].alarmStatus == 1) //Alarm off
					{
						DodgedFalseAlarm++;
						Event EventObj = new Event("departure", NextEvent.objdTime);
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
					}
					else
					{// alram is on-----------------GM
						pat[NextEvent.PatientID].whoProcessed = 0;
						if(noOfWorkingNurse < noOfNurse-1) {
							int  n = rand.nextInt(noOfNurse);			
							while(1==1) {
								if (nur[n].nurseStatus == 1) {
									n = rand.nextInt(noOfNurse);
								}
								else
									break;	
							}
							FalseAlarmsServiced++;
							nur[n].nurseStatus = 1; //Nurse occupied
							pat[NextEvent.PatientID].NurseVisibleFA++;
							noOfWorkingNurse++;
							pat[NextEvent.PatientID].patientStatus = 1;
							pat[NextEvent.PatientID].nurseid = n;
							//pat[NextEvent.PatientID].whoProcessed = 0;
							pat[NextEvent.PatientID].AlarmsServiced++;
							pat[NextEvent.PatientID].serviceStartTime = NextEvent.objdTime;
							pat[NextEvent.PatientID].departureTime = FalseAlarmProsTime +  pat[NextEvent.PatientID].serviceStartTime;
							
							//Nurse fatigue - no.of alarms per second is calculated, if it is > 350 - then nurse fatigue
							
							if(((pat[NextEvent.PatientID].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.004) {
								pat[NextEvent.PatientID].alarmStatus = 1; // set alarm to off
								pat[NextEvent.PatientID].noOfFlipsToOff++;
								nurseSwitchOffRate++;
								pat[NextEvent.PatientID].SwitchOffTime = tnow;
							}
					
							Event EventObj = new Event("departure", pat[NextEvent.PatientID].departureTime);
							EventObj.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObj);

							//Since this is false alarm, do not reroute this is to technician---------------------GM
							pat[NextEvent.PatientID].rerouteDecision = "technicianFalse";
							
							/*pat[NextEvent.PatientID].ProcessDecision = "Not Processing";

							Event EventObjA = new Event("arrival", pat[NextEvent.PatientID].departureTime+ (- (Math.log(1 - Math.random()) /  lambda)));
							EventObjA.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObjA);*/
							
						}
						else
						{
							pq.enqueue(NextEvent);
							noPatInQ++;
						}
					}
				
				}
				else
				{
					if(pat[NextEvent.PatientID].technicianAlarmStatus == 1) //Alarm off
					{
						TechnicianAlarmMissed++;
						Event EventObj = new Event("departure", NextEvent.objdTime);
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
					}
					else
					{
						pat[NextEvent.PatientID].whoProcessed = 1; // 1 means technitian processed it----------------GM
						if(noOfWorkingTechnicians < noOfTechnician-1) {
							int  t = rand.nextInt(noOfTechnician);			
							while(1==1) {
								if (tech[t].technicianStatus == 1) {
								t = rand.nextInt(noOfTechnician);
								}
								else
									break;	
							}
							tech[t].technicianStatus = 1; //Technician occupied
							noOfWorkingTechnicians++;
							pat[NextEvent.PatientID].patientStatus = 1;
							pat[NextEvent.PatientID].technicianid = t;
							//pat[NextEvent.PatientID].whoProcessed = 1;
							pat[NextEvent.PatientID].serviceStartTime = NextEvent.objdTime;
							pat[NextEvent.PatientID].departureTime = pat[NextEvent.PatientID].arrivalTime + TechFalseAlarmProsTime ;

							FalseAlarmsIgnored++; // to track missed true alarms - which nurse should have handled but technician handled-------------GM
							
							if(((pat[NextEvent.PatientID].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.008) {
								pat[NextEvent.PatientID].technicianAlarmStatus = 1; // set alarm to off
								pat[NextEvent.PatientID].noOfFlipsToOff++;
								technicianSwitchOffRate++;
								pat[NextEvent.PatientID].technicianSwitchOffTime = tnow;
							}
							
							Event EventObj = new Event("departure", pat[NextEvent.PatientID].departureTime);
							TechFalseAlarmWorkload = TechFalseAlarmWorkload + TechFalseAlarmProsTime;
							EventObj.PatientID = NextEvent.PatientID;
							eq.addEvent(EventObj);
						}
						else
						{
							pq.enqueue(NextEvent);
							noPatInQ++;
						}	
					}
				}
			}
			
			System.out.println("PatientID is:" +NextEvent.PatientID);
			generateArrivalEvent(eq,pat);
}

public int searchErase(PatientQ pq, EventQ eq, Nurse nur[], Technician tech[], Patient pat[],int processorFlag) {
	
	int returnFlag = 1000000;
	for(int i=0; i< pq.size();i++)
	{
		Event e = pq.getithevent(i);
		if(pat[e.PatientID].whoProcessed == processorFlag)
		{
			returnFlag = i;
			break;
		}
	}
	return returnFlag;
}

/**
 * This method is called when the next event is a departure
 * 
 * 
 * 
 */
//different for H1
public void departureEvent(PatientQ pq, Event NextEvent, EventQ eq, File patFile, WritetoCSV loWriteToCSV, Nurse nur[], Technician tech[], Patient pat[])
{
	System.out.println("Inside departure event");
	int pId = NextEvent.PatientID;
	double timeInSys = tnow - pat[pId].arrivalTime;
	pat[pId].departureTime = tnow;
	pat[pId].timeinsystem = timeInSys;
	loWriteToCSV.generateCsvFile(tday,pId,pat[pId].patientStatus,pat[pId].arrivalTime,pat[pId].alarmStatus,pat[pId].alarmType,pat[pId].alarmMainType,pat[pId].alarmSubType,pat[pId].noOfTrueAlarmTech,pat[pId].noOfFalseAlarmTech,pat[pId].noOfTrueAlarmNonTech,pat[pId].noOfFalseAlarmNonTech,pat[pId].ProcessDecision,pat[pId].timeinqueue,pat[pId].departureTime,pat[pId].SwitchOffTime,pat[pId].noOfFalseAlarm,pat[pId].noOfTrueAlarm,pat[pId].nurseid,pat[pId].technicianid,pat[pId].whoProcessed,pat[pId].NurseVisibleTA,pat[pId].NurseVisibleFA,pat[pId].noOfFlipsToOn,pat[pId].noOfFlipsToOff,pat[pId].timeinsystem,patFile);
	// loWriteToCSV.generateCsvFileCust(day, shiftno, n, lambda, c, custFile); //you can also create a string variable and write the string variable in the end
	//We dont have target now can add it later if needed
	//if(c.timeinqueue<= targetWait)	noMeetTarget++;
	//noCustInSys--; //1 less customer
	noServComp++; //one more service completion
	totalTimeInSys += timeInSys;
	totalTimeInSysSq += (timeInSys*timeInSys);
	pat[pId].patientStatus = 0;
	int whoProcessed = pat[pId].whoProcessed;
	if(pat[pId].whoProcessed == 0 ) {
		int nId = pat[pId].nurseid;
		if(timeInSys > 0 && pat[pId].ProcessDecision == "Processing") {
			noOfWorkingNurse--;
			nur[nId].nurseStatus = 0;
			pat[pId].whoProcessed = 1000;
		}
	}
	else if(pat[pId].whoProcessed == 1)
	{
		int nId = pat[pId].technicianid;
		if(timeInSys > 0) {
			noOfWorkingTechnicians--;
			tech[nId].technicianStatus = 0;
			pat[pId].whoProcessed = 1000;
		}
	}
	
	if(pat[pId].rerouteDecision == "nurseTrue")
	{
		pat[pId].alarmType = 0; //True alarm
	    pat[pId].ProcessDecision = "Processing";
	    pat[pId].rerouteDecision = "None";
	    pat[pId].countDecision = "DontCount";
	    Event EventObjA = new Event("arrival", tnow + (- (Math.log(1 - Math.random()) /  lambda)));
		EventObjA.PatientID = NextEvent.PatientID; 
		pq.enqueue(EventObjA);
	}
	if(pat[pId].rerouteDecision == "technicianFalse")
	{
		pat[pId].alarmType = 1; //False alarm
	    pat[pId].ProcessDecision = "Not Processing";
	    pat[pId].rerouteDecision = "None";
	    pat[pId].countDecision = "DontCount";
	    Event EventObjA = new Event("arrival", tnow + .000000000000001);
		EventObjA.PatientID = NextEvent.PatientID;
		pq.enqueue(EventObjA);    
	}
	// totalTimeInQ += c.timeinqueue;
	//int nId = pat[pId].nurseid;
	//nur[nId].nurseStatus = 0;
	
	if(pq.size() > 0) {
		int p = searchErase(pq,eq, nur, tech,pat,whoProcessed);
		if(p == 1000000)
		{
			return;
		}
		else
		{
		if(pq.size() > maxNoOfAlarmsInQ) {
			maxNoOfAlarmsInQ = pq.size();
		}
		System.out.println("Before selecting nurse in departure");
		System.out.println("After selecting nurse in departure");
		NextEvent = (Event) pq.getithevent(p);
		pq.eraseEventAti(p);
		pId = NextEvent.PatientID;
		//noServComp++;
		pat[pId].timeinqueue = pat[pId].timeinqueue + tnow - pat[pId].arrivalTime;
		pat[pId].noOfTimesInQueue++;
		if(pat[pId].alarmType == 0) //True Alarm
		{	
			if(pat[pId].countDecision == "Count") {
				TrueAlarms++;
			}
			if(pat[pId].ProcessDecision == "Processing")
			{
				pat[pId].noOfTrueAlarm++;
			
				if(pat[pId].alarmMainType == "Technical") {
					pat[pId].noOfTrueAlarmTech++;
				}
				else pat[pId].noOfTrueAlarmNonTech++;
			
				if(pat[pId].alarmStatus == 1) //Alarm off
				{
					TrueAlarmMissed++;
					Event EventObj = new Event("departure", NextEvent.objdTime);
					EventObj.PatientID = NextEvent.PatientID;
					eq.addEvent(EventObj);
				}
				else
				{
					pat[pId].whoProcessed = 0;
					if(noOfWorkingNurse < noOfNurse-1) {
						//Randomly figure out which nurse is available
						int  n = rand.nextInt(noOfNurse);			
						while(1==1) {
							if (nur[n].nurseStatus == 1) {
								n = rand.nextInt(noOfNurse);
							}
							else
								break;	
						}
						nur[n].nurseStatus = 1; //Nurse occupied
						pat[pId].NurseVisibleTA++;
						noOfWorkingNurse++;
						pat[pId].patientStatus = 1;
						pat[pId].nurseid = n;
						pat[pId].AlarmsServiced++;
						pat[pId].serviceStartTime = NextEvent.objdTime;
						pat[pId].departureTime = TrueAlarmProsTime +  pat[pId].serviceStartTime;
				
						if(((pat[pId].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.004) { /*False alarm threshold*/
							pat[pId].alarmStatus = 1;
							pat[pId].noOfFlipsToOff++;
							nurseSwitchOffRate++;
							pat[pId].SwitchOffTime = tnow;
						}
						Event EventObj = new Event("departure", pat[pId].departureTime);
						EventObj.PatientID = pId;
						eq.addEvent(EventObj);
				
					}
					else
					{
						pq.enqueue(NextEvent);
						noPatInQ++;
					}
				}
			}
			else 
			{
				if(pat[pId].technicianAlarmStatus == 1) //Alarm off
				{
					TechnicianAlarmMissed++;
					Event EventObj = new Event("departure", NextEvent.objdTime);
					EventObj.PatientID = NextEvent.PatientID;
					eq.addEvent(EventObj);
				}
				else
				{
				//Check if a technician is free, if not add them to the queue
					pat[pId].whoProcessed = 1;
					if(noOfWorkingTechnicians < noOfTechnician-1) {
						int  t = rand.nextInt(noOfTechnician);			
						while(1==1) {
							if (tech[t].technicianStatus == 1) {
							t = rand.nextInt(noOfTechnician);
							}
							else
								break;	
						}
					
						tech[t].technicianStatus = 1; //Technician occupied
						noOfWorkingTechnicians++;
						pat[pId].patientStatus = 1;
						pat[pId].technicianid = t;
						// pat[NextEvent.PatientID].whoProcessed = 1;
						pat[pId].serviceStartTime = NextEvent.objdTime;
						pat[pId].departureTime = pat[NextEvent.PatientID].arrivalTime + TechTrueAlarmProsTime ;

						TrueAlarmsIgnored++;
						
						if(((pat[pId].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.008) { /*False alarm threshold*/
							pat[pId].technicianAlarmStatus = 1;
							pat[pId].noOfFlipsToOff++;
							technicianSwitchOffRate++;
							pat[pId].technicianSwitchOffTime = tnow;
						}
				
						Event EventObj = new Event("departure", pat[pId].departureTime);
						TechTrueAlarmWorkload = TechTrueAlarmWorkload + TechTrueAlarmProsTime;
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
						
						pat[NextEvent.PatientID].rerouteDecision = "nurseTrue";
						/*pat[pId].ProcessDecision = "Processing";

						Event EventObjA = new Event("arrival", pat[pId].departureTime+ (- (Math.log(1 - Math.random()) /  lambda)));
						EventObjA.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObjA);*/
					}		
					else
					{
						pq.enqueue(NextEvent);
						noPatInQ++;
					}
				}
			}	
		}
		else if(pat[pId].alarmType == 1) //False Alarm
		{
			if(pat[pId].countDecision == "Count") {
				FalseAlarms++;
			}
			if(pat[pId].ProcessDecision == "Processing")
			{
				pat[pId].noOfFalseAlarm++;
				if(pat[pId].alarmMainType == "Technical") {
				pat[pId].noOfFalseAlarmTech++;
				}
				else pat[pId].noOfFalseAlarmNonTech++;
			
				if(pat[pId].alarmStatus == 1) //Alarm off
				{
					DodgedFalseAlarm++;
					Event EventObj = new Event("departure", NextEvent.objdTime);
					EventObj.PatientID = NextEvent.PatientID;
					eq.addEvent(EventObj);
				}
				else
				{
					pat[pId].whoProcessed = 0;
					if(noOfWorkingNurse < noOfNurse-1) {
						int  n = rand.nextInt(noOfNurse);			
						while(1==1) {
							if (nur[n].nurseStatus == 1) {
								n = rand.nextInt(noOfNurse);
							}
							else
								break;	
						}
						FalseAlarmsServiced++;
						nur[n].nurseStatus = 1; //Nurse occupied
						pat[pId].NurseVisibleFA++;
						pat[pId].patientStatus = 1;
						noOfWorkingNurse++;
						pat[pId].AlarmsServiced++;
						pat[pId].nurseid = n;
						pat[pId].serviceStartTime = NextEvent.objdTime;
						pat[pId].departureTime = FalseAlarmProsTime +  pat[pId].serviceStartTime;
					
						if(((pat[pId].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.004) {
							pat[pId].alarmStatus = 1;
							pat[pId].noOfFlipsToOff++;
							nurseSwitchOffRate++;
							pat[pId].SwitchOffTime = tnow;
						}
						
						Event EventObj = new Event("departure", pat[pId].departureTime);
						EventObj.PatientID = pId;
						eq.addEvent(EventObj);
						
						pat[NextEvent.PatientID].rerouteDecision = "technicianFalse";
						/*pat[pId].ProcessDecision = "Not Processing";

						Event EventObjA = new Event("arrival", pat[pId].departureTime+ (- (Math.log(1 - Math.random()) /  lambda)));
						EventObjA.PatientID = pId;
						eq.addEvent(EventObjA);*/
					}
					else
					{
						pq.enqueue(NextEvent);
						noPatInQ++;
					}
				}
			}
			else
			{
				if(pat[pId].technicianAlarmStatus == 1) //Alarm off
				{
					TechnicianAlarmMissed++;
					Event EventObj = new Event("departure", NextEvent.objdTime);
					EventObj.PatientID = NextEvent.PatientID;
					eq.addEvent(EventObj);
				}
				else
				{
					pat[pId].whoProcessed = 1;
					if(noOfWorkingTechnicians < noOfTechnician-1) {
						int  t = rand.nextInt(noOfTechnician);			
						while(1==1) {
							if (tech[t].technicianStatus == 1) {
								t = rand.nextInt(noOfTechnician);
							}
							else
								break;	
						}
					
						tech[t].technicianStatus = 1; //Technician occupied
						noOfWorkingTechnicians++;
						pat[pId].patientStatus = 1;
						pat[pId].technicianid = t;
						//pat[NextEvent.PatientID].whoProcessed = 1;
						pat[pId].serviceStartTime = NextEvent.objdTime;
						pat[pId].departureTime = pat[pId].arrivalTime + TechFalseAlarmProsTime ;

						FalseAlarmsIgnored++;
						
						if(((pat[pId].noOfFalseAlarm) / ((tnow-1) * 60 * 60)) > 0.008) {
							pat[pId].technicianAlarmStatus = 1;
							pat[pId].noOfFlipsToOff++;
							technicianSwitchOffRate++;
							pat[pId].technicianSwitchOffTime = tnow;
						}
					
						Event EventObj = new Event("departure", pat[pId].arrivalTime +  TechFalseAlarmProsTime);
						TechFalseAlarmWorkload = TechFalseAlarmWorkload + TechFalseAlarmProsTime;
						EventObj.PatientID = NextEvent.PatientID;
						eq.addEvent(EventObj);
					}
					else
					{
						pq.enqueue(NextEvent);
						noPatInQ++;
					}
				}
			}
		}
		}
	// else sysStatus--;	
}}


/*public void removeEvent(Customer c, CustomerQ cq)
{
int i=0;
while (i < cq.size()) {
	Event ch = cq.getithevent(i);
	Customer chcust = ch.customer;
	if (chcust.arrivalTime == c.arrivalTime){
		cq.eraseEventAti(i);
		break;	
	}
i ++;
}

} */




/**
 * Calculate some final stats and print
 * 
 * 
 * 
 */
/*public void StatsPrint()
{
//Print the update status
System.out.println("# of Service Completions = " + noServComp);
System.out.println("# of Service Abandonments = " + noServAb);
//avg time in system for those who completed service
avgTimeInsystem = totalTimeInSys/noServComp;
//second moment for time in system for those who completed service
double secondMomentTimeInsystem = totalTimeInSysSq/noServComp;	
//Variance of time in system
varTimeInsystem = secondMomentTimeInsystem - Math.pow(avgTimeInsystem,2);
//avg time in queue for those who completed service
avgTimeInQ = totalTimeInQ/noServComp;
//avg number of customers in system
avgNumberInSystem = totalNoInSys / endTime;
//Var number of customers in system
varNumberInSystem = totalNoInSysSq / endTime;
//avg number of customers in queue
avgNumberInQ = totalNoInQ / endTime;
//Var number of customers in queue
varNumberInQ = totalNoInQSq / endTime;
double totalcust = noServComp + noServAb;
percmeetTarget = noMeetTarget / totalcust;
//You can also display other statistics
System.out.println("Avg. time in system= " +avgTimeInsystem);
System.out.println("Variance of time in system= " +varTimeInsystem);
System.out.println("Avg. time in queue= " +avgTimeInQ);
System.out.println("Avg number in system= " +avgNumberInSystem);	
System.out.println("Var number in system= " +varNumberInSystem);
System.out.println("Avg number in queue= " +avgNumberInQ);
System.out.println("Var number in queue= " +varNumberInQ);
System.out.println("Number of customers who met the waiting time target = " +noMeetTarget);
System.out.println("Percentage of customers who met the target with =" +percmeetTarget );
}*/

}