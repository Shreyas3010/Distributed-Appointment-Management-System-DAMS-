	package Project.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Project.FinalVariables.*;
import Project.Model.AppointmentDetails;

public class AppointmentImplMontreal implements AppointmentInterface {
    
    //City
    
    public static final String MONTREAL="MTL";
    public static final String SHERBROOKE="SHE";
    public static final String QUEBEC="QUE";

    //TimeSlot Acronym
    
    public static final String MORNING="M";
    public static final String AFTERNOON="A";
    public static final String EVENING="E";
    
    // appointmentType
    
    public static final String DENTAL= "Dental";
    public static final String SURGEON= "Surgeon";
    public static final String PHYSICIAN= "Physician";
    
    String responseFromQuebec="";
    String responseFromSherbrooke="";
        
    public boolean UDPCallListAppointmentAvailability=false;
    public boolean UDPCallGetAppointmentSchedule=false;
    
    
    private static Logger logger = Logger.getLogger("MontrealServerLog");
    
    
    // --------- DATABASE FOR MONTREAL --------- // 
    
    public static HashMap<String, ConcurrentHashMap <String,AppointmentDetails>> montrealAppointmentDB = new HashMap<>();
    
    //Adding Test Data//
    
    static {
    	
		FileHandler fh;
		try {
		fh = new FileHandler("/Users/shreyaspatel/eclipse-workspace/COMP_6231_Assignment03/log/MontrealServer.log");
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		
		logger.info("Monteal Server Logger Initialized");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void printDB()  
    {
    	montrealAppointmentDB.entrySet().forEach( entry -> {
    	    System.out.println("<= " +entry.getKey() + " =>" );
    	    System.out.println("==============" );
    	    entry.getValue().entrySet().forEach(subEntry ->{
    	    	System.out.print("= "+subEntry.getKey() + " =" +subEntry.getValue());
    	    	
    	    });
    	    System.out.println("==============\n" );
    	});
    }
    // ----------------------------------------- //;
    
    
    //Helper Methods//
    
    public boolean hasAppointment(String patientID, String appointmentID, String appointmentType)
    {
    	AppointmentDetails tmpAppointmentDetails;
    	if(montrealAppointmentDB.get(appointmentType).containsKey(appointmentID))
    	{
			tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
			if(tmpAppointmentDetails.containsPatientId(patientID))
			{
				return true;
			}
    	}
    	
    	return false;
    }
    
    public boolean containsAppointmentWithSameTypeInTheSameDay(String patientID, String appointmentID, String appointmentType)
    {
		String tmpAppointmentID;
		AppointmentDetails tmpAppointmentDetails;
		if(appointmentID.charAt(3) == 'M' || appointmentID.charAt(3) == 'A')
		{
			tmpAppointmentID=appointmentID.substring(0,3)+"E"+appointmentID.substring(4, 10);
			if(montrealAppointmentDB.get(appointmentType).containsKey(tmpAppointmentID))
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(tmpAppointmentID);
				if(tmpAppointmentDetails.containsPatientId(patientID))
				{
					return true;
				}
			}
		}
    	
		if(appointmentID.charAt(3) == 'M' || appointmentID.charAt(3) == 'E')
		{
			tmpAppointmentID=appointmentID.substring(0,3)+"A"+appointmentID.substring(4, 10);
			if(montrealAppointmentDB.get(appointmentType).containsKey(tmpAppointmentID))
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(tmpAppointmentID);
				if(tmpAppointmentDetails.containsPatientId(patientID))
				{
					return true;
				}
			}
		}
		
		if(appointmentID.charAt(3) == 'A' || appointmentID.charAt(3) == 'E')
		{
			tmpAppointmentID=appointmentID.substring(0,3)+"M"+appointmentID.substring(4, 10);
			if(montrealAppointmentDB.get(appointmentType).containsKey(tmpAppointmentID))
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(tmpAppointmentID);
				if(tmpAppointmentDetails.containsPatientId(patientID))
				{
					return true;
				}
			}
		}
		return false;
    }

	public ArrayList<String> giveDatesOfAllDaysInTheWeek(String date) throws ParseException
	{
		
		SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
		
		Date givenDate = format.parse(date);
		 
		Calendar cal = Calendar.getInstance();
	 	cal.setTime(givenDate);
	 	 ArrayList<String> days = new ArrayList<String>();
	    int change = -cal.get(GregorianCalendar.DAY_OF_WEEK) + 1; 
	    //add 2 if your week start on monday and add 1 if first day is sunday.
		cal.add(Calendar.DAY_OF_MONTH, change );
		for (int i = 0; i < 7; i++)
		{
		    days.add(format.format(cal.getTime()));
		    cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return days;
	}
    
    public boolean alreadyHaveThreeAppointments(String patientID, String date) throws ParseException
    {
    	int counter=0;
    	if(patientID.substring(0, 3).equals(MONTREAL))
    	{
    		return false;
    	}
    	
	    AppointmentDetails tmpAppointmentDetails;
	    ArrayList<String>  datesInTheWeek =giveDatesOfAllDaysInTheWeek(date);
	    
		for ( String appointmentType : montrealAppointmentDB.keySet() ) 
		{
			for(String day : datesInTheWeek)
			{
				// Morning
				if(montrealAppointmentDB.get(appointmentType).containsKey(MONTREAL+MORNING+day))
				{
					tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(MONTREAL+MORNING+day);
					if(tmpAppointmentDetails.containsPatientId(patientID))
					{
						counter++;
						if(counter >=3)
						{
							return true;
						}
					}
				}
					
				// Afternoon
				if(montrealAppointmentDB.get(appointmentType).containsKey(MONTREAL+AFTERNOON+day))
				{
					tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(MONTREAL+AFTERNOON+day);
					if(tmpAppointmentDetails.containsPatientId(patientID))
					{
						counter++;
						if(counter >=3)
						{
							return true;
						}
					}
				}
				
				// Evening
				if(montrealAppointmentDB.get(appointmentType).containsKey(MONTREAL+EVENING+day))
				{
					tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(MONTREAL+EVENING+day);
					if(tmpAppointmentDetails.containsPatientId(patientID))
					{
						counter++;
						if(counter >=3)
						{
							return true;
						}
					}
				}
				
				
			}
		}

		return false;
    }
    
	    
	@Override
	public String addAppointment(String appointmentID, String appointmentType, int capacity) {
		
		logger.info("addAppointment -> appointmentID:"+appointmentID+", appointmentType:"+appointmentType+", capacity:"+capacity);
		String reply;
		if(!appointmentID.substring(0, 3).equals(MONTREAL))
		{
			reply="Failed: Cannot add appointment to other server";
		}
		else if(montrealAppointmentDB.get(appointmentType).containsKey(appointmentID))
		{
			reply="Failed: Appointment already exists! You cannot add it again";
		}
		else
		{
			montrealAppointmentDB.get(appointmentType).put(appointmentID, new AppointmentDetails(capacity,0,appointmentID,appointmentType));
			reply= "Success: Appointment " + appointmentID +" ("+appointmentType+")" + " is added successfully";
		}
		logger.info(reply);
		return reply;
	}

	@Override
	public String removeAppointment(String appointmentID, String appointmentType) throws ParseException {
		String reply;
		String tmpReply;
		String removedPatientIDsString;
		logger.info("removeAppointment -> appointmentID:"+appointmentID+", appointmentType:"+appointmentType);
		if(!appointmentID.substring(0, 3).equals(MONTREAL))
		{
			reply="Failed: Cannot remove appointment from other server";
			logger.info(reply);
		}
		else if(!montrealAppointmentDB.get(appointmentType).containsKey(appointmentID))
		{
			reply="Failed: Appointment does not exist.";
			logger.info(reply);
		}
		else
		{
			AppointmentDetails removedAppointmentDetails=montrealAppointmentDB.get(appointmentType).remove(appointmentID);
			reply= "Success: Appointment for " + appointmentID +" ("+appointmentType+")" + " is removed";
			logger.info(reply);
			ArrayList<String> removedPatientIDs=removedAppointmentDetails.getListOfBooking();
			
			logger.info("patientIds whose appointment got cancelled as appointment got removed - "+removedPatientIDs);
			
			if(!removedPatientIDs.isEmpty())
			{
				removedPatientIDs=bookNextAvaiableSlot(removedPatientIDs,appointmentType);

				logger.info("remaining patientIds after trying to book the next available appointment in Montreal for those removed patients - "+removedPatientIDs);

			}

			// call bookNextAvailableSlot in Quebec Server
			if(!removedPatientIDs.isEmpty())
			{
				removedPatientIDsString = removedPatientIDs.toString();
				removedPatientIDsString= removedPatientIDsString.replace("[", "").replace("]", "").replace(" ", "");
				
				tmpReply=this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT, "bookNextAvaiableSlot", removedPatientIDsString, "NULL", appointmentType);
				if(tmpReply.length() ==0)
				{
					removedPatientIDs.clear();
				}
				else
				{
					removedPatientIDs = new ArrayList<>(Arrays.asList(tmpReply.split(",")));
				}
				
				logger.info("remaining patientIds after trying to book the next available appointment in Quebec for those removed patients - "+removedPatientIDs);
			}
			
			// call bookNextAvailableSlot in Sherbrooke Server

			if(!removedPatientIDs.isEmpty())
			{

				removedPatientIDsString = removedPatientIDs.toString();
				removedPatientIDsString= removedPatientIDsString.replace("[", "").replace("]", "").replace(" ", "");
				
				tmpReply=this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT, "bookNextAvaiableSlot", removedPatientIDsString, "NULL", appointmentType);
				if(tmpReply.length() ==0)
				{
					removedPatientIDs.clear();
				}
				else
				{
					removedPatientIDs = new ArrayList<>(Arrays.asList(tmpReply.split(",")));
				}

				logger.info("remaining patientIds after trying to book the next available appointment in Sherbrooke for that removed patient - "+removedPatientIDs);
			}
			
			// logger about the patients whose booking can not be possible
			if(!removedPatientIDs.isEmpty())
			{
				logger.info("remained patientIds who did not get any appointment - "+removedPatientIDs);
			}
			
		}
		return reply;
	}

	public ArrayList<String> bookNextAvaiableSlot(ArrayList<String> removedPatientIDs, String appointmentType) throws ParseException
	{
		int index=0;
		AppointmentDetails tmpAppointmentDetails;
		int replyFromObject;
		if(montrealAppointmentDB.containsKey(appointmentType))
		{
			for(String appointmentID :montrealAppointmentDB.get(appointmentType).keySet())
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
				index=0;
				while(!tmpAppointmentDetails.isBookingFull())
				{
					if(removedPatientIDs.size() == index)
					{
						break;
					}
					if(removedPatientIDs.isEmpty())
					{
						break;
					}
					if(!this.containsAppointmentWithSameTypeInTheSameDay(removedPatientIDs.get(index),appointmentID,appointmentType))
					{
						if(!removedPatientIDs.get(index).substring(0, 3).equals(MONTREAL) && this.alreadyHaveThreeAppointments(removedPatientIDs.get(index),appointmentID.substring(4, 10)))
						{
							index++;
						}
						else
						{
							replyFromObject=tmpAppointmentDetails.addPatientToBooking(removedPatientIDs.get(index));
							
							if(replyFromObject == 0)
							{
								index++;
							}
							else if(replyFromObject == 1)
							{
								logger.info(" removed patient -> "+removedPatientIDs.get(index)+" (from removed appointment) got booking at -> "+appointmentID+" ("+appointmentType+")");
								removedPatientIDs.remove(index);
							}
							else if(replyFromObject == -1)
							{
								break;
							}
						}
					}
					else
					{
						index++;
					}
					
					
				}
				if(removedPatientIDs.isEmpty())
				{
					break;
				}
			}
		}
		
		return removedPatientIDs;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public String listAppointmentAvailability(String appointmentType) throws InterruptedException {
		String reply="";
	    responseFromQuebec="";
	    responseFromSherbrooke="";
	    
	    logger.info("listAppointmentAvailability -> appointmentType:"+appointmentType);
		if(montrealAppointmentDB.containsKey(appointmentType))
		{
			for (ConcurrentHashMap.Entry element : montrealAppointmentDB.get(appointmentType).entrySet()) {
	            reply=reply+"Success: "+element.getKey()+" :-> "+String.valueOf(((AppointmentDetails) element.getValue()).availableBookingSpace());
	        }
		}
		if(!this.UDPCallListAppointmentAvailability)
		{
			// Quebec UDP call
		
			logger.info(" UDP call to Quebec Server listAppointmentAvailability -> appointmentType:"+appointmentType);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"listAppointmentAvailability","NULL", "NULL",appointmentType);
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();
			
			// Sherbrooke UDP call
			
	        logger.info(" UDP call to Sherbrooke Server listAppointmentAvailability -> appointmentType:"+appointmentType);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"listAppointmentAvailability","NULL", "NULL",appointmentType);
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	       
	        QuebecThread.join();
	        logger.info("reply from Quebec Server ->"+responseFromQuebec);
	        SherbrookeThread.join();
	        logger.info("reply from Sherbrooke Server ->"+responseFromSherbrooke);
		}		

		logger.info(reply+responseFromQuebec+responseFromSherbrooke);
		if(reply.trim().isEmpty() && responseFromSherbrooke.trim().isEmpty() && responseFromQuebec.trim().isEmpty())
		{
			return " Failed: There is no Appointment with AppointmentType:"+appointmentType;
		}
		return reply+responseFromQuebec+responseFromSherbrooke;
	}

	@Override
	public String bookAppointment(String patientID, String appointmentID, String appointmentType) throws ParseException, InterruptedException 
	{
		String reply = ""; int replyFromObject;
	    responseFromQuebec="";
	    responseFromSherbrooke="";
		
		logger.info("bookAppointment -> patientId:"+patientID+", appointmentID"+appointmentID+", appointmentType"+appointmentType);
			if(appointmentID.substring(0, 3).equals(MONTREAL))
			{
				if(!montrealAppointmentDB.get(appointmentType).containsKey(appointmentID))
				{
					reply="Failed: Appointment does not exist.";
				}
				else if(this.containsAppointmentWithSameTypeInTheSameDay(patientID,appointmentID,appointmentType))
				{
					reply="Failed: more than one booking of same appointment type in a day is not allowed.";
				}
				else if( !patientID.substring(0, 3).equals(MONTREAL) && this.alreadyHaveThreeAppointments(patientID,appointmentID.substring(4, 10)))
				{
					reply="Failed: more than three bookings in a week are not allowed in other than own city.";
				}
				else if(montrealAppointmentDB.get(appointmentType).get(appointmentID).isBookingFull())
				{
					reply="Failed: Appointments are fully booked.";
				}
				else
				{	
					AppointmentDetails appointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
					replyFromObject=appointmentDetails.addPatientToBooking(patientID);
					if(replyFromObject == -1)
					{
						reply="Failed: Appointments are full booked.";
					}
					else if(replyFromObject == 0)
					{
						reply="Failed: Appointment is already booked.";
					}
					else
					{
						 //reply= "Success: Appointment " + appointmentID +" ("+appointmentType+")" + " is booked successfully in Montreal Server";
						reply="Success: you successfully booked the appointment for ("+appointmentType+") with id "+appointmentID;
					}
				}
			}
			else if(appointmentID.substring(0, 3).equals(SHERBROOKE))
			{
				// Sherbrooke UDP call
		        logger.info("UDP Calling bookAppointment to sherbrooke server -> patientID:"+patientID+", appointmentID"+appointmentID+", appointmentType"+appointmentType);
		        Runnable SherbrookeTask = () -> {
		        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"bookAppointment",patientID,appointmentID, appointmentType);
		        };
		        Thread SherbrookeThread = new Thread(SherbrookeTask);
		        SherbrookeThread.start();	
		        
		        SherbrookeThread.join();
		        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
		        reply=responseFromSherbrooke;
				return reply;
			}
			else
			{				
				
				// Quebec UDP call
				logger.info("UDP Calling bookAppointment to quebec server -> patientID:"+patientID+", appointmentID"+appointmentID+", appointmentType"+appointmentType);
		        Runnable QuebecTask = () -> {
		        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"bookAppointment",patientID, appointmentID, appointmentType);
			        
		        };
		        Thread QuebecThread = new Thread(QuebecTask);
		        QuebecThread.start();
		        
		        QuebecThread.join();
		        logger.info("response from Quebec Server ->"+responseFromQuebec);
		        reply=responseFromQuebec;
		        
				return reply;
			}
		
		logger.info(reply);
		return reply;
	}

	@Override
	public String getAppointmentSchedule(String patientID) throws InterruptedException {
		String reply="";
	    responseFromQuebec="";
	    responseFromSherbrooke="";
	    AppointmentDetails tmpAppointmentDetails;
	    
	    logger.info("getAppointmentSchedule -> patientID:"+patientID);
		for ( String appointmentType : montrealAppointmentDB.keySet() ) 
		{
			for(String appointmentID :montrealAppointmentDB.get(appointmentType).keySet())
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
				if(tmpAppointmentDetails.containsPatientId(patientID))
				{
					reply=reply+" ( "+appointmentID+" - "+appointmentType+" ) ";
				}
			}
		}
		
		if(!this.UDPCallGetAppointmentSchedule)
		{
			// Quebec UDP call
			logger.info("UDP Calling getAppointmentSchedule to quebec server -> patientID:"+patientID);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"getAppointmentSchedule",patientID, "NULL","NULL");
		        
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();
			
			// Sherbrooke UDP call
	        logger.info("UDP Calling getAppointmentSchedule to sherbrooke server -> patientID:"+patientID);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"getAppointmentSchedule",patientID,"NULL", "NULL");
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	       
	        QuebecThread.join();
	        logger.info("response from Quebec Server ->"+responseFromQuebec);
	        responseFromQuebec = "\n"+responseFromQuebec;
	        SherbrookeThread.join();
	        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
	        responseFromSherbrooke="\n"+responseFromSherbrooke;
		}	
		if(reply.isEmpty())
		{
			reply=reply+"You donot have any appointment.";
		}
		reply="-- Montreal --\n"+reply;
		
		logger.info(reply+responseFromQuebec+responseFromSherbrooke);
		return reply+responseFromQuebec+responseFromSherbrooke;		
	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID) throws InterruptedException {
		
		logger.info("cancelAppintment -> patientID:"+patientID+", appointmentID:"+appointmentID);
		String reply="";
	    responseFromQuebec="";
	    responseFromSherbrooke="";
	    
		AppointmentDetails tmpAppointmentDetails;
		if(appointmentID.substring(0, 3).equals(MONTREAL))
		{
			for ( String appointmentType : montrealAppointmentDB.keySet() ) 
			{
				if(montrealAppointmentDB.get(appointmentType).containsKey(appointmentID))
				{
					tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
					if(tmpAppointmentDetails.removePatientToBooking(patientID) == 1)
					{
						reply=reply+"Success: Appointment ( "+appointmentID+" : "+appointmentType+" ) Cancelled Successfully\n";
					}
				}
			}
		}
		else if(appointmentID.substring(0, 3).equals(SHERBROOKE))
		{
			// Sherbrooke UDP call
	        logger.info("UDP Calling cancelAppointment to sherbrooke server -> patientID:"+patientID+", appointmentID"+appointmentID);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"cancelAppointment",patientID,appointmentID,"NULL");
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	        
	        SherbrookeThread.join();
	        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
	        reply=responseFromSherbrooke;
			return reply;
		}
		else
		{
			// Quebec UDP call
			logger.info("UDP Calling cancelAppointment to quebec server -> patientID:"+patientID+", appointmentID:"+appointmentID);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"cancelAppointment",patientID, appointmentID,"NULL");
		        
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();
	        
	        QuebecThread.join();
	        logger.info("response from Quebec Server ->"+responseFromQuebec);
	        reply=responseFromQuebec;
	        
			return reply;
		}
		if(reply.isEmpty())
		{
			reply=reply+"Failed: Appointment For " + appointmentID + " Does Not Exist.";
		}
		logger.info(reply);
	return reply;
	}

	//Helper
	
	public boolean cancelAppointmentHelper(String patientID, String oldAppointmentID, String oldAppointmentType) throws InterruptedException
	{
		
	    responseFromQuebec="";
	    responseFromSherbrooke="";
	    
		AppointmentDetails tmpAppointmentDetails;
		if(oldAppointmentID.substring(0, 3).equals(MONTREAL))
		{
			if(montrealAppointmentDB.get(oldAppointmentType).containsKey(oldAppointmentID))
			{
				tmpAppointmentDetails=montrealAppointmentDB.get(oldAppointmentType).get(oldAppointmentID);
				if(tmpAppointmentDetails.removePatientToBooking(patientID) == 1)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			
		}
		else if(oldAppointmentID.substring(0, 3).equals(SHERBROOKE))
		{
			// Sherbrooke UDP call
	        logger.info("UDP Calling cancelAppointmentHelper to sherbrooke server -> patientID:"+patientID+", oldAppointmentID:"+oldAppointmentID+", oldAppointmentType:"+oldAppointmentType);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"cancelAppointmentHelper",patientID,oldAppointmentID,oldAppointmentType);
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	        
	        SherbrookeThread.join();
	        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
	        
	        return Boolean.parseBoolean(responseFromSherbrooke.trim());
		}
		else
		{
			// Quebec UDP call
			logger.info("UDP Calling cancelAppointmentHelper to quebec server -> patientID:"+patientID+", oldAppointmentID:"+oldAppointmentID+", oldAppointmentType:"+oldAppointmentType);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"cancelAppointmentHelper",patientID, oldAppointmentID,oldAppointmentType);
		        
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();
	        
	        QuebecThread.join();
	        
	        logger.info("response from Quebec Server ->"+responseFromQuebec);
			return Boolean.parseBoolean(responseFromQuebec.trim());
		}

		return false;
	}
	
	public boolean replaceWithDummyPatient(String patientID, String appointmentID, String appointmentType) throws InterruptedException
	{
    	AppointmentDetails tmpAppointmentDetails;
		
	    responseFromQuebec="";
	    responseFromSherbrooke="";
	    
		if(appointmentID.substring(0, 3).equals(MONTREAL))
		{
			tmpAppointmentDetails=montrealAppointmentDB.get(appointmentType).get(appointmentID);
			if(patientID.length() < 10)
			{
				return tmpAppointmentDetails.replacePatientId(patientID, "DUMMY"+patientID);
			}
			else
			{
				return tmpAppointmentDetails.replacePatientId(patientID,patientID.substring(5));
			}
						
		}
		else if(appointmentID.substring(0, 3).equals(SHERBROOKE))
		{
			// Sherbrooke UDP call
	        logger.info("UDP Calling replaceWithDummyPatient to sherbrooke server -> patientID:"+patientID+", AppointmentID:"+appointmentID+", oldAppointmentType:"+appointmentType);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"replaceWithDummyPatient",patientID,appointmentID,appointmentType);
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	        
	        SherbrookeThread.join();
	        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
	        
	        return Boolean.parseBoolean(responseFromSherbrooke.trim());
		}
		else
		{
			// Quebec UDP call
			logger.info("UDP Calling replaceWithDummyPatient to quebec server -> patientID:"+patientID+", appointmentID:"+appointmentID+", appointmentType:"+appointmentType);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"replaceWithDummyPatient",patientID, appointmentID,appointmentType);
		        
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();
	        
	        QuebecThread.join();
	        
	        logger.info("response from Quebec Server ->"+responseFromQuebec);
			return Boolean.parseBoolean(responseFromQuebec.trim());
		}

	}
	
	
	
	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType) throws InterruptedException, ParseException {
		
		boolean performNoOperation;
		String reply="";
		
		if(oldAppointmentID.substring(0, 3).equals(MONTREAL))
		{
			if(!this.hasAppointment(patientID, oldAppointmentID, oldAppointmentType))
			{
				logger.info("Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.");
				return "Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.";
			}
			
			
		}
		else if(oldAppointmentID.substring(0, 3).equals(SHERBROOKE))
		{
			// Sherbrooke UDP call hasAppointment
	        logger.info("UDP Calling hasAppointment to sherbrooke server -> patientID:"+patientID+", oldAppointmentID:"+oldAppointmentID+", oldAppointmentType:"+oldAppointmentType);
	        Runnable SherbrookeTask = () -> {
	        	responseFromSherbrooke=" "+this.sendUDPRequest(FinalVariables.SHERBROOKE_INTER_SERVER_PORT,"hasAppointment",patientID,oldAppointmentID,oldAppointmentType);
	        };
	        Thread SherbrookeThread = new Thread(SherbrookeTask);
	        SherbrookeThread.start();	
	        
	        SherbrookeThread.join();
	        logger.info("response from Sherbrooke Server ->"+responseFromSherbrooke);
	        
	        if(!Boolean.parseBoolean(responseFromSherbrooke.trim()))
	        {
				logger.info("Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.");
				return "Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.";

	        }
	        


		}
		else
		{
			// Quebec UDP call hasAppointment
			
	        logger.info("UDP Calling hasAppointment to Quebec server -> patientID:"+patientID+", oldAppointmentID:"+oldAppointmentID+", oldAppointmentType:"+oldAppointmentType);
	        Runnable QuebecTask = () -> {
	        	responseFromQuebec=" "+this.sendUDPRequest(FinalVariables.QUEBEC_INTER_SERVER_PORT,"hasAppointment",patientID,oldAppointmentID,oldAppointmentType);
	        };
	        Thread QuebecThread = new Thread(QuebecTask);
	        QuebecThread.start();	
	        
	        QuebecThread.join();
	        logger.info("response from Quebec Server ->"+responseFromQuebec);
	        
	        if(!Boolean.parseBoolean(responseFromQuebec.trim()))
	        {
				logger.info("Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.");
				return "Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.";

	        }
	        
			
		}
		

		// replaceWithDummyPatient
			
		if(!this.replaceWithDummyPatient(patientID, oldAppointmentID, oldAppointmentType))
		{
			logger.info("Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.");
			return "Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") has not already booked.";

		}
		
		
		// bookAppointment-inside
		
		reply= this.bookAppointment(patientID, newAppointmentID, newAppointmentType);
		if(reply.contains("Failed:"))
		{
			performNoOperation =false;
			while(!performNoOperation)
			{
				performNoOperation=this.replaceWithDummyPatient("DUMMY"+patientID, oldAppointmentID, oldAppointmentType);
			}
			
			logger.info("Failed: Swap Appointment can not be performed as appointment (" + newAppointmentID+" , "+ newAppointmentType+ ") can not be booked.");
			return "Failed: Swap Appointment can not be performed as appointment (" + newAppointmentID+" , "+ newAppointmentType+ ") can not be booked.";
		}
		
		// cancelAppointment
		if(this.cancelAppointmentHelper("DUMMY"+patientID, oldAppointmentID, oldAppointmentType))
		{
			reply="Success: Swap Appointment ( "+oldAppointmentID+" : "+oldAppointmentType+" ) => ( "+newAppointmentID+" : "+newAppointmentType+" ) has performed Successfully\n";
		}
		else
		{
			performNoOperation =false;
			while(!performNoOperation)
			{
				performNoOperation=this.replaceWithDummyPatient("DUMMY"+patientID, oldAppointmentID, oldAppointmentType);
			}
			
			performNoOperation =false;
			while(!performNoOperation)
			{
				performNoOperation=this.cancelAppointmentHelper(patientID, newAppointmentID, newAppointmentType);
			}
			
			reply="Failed: Swap Appointment can not be performed as appointment (" + oldAppointmentID+" , "+ oldAppointmentType+ ") can not be cancelled.";
			
		}
		
		logger.info(reply);
		return reply;
	}
	
	private String sendUDPRequest(int serverPort, String method, String patientID, String appointmentID, String appointmentType)
	{
        DatagramSocket aSocket = null;
        String reply = "";
        String dataFromClient = method + ";" + patientID + ";" + appointmentID + ";" + appointmentType;
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket result = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(result);
            reply = new String(result.getData());

            String[] parts = reply.split(";");
            reply = parts[0];


        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return reply;

	}

	public static void initializeDatabase()
	{
    	montrealAppointmentDB.put(DENTAL, new ConcurrentHashMap <String,AppointmentDetails>());
       	montrealAppointmentDB.get(DENTAL).put("MTLM010522", new AppointmentDetails(15,0,"MTLM010522",DENTAL));
    	
    	montrealAppointmentDB.put(SURGEON, new ConcurrentHashMap <String,AppointmentDetails>());
    	montrealAppointmentDB.get(SURGEON).put("MTLM010522", new AppointmentDetails(10,0,"MTLM010522",SURGEON));
    	
    	montrealAppointmentDB.put(PHYSICIAN, new ConcurrentHashMap <String,AppointmentDetails>());  
    	montrealAppointmentDB.get(PHYSICIAN).put("MTLM010522", new AppointmentDetails(12,0,"MTLM010522",PHYSICIAN));
	}
	public static void main(String [] args)
	{
		//printDB();
	}
	
}
