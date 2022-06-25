package Project.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import Project.FinalVariables.FinalVariables;
import Project.ReplicaManager.ReplicaManager;



public class AppointmentMontrealServer {	
    
    static Properties props = null;
  
    public static void listenForRequestInterServer(AppointmentImplMontreal serverService, int serverUdpPort) throws InterruptedException, ParseException {
        DatagramSocket aSocket = null;
        while(true) {	
	        String sendingReply = "";
	        try {
	            aSocket = new DatagramSocket(serverUdpPort);
	            byte[] buffer = new byte[1000];
	            System.out.println( "Montreal UDP Server Started at port " + aSocket.getLocalPort() );
	            
	            while (true) {
	                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
	                aSocket.receive(request);
	                String message = new String(request.getData(), 0,
	                        request.getLength());
	                String[] parts = message.split(";");
	                String method = parts[0];
	                String patientID = parts[1];
	                String appointmentID = parts[2];
	                String appointmentType = parts[3];
	                ArrayList<String> removedPatientIDs;
	                
	                if (method.equalsIgnoreCase("listAppointmentAvailability")) {
	                	serverService.UDPCallListAppointmentAvailability=true;
	                    String result = serverService.listAppointmentAvailability(appointmentType);
	                    serverService.UDPCallListAppointmentAvailability=false;
	                    sendingReply = result + ";";
	                } else if (method.equalsIgnoreCase("getAppointmentSchedule")) {
	                	serverService.UDPCallGetAppointmentSchedule=true;
	                    String result = serverService.getAppointmentSchedule(patientID);
	                    serverService.UDPCallGetAppointmentSchedule=false;
	                    sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("bookAppointment"))
	                {
	                	String result = serverService.bookAppointment(patientID, appointmentID, appointmentType);
	                	sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("cancelAppointment"))
	                {
	                	String result = serverService.cancelAppointment(patientID, appointmentID);
	                	sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("bookNextAvaiableSlot"))
	                {
	    				removedPatientIDs = new ArrayList<>(Arrays.asList(patientID.split(",")));
	    				
	    				removedPatientIDs = serverService.bookNextAvaiableSlot(removedPatientIDs,appointmentType);
	                    
	    				String result = removedPatientIDs.toString();
	    				result= result.replace("[", "").replace("]", "").replace(" ", "");
	    				sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("hasAppointment"))
	                {
	                	String result = String.valueOf(serverService.hasAppointment(patientID, appointmentID, appointmentType));
	                	sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("cancelAppointmentHelper"))
	                {
	                	String result = String.valueOf(serverService.cancelAppointmentHelper(patientID, appointmentID, appointmentType));
	                	sendingReply = result + ";";
	                }
	                else if(method.equalsIgnoreCase("replaceWithDummyPatient"))
	                {
	                	String result = String.valueOf(serverService.replaceWithDummyPatient(patientID, appointmentID, appointmentType));
	                	sendingReply = result + ";";
	                }
	                
	                
	                byte[] sendData = sendingReply.getBytes();
	                DatagramPacket reply = new DatagramPacket(sendData, sendingReply.length(), request.getAddress(),
	                        request.getPort());
	                aSocket.send(reply);
	            }
	        } catch (SocketException e) {
	            System.out.println("SocketException: " + e.getMessage());
	        } catch (IOException e) {
	            System.out.println("IOException: " + e.getMessage());
	        } finally {
	            if (aSocket != null)
	                aSocket.close();
	        }
        }
    }
	
