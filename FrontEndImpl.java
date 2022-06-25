package Project.FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jws.WebService;

import Project.FinalVariables.*;
import Project.Model.ResponseDetails;
import Project.Model.UserRequestInfo;

@WebService(endpointInterface = "Project.FrontEnd.FrontEndInterface")

public class FrontEndImpl implements FrontEndInterface {
	
	static DatagramSocket datagramSocketFromRM = null;
	static long longestResponseTime = 2;
	static int crashFailureFrequencyRM1=0;
	static int crashFailureFrequencyRM2=0;
	static int crashFailureFrequencyRM3=0;
	static int crashFailureFrequencyRM4=0;
	
	static int softwareFailureFrequencyRM1=0;
	static int softwareFailureFrequencyRM2=0;
	static int softwareFailureFrequencyRM3=0;
	static int softwareFailureFrequencyRM4=0;
	
	public FrontEndImpl() throws SocketException
	{
		datagramSocketFromRM = new DatagramSocket(FinalVariables.FRONT_LISTENER_PORT_FROM_RM);
	}
	
	private long getTime()
	{
		return (new Date()).getTime()/1000;
	}
	@Override
	public String addAppointment(String appointmentID, String appointmentType, int capacity) {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("addAppointment", "null");
		UserRequestInfo.setAppointmentID(appointmentID);
		UserRequestInfo.setAppointmentType(appointmentType);
		UserRequestInfo.setBookingCapacity(capacity);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;
	}

