package Project.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class AppointmentDetails{
	
    public static final String APPOINTMENT_TIME_MORNING = "Morning";
    public static final String APPOINTMENT_TIME_AFTERNOON = "Afternoon";
    public static final String APPOINTMENT_TIME_EVENING = "Evening";
    public static final String PHYSICIAN = "Physician";
    public static final String SURGEON = "Surgeon";
    public static final String DENTAL = "Dental";
    public static final int APPOINTMENT_FULL = -1;
    public static final int ALREADY_BOOKED = 0;
    public static final int SUCCESSFULLY_BOOKED = 1;
    public static final int PATIENT_NOT_EXIST = 0;
    public static final int SUCCESSFULLY_REMOVED = 1;
	
    
	int capacity;
	int totalBooking;
	String appoinmentId;
	String appoinmentType;
	String appoinmentDate;
	String appoinmentTimeSlot;
	
	ArrayList<String> bookedPatientIds=new ArrayList<String>();
	
	
	//------------ Constructor ------------//
	
	
	public AppointmentDetails(int capacity, int totalBooking,String appoinmentId, String appoinmentType){
		this.capacity=capacity;
		this.totalBooking=totalBooking;
		this.appoinmentId=appoinmentId;
		this.appoinmentDate=this.getAppoinmentDateFromAppoinmentId(appoinmentId);
		this.appoinmentTimeSlot= this.getAppoinmentTimeSlotFromAcronym(appoinmentId.charAt(3));
		this.appoinmentType=appoinmentType;
	}
	
	
	//------------ Getter ------------//
	
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public int getTotalBooking() {
		return this.totalBooking;
	}
	
	public ArrayList<String> getListOfBooking()
	{
		return this.bookedPatientIds;
	}
	
	public String getAppoinmentTimeSlotFromAcronym(char c)
	{
		if(c == 'M')
			return APPOINTMENT_TIME_MORNING;
		else if(c == 'A')
			return APPOINTMENT_TIME_AFTERNOON;
		else if(c == 'E')
			return APPOINTMENT_TIME_EVENING;
		return "Invalid";
	}
	
	public String getAppoinmentDateFromAppoinmentId(String appoinmentId)
	{
		return appoinmentId.substring(4,6)+"/"+appoinmentId.substring(6,8)+"/"+appoinmentId.substring(8, 10);
	}
	
	public String getAppoinmentId()
	{
		return this.appoinmentId;
	}
	
	
	//------------ Setter ------------//
	
	
	public void setCapacity(int capacity)
	{
		this.capacity=capacity;
	}
	
	public void setTotalBooking(int totalBooking)
	{
		this.totalBooking=totalBooking;
	}
	
	public void setBookedPatientIds(ArrayList <String> bookedPatientIds)
	{
		this.bookedPatientIds=bookedPatientIds;
	}
	
	
	//------------ Other ------------//
	
	
	public void increamentTotalBooking()
	{
		this.totalBooking=this.totalBooking+1;
	}

	public void decreamentTotalBooking()
	{
		this.totalBooking=this.totalBooking>0?this.totalBooking-1:0;
	}

	
	public int addPatientToBooking(String patientId)
	{
		if(this.isBookingFull())
		{
			return APPOINTMENT_FULL;
		}
		
		if(this.bookedPatientIds.contains(patientId))
		{
			return ALREADY_BOOKED;
		}
		
		this.bookedPatientIds.add(patientId);
		this.totalBooking=this.totalBooking+1;
		return SUCCESSFULLY_BOOKED;
	}

	public int removePatientToBooking(String patientId)
	{
		if(!this.bookedPatientIds.contains(patientId))
		{
			return PATIENT_NOT_EXIST;
		}
		
		this.bookedPatientIds.remove(patientId);
		this.totalBooking=this.totalBooking-1;
		return SUCCESSFULLY_REMOVED;
	}
	
	public boolean containsPatientId(String patientId)
	{
		return this.bookedPatientIds.contains(patientId);
	}
	
	public boolean isBookingFull()
	{
		return this.totalBooking >= this.capacity;
	}
	
	public boolean replacePatientId(String oldPatientId,String newPatientId)
	{
		int index = this.bookedPatientIds.indexOf(oldPatientId);
		if(index == -1)
		{
			return false;
		}
		this.bookedPatientIds.set(index, newPatientId);
		
		return true;
	}
	public int availableBookingSpace()
	{
		return this.capacity-this.totalBooking;
	}
	@Override
	public String toString()
	{
		return "\n--------------\nappointmentId: "+
							this.appoinmentId+", appoinmentType: "+this.appoinmentType+
							", appoinmentTimeSlot: "+this.appoinmentTimeSlot+
							", appoinmentDate: "+this.appoinmentDate+", capacity: "
							+this.capacity+", totalBooking: "+this.totalBooking
							+",\nbookedPatientIds:"+
							this.bookedPatientIds.stream().reduce("",(str1, str2)-> str1 + " " + str2)
							+"\n--------------\n";
	}
}