	public static String processRequestFromUser(String response, AppointmentImplMontreal serverService) throws ParseException, InterruptedException {
		String reply="";
		String[] partsOfMessageFromSequencer = response.split(";");
		// Parsing response to respective parameters
		int sequenceNumber = Integer.parseInt(partsOfMessageFromSequencer[0].trim());
		String frontEndIPAddress = partsOfMessageFromSequencer[1];
		String messageType = partsOfMessageFromSequencer[2];
		String methodName = partsOfMessageFromSequencer[3];
	    String userID = partsOfMessageFromSequencer[4];
	    String appointmentID = partsOfMessageFromSequencer[5];
	    String appointmentType = partsOfMessageFromSequencer[6];
	    String oldAppointmentID = partsOfMessageFromSequencer[7];
	    String oldAppointmentType = partsOfMessageFromSequencer[8];	    
	    int bookingCapacity = Integer.parseInt(partsOfMessageFromSequencer[9].trim());
	    

	    //method call to server
	    switch(methodName) 
		{
		
		case "addAppointment":
			reply = serverService.addAppointment(appointmentID, appointmentType, bookingCapacity);
			System.out.println("addAppointment in Server - reply:"+reply);
			break;
			
		case "removeAppointment":
			reply= serverService.removeAppointment(appointmentID, appointmentType);		
			System.out.println("removeAppointment in Server- reply:"+reply);
			break;
		
		case "listAppointmentAvailability":
			reply= serverService.listAppointmentAvailability(appointmentType);
			System.out.println("listAppointmentAvailability in Server- reply:"+reply);
			break;
			
		case "bookAppointment":
			reply= serverService.bookAppointment(userID,appointmentID, appointmentType);	
			System.out.println("bookAppointment in Server - reply:"+reply);
			break;
			
		case "getAppointmentSchedule":
			reply= serverService.getAppointmentSchedule(userID);	
			System.out.println("getAppointmentSchedule in Server- reply:"+reply);
			break;
			
		case "cancelAppointment":
			reply= serverService.cancelAppointment(userID,appointmentID);	
			System.out.println("cancelAppointment in Server - reply:"+reply);
			break;
			
		case "swapAppointment":
			reply= serverService.swapAppointment(userID,oldAppointmentID,oldAppointmentType,appointmentID, appointmentType);
			System.out.println("swapAppointment in Server - reply:"+reply);
			break;
		case "checkAlive":
			reply="I am alive";
			System.out.println("checkAlive in Server - reply:"+reply);
			break;
		default:
			//log.info("Error occured in the Replica Manager");
			break;
		}
		return reply+";";
	 
}
public static void listenForRequestFromRM(AppointmentImplMontreal serverService)
{
	DatagramSocket datagramSocketReplica=null;
	String response="";
	while(true){
		System.out.println("Receiving Request from RM");
		try {
			
			// receive request from RM
			datagramSocketReplica=new DatagramSocket(FinalVariables.MONTREAL_SERVER_PORT);
			byte[] buffer=new byte[1000];
			String messageFromRM="";
			DatagramPacket datagramPacketFromRM=new DatagramPacket(buffer,buffer.length);
			datagramSocketReplica.receive(datagramPacketFromRM);
			messageFromRM=new String(datagramPacketFromRM.getData());
			System.out.println("Message from RM: "+messageFromRM.trim());
			
			String[] partsOfMessageFromSequencer = messageFromRM.split(";");
			if(partsOfMessageFromSequencer[3].equals("kill"))
			{
				System.out.println("Received crash failure recovery command to shut down");
				shutDown();
			}	
			else if(partsOfMessageFromSequencer[3].equals("alive"))
			{
				response="I am Alive.";
				System.out.println("sending response to RM: "+response);
	            byte[] messageToRM = response.getBytes();
	            DatagramPacket responseToRM = new DatagramPacket(messageToRM, messageToRM.length, datagramPacketFromRM.getAddress(), datagramPacketFromRM.getPort());

	            datagramSocketReplica.send(responseToRM);
	            System.out.println("sent response to RM");
				continue;
			}
			else
			{
				response = processRequestFromUser(messageFromRM.trim(), serverService);
				// send response to RM
				System.out.println("sending response to RM: "+response);
	            byte[] messageToRM = response.getBytes();
	            DatagramPacket responseToRM = new DatagramPacket(messageToRM, messageToRM.length, datagramPacketFromRM.getAddress(), datagramPacketFromRM.getPort());

	            datagramSocketReplica.send(responseToRM);
	            System.out.println("sent response to RM");
			}

		}
		catch(Exception e) {
			System.out.println("Socket Exception in main: "+e.getMessage());
		}
		finally {
			if(datagramSocketReplica!=null) {
				datagramSocketReplica.close();
			}
			
		}
	}
}

public static void shutDown() {
	System.exit(0);
}

	public static void main(String[] args)  {	

		AppointmentImplMontreal.montrealAppointmentDB = new HashMap<>();
		AppointmentImplMontreal.initializeDatabase();
		AppointmentImplMontreal appointmentImplMontreal =new AppointmentImplMontreal();
		ReplicaManager.setExpectedSequencerId(1);
        Runnable taskInterServer = () -> {
            try {
            	listenForRequestInterServer(appointmentImplMontreal, FinalVariables.MONTREAL_INTER_SERVER_PORT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        };
        Thread threadInterServer = new Thread(taskInterServer);
        threadInterServer.start();
        
		Runnable task = () -> {
            listenForRequestFromRM(appointmentImplMontreal);
        };
        Thread thread = new Thread(task);
        thread.start();
	}

}
