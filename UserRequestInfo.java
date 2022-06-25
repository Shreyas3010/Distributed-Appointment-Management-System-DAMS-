package Project.Model;

import Project.FinalVariables.FinalVariables;

public class UserRequestInfo {

    private String methodName = "null";
    private String userID = "null";
    private String appointmentType = "null";
    private String oldAppointmentType = "null";
    private String appointmentID = "null";
    private String oldAppointmentID = "null";
    private String frontEndIPAddress = FinalVariables.FRONT_IP_ADDRESS;
    private int bookingCapacity = 0;
    private int sequenceNumber = 0;
    private String portNumber = "00";
    private int retryCount = 1;
    
    public UserRequestInfo(String methodName, String userID) {
    	setMethodName(methodName);
    	setUserID(userID);
    }
    
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getFrontEndIPAddress() {
		return frontEndIPAddress;
	}
	public void setFrontEndIPAddress(String frontEndIPAddress) {
		this.frontEndIPAddress = frontEndIPAddress;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getAppointmentType() {
		return appointmentType;
	}
	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}
	public String getOldAppointmentType() {
		return oldAppointmentType;
	}
	public void setOldAppointmentType(String oldAppointmentType) {
		this.oldAppointmentType = oldAppointmentType;
	}
	public String getAppointmentID() {
		return appointmentID;
	}
	public void setAppointmentID(String appointmentID) {
		this.appointmentID = appointmentID;
	}
	public String getOldAppointmentID() {
		return oldAppointmentID;
	}
	public void setOldAppointmentID(String oldAppointmentID) {
		this.oldAppointmentID = oldAppointmentID;
	}
	public int getBookingCapacity() {
		return bookingCapacity;
	}
	public void setBookingCapacity(int bookingCapacity) {
		this.bookingCapacity = bookingCapacity;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
    
    public String noRequestSendError() {
        return "request: " + this.getMethodName() + " from " + this.getUserID() + " not sent";
    }
    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public boolean haveRetries() {
        return retryCount > 0;
    }

    public void countRetry() {
        retryCount--;
    }
    
    @Override
    public String toString() {
        return this.getFrontEndIPAddress()+ ";" +
                this.getPortNumber()+ ";" +
                this.getMethodName() + ";" +
                this.getUserID()+ ";" +
                this.getAppointmentID()+ ";" +
                this.getAppointmentType() + ";" +
                this.getOldAppointmentID() + ";" +
                this.getOldAppointmentType()+ ";" +
                this.getBookingCapacity();
    }
}
