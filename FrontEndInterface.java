package Project.FrontEnd;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding(style = Style.RPC)


public interface FrontEndInterface {


	@WebMethod
	String addAppointment (String appointmentID, String appointmentType, int capacity);
	  
	@WebMethod
	String removeAppointment (String appointmentID, String appointmentType) throws ParseException, InterruptedException, ExecutionException, TimeoutException;
	  
	@WebMethod
	String listAppointmentAvailability (String appointmentType) throws InterruptedException;
	  
	  
	@WebMethod
	String bookAppointment (String patientID, String appointmentID, String appointmentType) throws ParseException, InterruptedException;
	  
	@WebMethod
	String getAppointmentSchedule (String patientID) throws InterruptedException, ExecutionException, TimeoutException;

	@WebMethod 
	String cancelAppointment (String patientID, String appointmentID) throws InterruptedException;
	  
	@WebMethod
	String swapAppointment (String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) throws InterruptedException, ParseException;

}
