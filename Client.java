package Project.Client;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import Project.Authenticate.Authentication;
import Project.FrontEnd.FrontEndInterface;
import Project.FinalVariables.*;

public class Client {
	
    
    //City Acronym
    public static final int MTL = 1;
    public static final int QUE = 2;
    public static final int SHE = 3;
    
    //City
    
    public static final String MONTREAL="MTL";
    public static final String SHERBROOKE="SHE";
    public static final String QUEBEC="QUE";

    //User type
    public static final int ADMIN = 1;
    public static final int PATIENT = 2;
	
    //TimeSlot    
    public static final int MORNING = 1;
    public static final int AFTERNOON = 2;
    public static final int EVENING = 3;
    
    //Appointment Type
    public static final int PHYSICIAN = 1;
    public static final int SURGEON = 2;
    public static final int DENTAL = 3;
    
    //Menu Options
    public static final int BOOK_APPOINTMENT = 1;
    public static final int GET_APPOINTMENT_SCDEDULE = 2;
    public static final int CANCEL_APPOINTMENT = 3;
    public static final int SWAP_APPOINTMENT = 4;
    public static final int ADD_APPOINTMENT = 5;
    public static final int REMOVE_APPOINTMENT = 6;
    public static final int LIST_APPOINTMENT_AVAILABILITY = 7;
    public static final int EXIT = 8;
    
	 
	String userId = "";
	boolean isUserPatient;
	boolean exit;
	int capacity;
	String appointmentType;
	String appointmentID;
	int choice;
	int tmpChoice;
	String tmpInput;
	String tmpId;
	
	
	
	
	
	private static Logger logger = Logger.getLogger("ClientLog");
	
	//Constructor
	Client() throws NotBoundException, SecurityException, IOException
	{
		Scanner scanInput = new Scanner(System.in);
		
		while(true)
		{
			this.exit=false;
			this.userId=this.askForCity();
			String password;
			
			this.userId= this.userId+this.askForUserType();
			
			this.userId= this.userId+this.askForFourDigitId();
			
			System.out.println("\nYour ID is "+this.userId);
			System.out.println("Please enter your password.");
			password= scanInput.nextLine().trim();
			


			Authentication authenticationService = new Authentication();
					
			if(!authenticationService.login(this.userId, password, this.isUserPatient))
			{
				System.out.println("Wrong password or Id. \n Please try again");
			}
			else
			{
				System.out.println("Successfully logged in !!");
				break;
			}
		}
		
		FileHandler fh;
		
		fh = new FileHandler("/Users/shreyaspatel/eclipse-workspace/COMP_6231_Assignment03/log/"+this.userId+".log");
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		
		logger.info("Client ("+this.userId+") Logger Initialized");
		
	}
	
	
	//AskFor
	String askForCity()
	{
		Scanner scanInput = new Scanner(System.in);
		
		System.out.println("Please Enter City: /n 1. Montreal /n 2. Quebec /n 3. Sherbrooke");
		
		this.tmpInput= scanInput.nextLine().trim();
		if(!this.validDigit(this.tmpInput,1)) 
		{
			System.out.println("Your choice is not appropriate.");
			return this.askForCity();
		}
		this.tmpChoice=Integer.valueOf(this.tmpInput);
		
		switch (this.tmpChoice) 
		{
			case MTL:
				return MONTREAL;
			case QUE:
				return QUEBEC;
			case SHE:
				return SHERBROOKE;
		    default:
				System.out.println("Your choice is not appropriate.");
				return this.askForCity();
		}
	}
	
	String askForUserType () throws NumberFormatException
	{
		Scanner scanInput = new Scanner(System.in);
		System.out.println("Please Enter 1. Admin or 2. Patient ?");
		this.tmpInput= scanInput.nextLine().trim();
		if(!this.validDigit(this.tmpInput,1)) 
		{
			System.out.println("Your choice is not appropriate.");
			return this.askForUserType();
		}
		this.tmpChoice=Integer.valueOf(this.tmpInput);
		
		switch (this.tmpChoice) 
		{
			case ADMIN:
				this.isUserPatient=false;
				return "A";
			case PATIENT:
				this.isUserPatient=true;
				return "P";
			default:
				System.out.println("Your choice is not appropriate.");
				return this.askForUserType();
		}
	}
	
