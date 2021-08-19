import java.io.*;
import java.util.*;
//Reading files is similar as well with try and catch.
public class WritetoCSV {

      String objsfilePath = "";
      
	   public WritetoCSV()
	   {
		   //Hard coded path needs to handled properly
		   this.objsfilePath = "Data.csv";
	   }
	 
	 
	   public void generateCsvFileArr(ArrayList<double[]> poArrayList)
	   {
		try
		{
			File loFile = new File(this.objsfilePath);
			
			//Delete and create the file again
			if(loFile.exists())
			{
				loFile.delete();
				loFile.createNewFile();
			}
			
			double[] z = poArrayList.get(0);
			//Create a new file writer object and print all the vector elements as csv
			FileWriter loFileWriter = new FileWriter(loFile);
			for(int cnt = 0;cnt < z.length;cnt++)
			{
			
			if(cnt == 0)
			{
				loFileWriter.append("Random z");
			    loFileWriter.append('\n');
			}
			
	 
		    loFileWriter.append("" + z[cnt]);
		    loFileWriter.append('\n');
	 
		    //flush data to append the next one
		    loFileWriter.flush();
			}
		    
			//Better to close the file 
			loFileWriter.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	    }

		public void generateCsvFile(int day, int pID, int ps, double arrT, int alarmS, int alarmT, String alarmMT, int alarmST, int nTAT, int nFAT, int nTANT, int nFANT, String pd, double waitT, double depT, double sOff , int nFA,int nTA, int nid, int tid, int p, int nidTA, int nidFA, int fOn, int fOff, double timeInSys,File loFile)
		   {
			try
			{
				//Create a new file writer object and print all the vector elements as csv
				FileWriter loFileWriter = new FileWriter(loFile, true); //including true helps in appending, ow it deletes the file and adds fresh
					loFileWriter.append("" + day );
					loFileWriter.append(',');
					loFileWriter.append("" + pID );
					loFileWriter.append(',');
					loFileWriter.append("" + ps );
					loFileWriter.append(',');
				    loFileWriter.append("" + arrT );
					loFileWriter.append(',');
					loFileWriter.append("" + alarmS );
					loFileWriter.append(',');
					loFileWriter.append("" + alarmT );
					loFileWriter.append(',');	
					loFileWriter.append("" + alarmMT );
					loFileWriter.append(',');
					loFileWriter.append("" + alarmST );
					loFileWriter.append(',');
					loFileWriter.append("" + nTAT );
					loFileWriter.append(',');
					loFileWriter.append("" + nFAT );
					loFileWriter.append(',');
					loFileWriter.append("" + nTANT );
					loFileWriter.append(',');
					loFileWriter.append("" + nFANT );
					loFileWriter.append(',');
					loFileWriter.append("" + pd );
					loFileWriter.append(',');
					loFileWriter.append("" + waitT );
					loFileWriter.append(',');
					loFileWriter.append("" + depT );
					loFileWriter.append(',');
					loFileWriter.append("" + sOff );
					loFileWriter.append(',');
					loFileWriter.append("" + nFA );
					loFileWriter.append(',');
					loFileWriter.append("" + nTA );
					loFileWriter.append(',');
					loFileWriter.append("" + nid );
					loFileWriter.append(',');
					loFileWriter.append("" + tid );
					loFileWriter.append(',');
					loFileWriter.append("" + p );
					loFileWriter.append(',');
					loFileWriter.append("" + nidTA );
					loFileWriter.append(',');
					loFileWriter.append("" + nidFA );
					loFileWriter.append(',');
					loFileWriter.append("" + fOn );
					loFileWriter.append(',');
					loFileWriter.append("" + fOff );
					loFileWriter.append(',');
					loFileWriter.append("" + timeInSys );
					loFileWriter.append(',');
				    loFileWriter.append('\n');
		 
			    //flush data to append the next one
			    loFileWriter.flush();
							    
				//Better to close the file 
				loFileWriter.close();
			}
			catch(IOException e)
			{
			     e.printStackTrace();
			} 
		    } 
/* To write a vector to a CSV file
		public void generateCsvFileVect(Vector<Event> poEventQVect)
		   {
			try
			{
				File loFile = new File(this.objsfilePath);
				
				//Delete and create the file again
				if(loFile.exists())
				{
					loFile.delete();
					loFile.createNewFile();
				}
				
				
				//Create a new file writer object and print all the vector elements as csv
				FileWriter loFileWriter = new FileWriter(loFile);
				for(int cnt = 0;cnt < poEventQVect.size();cnt++)
				{
				
				if(cnt == 0)
				{
					loFileWriter.append("Event Type");
				    loFileWriter.append(',');
				    loFileWriter.append("Time");
				    loFileWriter.append(',');
				    loFileWriter.append("Customer ID");
				    loFileWriter.append(',');
				    loFileWriter.append('\n');
				}
			    Event loEventObj = poEventQVect.get(cnt);
		 
			    loFileWriter.append("" + loEventObj.objsType);
			    loFileWriter.append(',');
			    loFileWriter.append("" + loEventObj.objdTime);
			    loFileWriter.append(',');
			    loFileWriter.append("" +loEventObj.objnCustomerID);
			    loFileWriter.append(',');
			    loFileWriter.append('\n');
		 
			    //flush data to append the next one
			    loFileWriter.flush();
				}
			    
				//Better to close the file 
				loFileWriter.close();
			}
			catch(IOException e)
			{
			     e.printStackTrace();
			} 
		    }
	*/	
}


