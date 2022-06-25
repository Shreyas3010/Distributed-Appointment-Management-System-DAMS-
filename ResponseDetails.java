package Project.Model;

import Project.FinalVariables.*;

public class ResponseDetails {
	String responseFromRM1="";
	String responseFromRM2="";
	String responseFromRM3="";
	String responseFromRM4="";
	int sequenceNumber;
	
	public ResponseDetails()
	{
		responseFromRM1="NULL";
		responseFromRM2="NULL";
		responseFromRM3="NULL";
		responseFromRM4="NULL";
		sequenceNumber=0;
	}
	public void setResponse(String responseFromRM,String ipAddress)
	{
		switch (ipAddress.substring(1)) {
		  case FinalVariables.RM1_IP_ADDRESS:
			  this.setResponseFromRM1(responseFromRM);
		      break;
		  case FinalVariables.RM2_IP_ADDRESS:
			  this.setResponseFromRM2(responseFromRM);
		      break;
		  case FinalVariables.RM3_IP_ADDRESS:
			  this.setResponseFromRM3(responseFromRM);
		      break;
		  default:
			  this.setResponseFromRM4(responseFromRM);
			  break;
		}
	}
	public String getResponseFromRM1() {
		return responseFromRM1;
	}
	public void setResponseFromRM1(String responseFromRM1) {
		this.responseFromRM1 = responseFromRM1;
	}
	public String getResponseFromRM2() {
		return responseFromRM2;
	}
	public void setResponseFromRM2(String responseFromRM2) {
		this.responseFromRM2 = responseFromRM2;
	}
	public String getResponseFromRM3() {
		return responseFromRM3;
	}
	public void setResponseFromRM3(String responseFromRM3) {
		this.responseFromRM3 = responseFromRM3;
	}
	public String getResponseFromRM4() {
		return responseFromRM4;
	}
	public void setResponseFromRM4(String responseFromRM4) {
		this.responseFromRM4 = responseFromRM4;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