	String askForFourDigitId()
	{
		Scanner scanInput = new Scanner(System.in);
		System.out.println("Please Enter ID (4 digit) (e.g. 1234): ");
		this.tmpInput= scanInput.nextLine().trim();
		
		if(this.validDigit(this.tmpInput,4))
		{
			return this.tmpInput;
		}
		else
		{
			System.out.println("Entered ID is not appropriate.");
			return this.askForFourDigitId();
		}
	}
	
	String askForTimeSlot()
	{
		Scanner scanInput = new Scanner(System.in);
		System.out.println("Please Enter time slot: /n 1. Morning /n 2. Afternoon /n 3. Evening");
		this.tmpInput= scanInput.nextLine().trim();
		if(!this.validDigit(this.tmpInput,1)) 
		{
			System.out.println("Your choice is not appropriate.");
			return this.askForTimeSlot();
		}
		this.tmpChoice=Integer.valueOf(this.tmpInput);
		
		switch (this.tmpChoice) 
		{
			case MORNING:
				return "M";
			case AFTERNOON:
				return "A";
			case EVENING:
				return "E";
			default:
				System.out.println("Your choice is not appropriate.");
				return this.askForTimeSlot();
		}

	}
	
	public boolean isDateValid(String dataString, String time)
	{
        DateFormat sdf = new SimpleDateFormat("ddMMyy HHmmss");
        
        Date givenDate;
        Date todaysDate = new Date();  

        sdf.setLenient(false);
        try {
            givenDate=sdf.parse(dataString+" "+time);
            
            if(todaysDate.before(givenDate))
            {
            	//System.out.println("Your desired date "+givenDate+" is after (current date and time) "+todaysDate);
            }
            else
            {
            	System.out.println("Your desired date "+givenDate+" is not after (current date and time) "+todaysDate);
            	return false;
            }
        } catch (ParseException e) {
        	System.out.println("Your desired date is not valid.");
            return false;
        }
        return true;
	}
	
	String returnTime(String timeSlot)
	{
		if(timeSlot.equals("M"))
		{
			return "080000";
		}
		else if(timeSlot.equals("A"))
		{
			return "120000";
		}
		else if(timeSlot.equals("E"))
		{
			return "160000";
		}
		
		return "000000";
	}
	
	String askForDate(boolean checkWithCurrentTimeAndDate, String timeSlot)
	{
		Scanner scanInput = new Scanner(System.in);
		
		System.out.println("Please Enter desired date (DDMMYY) (e.g. 280222): ");
		this.tmpInput= scanInput.nextLine().trim();
		
		if(this.validDigit(this.tmpInput,6)) 
		{
			if(checkWithCurrentTimeAndDate && !this.isDateValid(this.tmpInput,this.returnTime(timeSlot)))
			{
				return this.askForDate(checkWithCurrentTimeAndDate,timeSlot);
			}
			return this.tmpInput;
		}
		else
		{
			System.out.println("desired date is not appropriate(it can only contain 6 digits).");
			return this.askForDate(checkWithCurrentTimeAndDate,timeSlot);
		}
	}
	
	String askForAppointmentType()
	{
		Scanner scanInput = new Scanner(System.in);
		System.out.println("Please Enter appointmentType. \n1. Physician\n2. Surgeon\n3. Dental");
		this.tmpInput= scanInput.nextLine().trim();
		if(!this.validDigit(this.tmpInput,1)) 
		{
			System.out.println("Your choice is not appropriate.");
			return this.askForAppointmentType();
		}
		this.tmpChoice=Integer.valueOf(this.tmpInput);
	
		switch (this.tmpChoice) 
		{
			case PHYSICIAN:
				return "Physician";
			case SURGEON:
				return "Surgeon";
			case DENTAL:
				return "Dental";
			default:
				System.out.println("Your choice is not appropriate.");
				return this.askForAppointmentType();
		}
	}
	
	String askForAppoinmentId()
	{
		Scanner scanInput = new Scanner(System.in);
		  System.out.println("Please Enter appointmentID.(e.g. MTLA280222)");
		  this.tmpInput= scanInput.nextLine().trim();
		  if(validAppointmentId(this.tmpInput))
		  {
			  return this.tmpInput;
		  }
		  
		  System.out.println("Entered appointmentID is not appropriate");
		  return this.askForAppoinmentId();
	}
	