	@Override
	public String removeAppointment(String appointmentID, String appointmentType) throws ParseException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("removeAppointment", "null");
		UserRequestInfo.setAppointmentID(appointmentID);
		UserRequestInfo.setAppointmentType(appointmentType);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;
	}

	@Override
	public String listAppointmentAvailability(String appointmentType) throws InterruptedException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("listAppointmentAvailability", "null");
		UserRequestInfo.setAppointmentType(appointmentType);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;
	}

	@Override
	public String bookAppointment(String patientID, String appointmentID, String appointmentType)
			throws ParseException, InterruptedException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("bookAppointment", "null");
		UserRequestInfo.setUserID(patientID);
		UserRequestInfo.setAppointmentID(appointmentID);
		UserRequestInfo.setAppointmentType(appointmentType);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;

	}

	@Override
	public String getAppointmentSchedule(String patientID) throws InterruptedException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("getAppointmentSchedule", "null");
		UserRequestInfo.setUserID(patientID);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;

	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID) throws InterruptedException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("cancelAppointment", "null");
		UserRequestInfo.setUserID(patientID);
		UserRequestInfo.setAppointmentID(appointmentID);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));	
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());	
		this.receiveResponseFromAllReplicaManagers(timeThen);	
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		return finalResponse;

	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType) throws InterruptedException, ParseException {
		String finalResponse = "VOID";
		UserRequestInfo UserRequestInfo = new UserRequestInfo("swapAppointment", "null");
		UserRequestInfo.setUserID(patientID);
		UserRequestInfo.setOldAppointmentID(oldAppointmentID);
		UserRequestInfo.setOldAppointmentType(oldAppointmentType);
		UserRequestInfo.setAppointmentID(newAppointmentID);
		UserRequestInfo.setAppointmentType(newAppointmentType);
		long timeThen = this.getTime();
		UserRequestInfo.setSequenceNumber(this.sendUnicastToSequencer(UserRequestInfo));
		
		this.receiveResponseFromAllReplicaManagers(timeThen);
		finalResponse = this.processResponseFromAllReplicaManagers(UserRequestInfo.getSequenceNumber(),timeThen);
		//System.out.println("In FrontEndImpl:\n"+UserRequestInfo.toString());
		return finalResponse;

	}
	
	// Boyer–Moore majority vote algorithm
	private String findMajorityResponse(String [] arrayOfResponses, int lengthOfArray)
	{
		String majorityElement = arrayOfResponses[0];
        int i = 0;
        for (int j = 0; j < lengthOfArray; j++)
        {
            if (i == 0)
            {
            	majorityElement = arrayOfResponses[j];
                i = 1;
            }
            
            else if (majorityElement.substring(0,4).equals(arrayOfResponses[j].substring(0,4))) {
                i++;
            }
            else if(!arrayOfResponses[j].equalsIgnoreCase("NULL")){
                i--;
            }
            
        }
        return majorityElement;
	}
	private boolean checkIfEveryResponseIsReceived(ResponseDetails responseDetails)
	{
		if(!responseDetails.getResponseFromRM1().equalsIgnoreCase("NULL") && !responseDetails.getResponseFromRM2().equalsIgnoreCase("NULL") && !responseDetails.getResponseFromRM3().equalsIgnoreCase("NULL") && !responseDetails.getResponseFromRM4().equalsIgnoreCase("NULL")) {
			return true;
		}
		return false;
	}

	private void informRMAboutCrashFailure(String ipAddress, int sequenceNumber)
	{
		DatagramSocket datagramSocketToRM=null;
		String message=String.valueOf(sequenceNumber)+":0";
		System.out.println("sending response to Replica Manager for Crash Failure: "+String.valueOf(sequenceNumber));
		try {
			// send response to RM
			datagramSocketToRM = new DatagramSocket(FinalVariables.FE_PORT_FOR_CRASH_FAILURE);
            byte[] messageToRM = message.getBytes();
            InetAddress hostRM = InetAddress.getByName(ipAddress);
            DatagramPacket responseToRM = new DatagramPacket(messageToRM, messageToRM.length, hostRM, FinalVariables.RM_LISTENER_PORT_FOR_CRASH_FAILURE);

            datagramSocketToRM.send(responseToRM);	
            System.out.println("sent response to Replica Manager for Crash Failure");
			
		}
		catch(Exception e) {
			System.out.println("Socket Exception in informRMAboutCrashFailure(): "+e.getMessage());
		}
		finally {
			if(datagramSocketToRM!=null) {
				datagramSocketToRM.close();
			}
		}
	}

	private void informRMAboutSoftwareFailure(String ipAddress, int sequenceNumber)
	{
		DatagramSocket datagramSocketToRM=null;
		String messsage="0:"+String.valueOf(sequenceNumber);
		System.out.println("sending response to Replica Manager for Software Failure: "+String.valueOf(sequenceNumber) +" to "+ipAddress);
		try {
			// send response to RM
			datagramSocketToRM = new DatagramSocket(FinalVariables.FE_PORT_FOR_SOFTWARE_FAILURE);
            byte[] messageToRM = messsage.getBytes();
            InetAddress hostRM = InetAddress.getByName(ipAddress);
            DatagramPacket responseToRM = new DatagramPacket(messageToRM, messageToRM.length, hostRM, FinalVariables.RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE);

            datagramSocketToRM.send(responseToRM);	
            System.out.println("sent response to Replica Manager for Software Failure: "+String.valueOf(sequenceNumber) +" to "+ipAddress);
			
		}
		catch(Exception e) {
			System.out.println("Socket Exception in informRMAboutSoftwareFailure(): "+e.getMessage());
		}
		finally {
			if(datagramSocketToRM!=null) {
				datagramSocketToRM.close();
			}
		}
	}
	private String [] getArrayOfResponses(ResponseDetails responseDetails, String [] arrayOfResponses)
	{
		int index=0;
		if(!responseDetails.getResponseFromRM1().equalsIgnoreCase("NULL"))
		{
			crashFailureFrequencyRM1=0;
			arrayOfResponses[index++]=responseDetails.getResponseFromRM1();
		}
		else
		{
			crashFailureFrequencyRM1++;
			System.out.println("Crash Failure Frequency: "+crashFailureFrequencyRM1+" in "+FinalVariables.RM1_IP_ADDRESS);
			if(crashFailureFrequencyRM1 == 3)
			{
				this.informRMAboutCrashFailure(FinalVariables.RM1_IP_ADDRESS,responseDetails.getSequenceNumber());
				crashFailureFrequencyRM1 = 0;
			}
		}
		if(!responseDetails.getResponseFromRM2().equalsIgnoreCase("NULL"))
		{
			crashFailureFrequencyRM2=0;
			arrayOfResponses[index++]=responseDetails.getResponseFromRM2();
		}
		else
		{
			crashFailureFrequencyRM2++;
			System.out.println("Crash Failure Frequency: "+crashFailureFrequencyRM2+" in "+FinalVariables.RM2_IP_ADDRESS);
			if(crashFailureFrequencyRM2 == 3)
			{
				this.informRMAboutCrashFailure(FinalVariables.RM2_IP_ADDRESS,responseDetails.getSequenceNumber());
				crashFailureFrequencyRM2 = 0;
			}
		}
		if(!responseDetails.getResponseFromRM3().equalsIgnoreCase("NULL"))
		{
			crashFailureFrequencyRM3=0;
			arrayOfResponses[index++]=responseDetails.getResponseFromRM3();
		}
		else
		{
			crashFailureFrequencyRM3++;
			System.out.println("Crash Failure Frequency: "+crashFailureFrequencyRM3+" in "+FinalVariables.RM3_IP_ADDRESS);
			if(crashFailureFrequencyRM3 == 3)
			{
				this.informRMAboutCrashFailure(FinalVariables.RM3_IP_ADDRESS,responseDetails.getSequenceNumber());
				crashFailureFrequencyRM3 = 0;
			}
		}
		if(!responseDetails.getResponseFromRM4().equalsIgnoreCase("NULL"))
		{
			crashFailureFrequencyRM4=0;
			arrayOfResponses[index++]=responseDetails.getResponseFromRM4();
		}
		else
		{
			crashFailureFrequencyRM4++;
			System.out.println("Crash Failure Frequency: "+crashFailureFrequencyRM4+" in "+FinalVariables.RM4_IP_ADDRESS);
			if(crashFailureFrequencyRM4 == 3)
			{
				this.informRMAboutCrashFailure(FinalVariables.RM4_IP_ADDRESS,responseDetails.getSequenceNumber());
				crashFailureFrequencyRM4 = 0;
			}
		}
		
		while(index<4)
		{
			arrayOfResponses[index++]="NULL";
		}
		return arrayOfResponses;
	}
	private void countSoftwareFailureOccurrence(String finalResponse, ResponseDetails responseDetails)
	{
		if(!responseDetails.getResponseFromRM1().substring(0, 4).equalsIgnoreCase(finalResponse.substring(0, 4)) && !responseDetails.getResponseFromRM1().equalsIgnoreCase("NULL"))
		{
			softwareFailureFrequencyRM1++;
			System.out.println("Software Failure Frequency: "+softwareFailureFrequencyRM1+" in "+FinalVariables.RM1_IP_ADDRESS);
			if(softwareFailureFrequencyRM1 == 3)
			{
				this.informRMAboutSoftwareFailure(FinalVariables.RM1_IP_ADDRESS, responseDetails.getSequenceNumber());
				softwareFailureFrequencyRM1 = 0;
			}
		}
		else
		{
			softwareFailureFrequencyRM1=0;
		}
		
		if(!responseDetails.getResponseFromRM2().substring(0, 4).equalsIgnoreCase(finalResponse.substring(0, 4)) && !responseDetails.getResponseFromRM2().equalsIgnoreCase("NULL"))
		{
			softwareFailureFrequencyRM2++;
			System.out.println("Software Failure Frequency: "+softwareFailureFrequencyRM2+" in "+FinalVariables.RM2_IP_ADDRESS);
			if(softwareFailureFrequencyRM2 == 3)
			{
				this.informRMAboutSoftwareFailure(FinalVariables.RM2_IP_ADDRESS, responseDetails.getSequenceNumber());
				softwareFailureFrequencyRM2 = 0;
			}
		}
		else
		{
			softwareFailureFrequencyRM2=0;
		}
		
		if(!responseDetails.getResponseFromRM3().substring(0, 4).equalsIgnoreCase(finalResponse.substring(0, 4)) && !responseDetails.getResponseFromRM3().equalsIgnoreCase("NULL"))
		{
			softwareFailureFrequencyRM3++;
			System.out.println("Software Failure Frequency: "+softwareFailureFrequencyRM3+" in "+FinalVariables.RM3_IP_ADDRESS);
			if(softwareFailureFrequencyRM3 == 3)
			{
				this.informRMAboutSoftwareFailure(FinalVariables.RM3_IP_ADDRESS, responseDetails.getSequenceNumber());
				softwareFailureFrequencyRM3 = 0;
			}
		}
		else
		{
			softwareFailureFrequencyRM3=0;
		}
		
		if(!responseDetails.getResponseFromRM4().substring(0, 4).equalsIgnoreCase(finalResponse.substring(0, 4)) && !responseDetails.getResponseFromRM4().equalsIgnoreCase("NULL"))
		{
			softwareFailureFrequencyRM4++;
			System.out.println("Software Failure Frequency: "+softwareFailureFrequencyRM4+" in "+FinalVariables.RM4_IP_ADDRESS);
			if(softwareFailureFrequencyRM4 == 3)
			{
				this.informRMAboutSoftwareFailure(FinalVariables.RM4_IP_ADDRESS, responseDetails.getSequenceNumber());
				softwareFailureFrequencyRM4 = 0;
			}
		}
		else
		{
			softwareFailureFrequencyRM4=0;
		}

	}
	private String processResponseFromAllReplicaManagers(int sequenceNumber,long timeThen) {
		 String finalResponse = "NULL";
		 String [] arrayOfResponses= new String[4];
	
	     while(true)
		 {
		 	 if(this.getTime()-timeThen > 2*longestResponseTime)
			 {
				 arrayOfResponses=this.getArrayOfResponses(FrontEnd.responseList.get(sequenceNumber), arrayOfResponses);
				 
				 finalResponse = this.findMajorityResponse(arrayOfResponses,3);
				 this.countSoftwareFailureOccurrence(finalResponse,FrontEnd.responseList.get(sequenceNumber));
				 break;

			 }
			 else
			 {
				 if(FrontEnd.responseList.containsKey(sequenceNumber))
				 {
					 if(this.checkIfEveryResponseIsReceived(FrontEnd.responseList.get(sequenceNumber)))
					 {
						 arrayOfResponses=this.getArrayOfResponses(FrontEnd.responseList.get(sequenceNumber), arrayOfResponses);
						 finalResponse = this.findMajorityResponse(arrayOfResponses,4);
						 this.countSoftwareFailureOccurrence(finalResponse,FrontEnd.responseList.get(sequenceNumber));
						 break;
					 }
				 }
			 }
				
		 }
		
		 return finalResponse;
	}

	private String receiveResponseFromReplicaManager(ResponseDetails responseDetails)
	{
		System.out.println("Receiving Request from RM");
		String messageFromRM="";
		int sequenceNumber;
		try {	
			// receive respone from Replica Manager
			byte[] buffer=new byte[1000];
			DatagramPacket datagramPacketFromRM=new DatagramPacket(buffer,buffer.length);
			try {
				datagramSocketFromRM.setSoTimeout((int) (2*longestResponseTime*1000));
				datagramSocketFromRM.receive(datagramPacketFromRM);
			} catch (SocketTimeoutException e) {
				System.out.println("Closing listener because of timeout.");
			}
			messageFromRM=new String(datagramPacketFromRM.getData()).trim();
			
			System.out.println("Message from Replica Manager: "+messageFromRM);	
			System.out.println("Replica Manager IP Address:"+datagramPacketFromRM.getAddress());

			String[] partsOfMessageFromRM = messageFromRM.split(";");
			
			sequenceNumber=Integer.parseInt(partsOfMessageFromRM[0].trim());
			responseDetails.setSequenceNumber(sequenceNumber);
			responseDetails.setResponse(partsOfMessageFromRM[1].trim(), datagramPacketFromRM.getAddress().toString().trim());
			
			FrontEnd.responseList.put(sequenceNumber, responseDetails);
			
		}
		catch(Exception e) {
			System.out.println("Socket Exception in receiveResponseFromReplicaManager(): "+e.getMessage());
		}
		
		return messageFromRM;
	}
	
	private void receiveResponseFromAllReplicaManagers(long timeThen)
	{
		boolean receivedFromRM1 = false;
		boolean receivedFromRM2 = false;
		boolean receivedFromRM3 = false;
		boolean receivedFromRM4 = false;
		ResponseDetails responseDetails = new ResponseDetails();
		
		 ExecutorService executorService = Executors.newFixedThreadPool(4);
		 @SuppressWarnings("unchecked")
		Future<String> RM1= (Future<String>) executorService.submit(()-> {
				this.receiveResponseFromReplicaManager(responseDetails);
			});
		 @SuppressWarnings("unchecked")
		Future<String> RM2= (Future<String>) executorService.submit(()-> {
				this.receiveResponseFromReplicaManager(responseDetails);
			});
		 @SuppressWarnings("unchecked")
		Future<String> RM3= (Future<String>) executorService.submit(()-> {
				this.receiveResponseFromReplicaManager(responseDetails);
			});
		 @SuppressWarnings("unchecked")
		Future<String> RM4= (Future<String>) executorService.submit(()-> {
				this.receiveResponseFromReplicaManager(responseDetails);
			});
		 

		 try {
				RM1.get(2*longestResponseTime*1000, TimeUnit.SECONDS);
				receivedFromRM1=true;
			} catch (InterruptedException e) {
				System.out.println("");
				RM1.cancel(true);
			} catch (ExecutionException e) {
				System.out.println("");
				RM1.cancel(true);
			} catch (TimeoutException e) {
				System.out.println("Thread timeout.");
				RM1.cancel(true);
			}
			 
			 try {
					RM2.get(2*longestResponseTime*1000, TimeUnit.SECONDS);
					receivedFromRM2=true;
				} catch (InterruptedException e) {
					System.out.println("");
					RM2.cancel(true);
				} catch (ExecutionException e) {
					System.out.println("");
					RM2.cancel(true);
				} catch (TimeoutException e) {
					System.out.println("Thread timeout.");
					RM2.cancel(true);
				}
			 
			 try {
					RM3.get(2*longestResponseTime*1000, TimeUnit.SECONDS);
					receivedFromRM3=true;
				} catch (InterruptedException e) {
					System.out.println("");
					RM3.cancel(true);
				} catch (ExecutionException e) {
					System.out.println("");
					RM3.cancel(true);
				} catch (TimeoutException e) {
					System.out.println("Thread timeout.");
					RM3.cancel(true);
				}
			 
			 try {
					RM4.get(2*longestResponseTime*1000, TimeUnit.SECONDS);
					receivedFromRM4=true;
				} catch (InterruptedException e) {
					System.out.println("");
					RM4.cancel(true);
				} catch (ExecutionException e) {
					System.out.println("");
					RM4.cancel(true);
				} catch (TimeoutException e) {
					System.out.println("Thread timeout.");
					RM4.cancel(true);
				}
			 
			 if(receivedFromRM1 && receivedFromRM2 && receivedFromRM3 && receivedFromRM4)
			 {
				 if(this.getTime()-timeThen>longestResponseTime)
				 {
					 longestResponseTime=this.getTime()-timeThen;
				 }
			 }
	}
    private int sendUnicastToSequencer(UserRequestInfo requestFromUser) {
        DatagramSocket aSocket = null;
        String dataFromClient = requestFromUser.toString();
        //System.out.println("FrontEnd : sendUnicastToSequencer:\n" + dataFromClient);
        int sequenceID = 0;
        try {
            aSocket = new DatagramSocket(FinalVariables.FRONT_PORT);
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName(FinalVariables.SEQUENCER_IP_ADDRESS);
            DatagramPacket requestToSequencer = new DatagramPacket(message, dataFromClient.length(), aHost, FinalVariables.SEQUENCER_PORT);

            aSocket.send(requestToSequencer);

            
            aSocket.setSoTimeout(1000);
            // Set up an UPD packet for recieving
            byte[] buffer = new byte[1000];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            // Try to receive the response from the ping
            aSocket.receive(response);
            String sentence = new String(response.getData(), 0,
                    response.getLength());
          //  System.out.println("FrontEnd : ResponseFromSequencer:\n" + sentence);
            sequenceID = Integer.parseInt(sentence.trim());
            //System.out.println("FrontEnd : ResponseFromSequencer:\n" + sequenceID);
            
        } catch (SocketException e) {
            System.out.println("Failed: " + requestFromUser.noRequestSendError());
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed: " + requestFromUser.noRequestSendError());
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return sequenceID;
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
