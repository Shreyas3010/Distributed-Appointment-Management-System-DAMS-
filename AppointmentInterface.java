package Project.Server;

import java.text.ParseException;


public interface AppointmentInterface
{


	String addAppointment (String appointmentID, String appointmentType, int capacity);
	  

	String removeAppointment (String appointmentID, String appointmentType) throws ParseException;
	  
	String listAppointmentAvailability (String appointmentType) throws InterruptedException;
	  	  
	String bookAppointment (String patientID, String appointmentID, String appointmentType) throws ParseException, InterruptedException;
	  
	String getAppointmentSchedule (String patientID) throws InterruptedException;

	String cancelAppointment (String patientID, String appointmentID) throws InterruptedException;
	  
	String swapAppointment (String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) throws InterruptedException, ParseException;
}