	int askForCapacity()
	{
		Scanner scanInput = new Scanner(System.in);
		System.out.println("Please Enter capacity. ");
		this.tmpInput= scanInput.nextLine().trim();
		if(this.tmpInput.matches("[0-9]+") && Integer.parseInt(this.tmpInput) >0)
		{
			return Integer.parseInt(this.tmpInput);
		}
		else
		{
			System.out.println("Entered capacity is not appropriate. ");
			return this.askForCapacity();
		}
		
	}
	
	
	//Validation
	public boolean validUserId(String userIdToBeChecked)
	{
		if(userIdToBeChecked.length()!= 8 || !(MONTREAL.equals(userIdToBeChecked.substring(0,3)) || QUEBEC.equals(userIdToBeChecked.substring(0,3)) || SHERBROOKE.equals(userIdToBeChecked.substring(0,3))) || !(userIdToBeChecked.charAt(3)=='P' || userIdToBeChecked.charAt(3)=='A'))
		{
			return false;
		}
		
		return this.validDigit(userIdToBeChecked.substring(4,8), 4);
	}

	public boolean validAppointmentId(String AppointmentIdToBeChecked)
	{
		if(AppointmentIdToBeChecked.length()!= 10 || !(MONTREAL.equals(AppointmentIdToBeChecked.substring(0,3)) || QUEBEC.equals(AppointmentIdToBeChecked.substring(0,3)) || SHERBROOKE.equals(AppointmentIdToBeChecked.substring(0,3))) || !(AppointmentIdToBeChecked.charAt(3)=='M' || AppointmentIdToBeChecked.charAt(3)=='A' || AppointmentIdToBeChecked.charAt(3)=='E'))
		{
			return false;
		}
		
		return this.validDigit(AppointmentIdToBeChecked.substring(4,10), 6);
	}
	
	
	public boolean validDigit(String userIdToBeChecked,int size)
	{
		if(userIdToBeChecked.length()!=size)
		{
			return false;
		}
		
		return userIdToBeChecked.matches("[0-9]+");
	}
	
	
	public static void main(String[] args) throws NotBoundException, InterruptedException, ParseException, SecurityException, IOException, ExecutionException, TimeoutException
	{
	
		
		Scanner scanInput = new Scanner(System.in);
		Client newClient = new Client();
		String reply="";
		
		Authentication authenticationService = new Authentication();
		
		
		// Montreal
		
		URL urlServerService = new URL("http://"+FinalVariables.FRONT_IP_ADDRESS+":"+FinalVariables.FRONT_PORT_WS+"/FrontEnd?wsdl");
		
		QName qNameServerService= new QName("http://FrontEnd.Project/","FrontEndImplService");
		
		Service service = Service.create(urlServerService, qNameServerService);
		
		FrontEndInterface serverService = service.getPort(FrontEndInterface.class);

		
			
		
		while(!newClient.exit)
		{
			System.out.println("\n=========================================================");
			System.out.println("Please select the proper choice from below operations !");
			System.out.println("=========================================================");
			System.out.println("1. bookAppointment \n2. getAppointmentSchedule \n3. cancelAppointment \n4. swapAppointment");
			if(!newClient.isUserPatient)
			{
				System.out.println("5. addAppointment \n6. removeAppointment \n7.listAppointmentAvailability");
			}
			System.out.println("8. Exit\n");
			
			newClient.choice= Integer.valueOf(scanInput.nextLine());
			
			
			switch (newClient.choice) 
			{
			
			  // bookAppointment
			  case BOOK_APPOINTMENT:
			  {
				  if(!newClient.isUserPatient)
				  {
					  while(true)
					  {
						  System.out.println("Please enter the patientId on whose behalf you want to perform this patient operation.");
						  newClient.tmpId= scanInput.nextLine().trim();
						  if(!authenticationService.isPatientIdValid(newClient.tmpId))
						  {
							  System.out.println("Given patientId is not valid");
						  }
						  else
						  {
							  break;
						  }
					  }
				  }
				  
				  newClient.appointmentID= newClient.askForCity();
				  newClient.appointmentID= newClient.appointmentID+newClient.askForTimeSlot();
				  newClient.appointmentID= newClient.appointmentID+newClient.askForDate(true,newClient.appointmentID.substring(3, 4));
								
				  newClient.appointmentType=newClient.askForAppointmentType();
					
				  //System.out.println("\nYour appointmentID is "+newClient.appointmentID+ " AppointmentType: "+newClient.appointmentType);
				  if(newClient.isUserPatient)
				  {
					  logger.info("Book Appoinment -> patientId:"+newClient.userId+" appointmentID:"+newClient.appointmentID+" appointmentType:"+newClient.appointmentType);
				  }
				  else
				  {
					  logger.info("admin ("+newClient.userId+") is performing patient operation on behalf of patient ("+newClient.tmpId+")");
					  logger.info("Book Appoinment -> patientId:"+newClient.tmpId+" appointmentID:"+newClient.appointmentID+" appointmentType:"+newClient.appointmentType);
				  }

				  reply=serverService.bookAppointment(newClient.isUserPatient?newClient.userId:newClient.tmpId,newClient.appointmentID, newClient.appointmentType);
				  
				  
				  System.out.println(reply);
				  logger.info(reply);
				  break;			  
			  } 
			  
			  //getAppointmentSchedule
			  case GET_APPOINTMENT_SCDEDULE:
			  {
					if(!newClient.isUserPatient)
					{
						while(true)
						{
							System.out.println("Please enter the patientId on whose behalf you want to perform this patient operation.");
							newClient.tmpId= scanInput.nextLine().trim();
							if(!authenticationService.isPatientIdValid(newClient.tmpId))
							{
								System.out.println("Given patientId is not valid");
							}
							else
							{
								break;
							}
						}
					}

				
				  if(newClient.isUserPatient)
				  {
					  logger.info("Get Appoinment Schedule -> patientID:"+newClient.userId);
				  }	
				  else
				  {
					  logger.info("admin ("+newClient.userId+") is performing patient operation on behalf of patient ("+newClient.tmpId+")");
					  logger.info("Get Appoinment Schedule -> patientID:"+newClient.tmpId);
				  }
				  reply=serverService.getAppointmentSchedule(newClient.isUserPatient?newClient.userId:newClient.tmpId);
				  
				  
				  System.out.println(reply);
				  logger.info(reply);
				  break;
			  }
			  
			  //cancelAppointment
			  case CANCEL_APPOINTMENT:
			  {
					if(!newClient.isUserPatient)
					{
						while(true)
						{
							System.out.println("Please enter the patientId on whose behalf you want to perform this patient operation.");
							newClient.tmpId= scanInput.nextLine().trim();
							if(!authenticationService.isPatientIdValid(newClient.tmpId))
							{
								System.out.println("Given patientId is not valid");
							}
							else
							{
								break;
							}
						}
					}
					
				  newClient.appointmentID=newClient.askForAppoinmentId();
				  //System.out.println("\nEntered appointmentID is "+newClient.appointmentID);
				  if(newClient.isUserPatient)
				  {
					  logger.info("Cancel Appoinment -> patientID:"+newClient.userId+" appoinmentID:"+newClient.appointmentID);
				  }	
				  else
				  {
					  logger.info("admin ("+newClient.userId+") is performing patient operation on behalf of patient ("+newClient.tmpId+")");
					  logger.info("Cancel Appoinment -> patientID:"+newClient.tmpId+" appoinmentID:"+newClient.appointmentID);
				  }
				  
				  reply=serverService.cancelAppointment(newClient.isUserPatient?newClient.userId:newClient.tmpId,newClient.appointmentID);
				  
		
				  System.out.println(reply);
				  logger.info(reply);
				  break;
			  }
			    
			  //swapAppointment
			  case SWAP_APPOINTMENT:  
			  {
				  String newAppointmentID = "";
				  String newAppointmentType ="";
					if(!newClient.isUserPatient)
					{
						while(true)
						{
							System.out.println("Please enter the patientId on whose behalf you want to perform this patient operation.");
							newClient.tmpId= scanInput.nextLine().trim();
							if(!authenticationService.isPatientIdValid(newClient.tmpId))
							{
								System.out.println("Given patientId is not valid");
							}
							else
							{
								break;
							}
						}
					}
				 
				  System.out.println("-- Old Information --");
				  newClient.appointmentID=newClient.askForAppoinmentId();
				  newClient.appointmentType=newClient.askForAppointmentType();
				  
				  System.out.println("-- New Information --");
				  newAppointmentID=newClient.askForAppoinmentId();
				  newAppointmentType=newClient.askForAppointmentType();
				  //System.out.println("\nEntered appointmentID is "+newClient.appointmentID);
				  if(newClient.isUserPatient)
				  {
					  logger.info("Swap Appoinment -> patientID:"+newClient.userId+" oldAppoinmentID:"+newClient.appointmentID+" oldAppointmentType:"+newClient.appointmentType+" newAppointmentID:"+newAppointmentID+" newAppointmentType:"+ newAppointmentType);
				  }	
				  else
				  {
					  logger.info("admin ("+newClient.userId+") is performing patient operation on behalf of patient ("+newClient.tmpId+")");
					  logger.info("Swap Appoinment -> patientID:"+newClient.tmpId+" old appoinmentID:"+newClient.appointmentID+" old AppointmentType:"+newClient.appointmentType+" newAppointmentID:"+newAppointmentID+" newAppointmentType:"+ newAppointmentType);
				  }
				  
				  reply=serverService.swapAppointment(newClient.isUserPatient?newClient.userId:newClient.tmpId,newClient.appointmentID,newClient.appointmentType,newAppointmentID,newAppointmentType);
				  
		
				  System.out.println(reply);
				  logger.info(reply);
				  break;
			  }
			  
			  //addAppointment
			  case ADD_APPOINTMENT:
			  {
				  if(newClient.isUserPatient)
				  {
					  System.out.println("Your choice is not appropriate");
					  break;
				  }
				  
				  newClient.appointmentID=newClient.userId.substring(0, 3);
				  newClient.appointmentID=newClient.appointmentID+newClient.askForTimeSlot();
				  newClient.appointmentID=newClient.appointmentID+newClient.askForDate(true,newClient.appointmentID.substring(3, 4));
					
				  newClient.appointmentType=newClient.askForAppointmentType();
				  
				  newClient.capacity= newClient.askForCapacity();	
				  
				  logger.info("Add Appoinment -> appoinmentID:"+newClient.appointmentID+", appointmentType:"+newClient.appointmentType+", capacity:"+newClient.capacity);
				  
				  //System.out.print("\nEntered AppointmentId: "+newClient.appointmentID+" AppointmentType: "+newClient.appointmentType+ " Capacity: "+newClient.capacity);
				  reply=serverService.addAppointment(newClient.appointmentID, newClient.appointmentType, newClient.capacity);
				  
					  
				  System.out.println(reply);
				  
				  logger.info(reply);
				  break;
			  }
			  
			  //removeAppointment
			  case REMOVE_APPOINTMENT:
			  {
					// booking of a client whose booking got cancelled is still left.
				  if(newClient.isUserPatient)
				  {
					  System.out.println("Your choice is not appropriate");
					  break;
				  }
				  
				  newClient.appointmentID=newClient.userId.substring(0, 3);
				  newClient.appointmentID=newClient.appointmentID+newClient.askForTimeSlot();
				  newClient.appointmentID=newClient.appointmentID+newClient.askForDate(false,newClient.appointmentID.substring(3, 4));
				  newClient.appointmentType=newClient.askForAppointmentType();
				  
				  //System.out.print("\nEntered AppointmentId: "+newClient.appointmentID+" AppointmentType: "+newClient.appointmentType);
				  
				  logger.info("Remove Appoinment -> appoinmentID:"+newClient.appointmentID+", appointmentType:"+newClient.appointmentType);
				  
				  reply=serverService.removeAppointment(newClient.appointmentID,newClient.appointmentType);
				  
				  System.out.println(reply);
				  logger.info(reply);
				  break;
			  }
			  
			  //listAppointmentAvailability
			  case LIST_APPOINTMENT_AVAILABILITY:
			  {
				// concurrent inter server communication is still left.
				  if(newClient.isUserPatient)
				  {
					  System.out.println("Your choice is not appropriate");
					  break;
				  }

				  newClient.appointmentType=newClient.askForAppointmentType();
			
				  //System.out.print("\nEntered AppointmentType: "+newClient.appointmentType);
				  

				  reply=serverService.listAppointmentAvailability(newClient.appointmentType);
				  
				  
				  System.out.println(reply);
				  logger.info(reply);
				  break;
			  }
			    
			  //EXIT
			  case EXIT:
			  {
				  newClient.exit=true;
				  System.out.println("Thank you !");
				  logger.info(newClient.userId+" logged out.");
			      break;
			  }
			  default:
			  {
				  System.out.println("Your choice is not appropriate");
			  }
			}
		}
		
	}
}
