package Project.ReplicaManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Map;

import Project.FinalVariables.*;
import Project.Server.AppointmentMontrealServer;
import Project.Server.AppointmentQuebecServer;
import Project.Server.AppointmentSherbrookeServer;
import Project.Server.NewAppointmentMontrealServer;
import Project.Server.NewAppointmentQuebecServer;
import Project.Server.NewAppointmentSherbrookeServer;

public class ReplicaManager {
    
    //City
    
    public static final String MONTREAL="MTL";
    public static final String SHERBROOKE="SHE";
    public static final String QUEBEC="QUE";

	public static String outputFilePath = System.getProperty("user.dir") + "/RM_DATA/Requests.txt";
	public static String inputFilePath = System.getProperty("user.dir") + "/RM_DATA/Requests.txt";

	public static String outputStatusFilePath = System.getProperty("user.dir") + "/RM_DATA/Status.txt";
	public static String inputStatusFilePath = System.getProperty("user.dir") + "/RM_DATA/Status.txt";
	
	static int expectedSequencerId;
	
	public ReplicaManager() {

		expectedSequencerId = 1;
		System.out.println("Hashmap for userRequestList : " + ReplicaManagerData.userRequestList.toString());
		try {
			readFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setExpectedSequencerId(int expectedSequencerId_)
	{
		expectedSequencerId = expectedSequencerId_;
	}
	
	private int getServicePort(String cityAcronym)
	{
	    switch(cityAcronym) 
		{
		case MONTREAL:
			return FinalVariables.MONTREAL_SERVER_PORT;		
		case QUEBEC:
			return FinalVariables.QUEBEC_SERVER_PORT;
		default:
			return FinalVariables.SHERBROOKE_SERVER_PORT;
		}
	}
	private void sendResponseToFrontEnd(String response, int sequenceNumber)
	{
		DatagramSocket datagramSocketToFE=null;
		response=String.valueOf(sequenceNumber)+";"+response;
		System.out.println("sending response to FrontEnd: "+response);
		try {
			// send response to FrontEnd
			datagramSocketToFE = new DatagramSocket(FinalVariables.RM_SENDER_PORT_TO_FE);
            byte[] messageToFE = response.getBytes();
            InetAddress hostFE = InetAddress.getByName(FinalVariables.FRONT_IP_ADDRESS);
            DatagramPacket responseToFE = new DatagramPacket(messageToFE, messageToFE.length, hostFE, FinalVariables.FRONT_LISTENER_PORT_FROM_RM);

            datagramSocketToFE.send(responseToFE);	
            System.out.println("sent response to FrontEnd");
			
		}
		catch(Exception e) {
			System.out.println("Socket Exception: "+e.getMessage());
		}
		finally {
			if(datagramSocketToFE!=null) {
				datagramSocketToFE.close();
			}
		}
		
	}
	@SuppressWarnings("resource")
	private String sendRequestToReplica(String request,int portNumber)
	{
		DatagramSocket datagramSocketRM=null;
		System.out.println("sending request to Replica: "+request+" to "+portNumber);
		try {
			// send request to Replica
			datagramSocketRM = new DatagramSocket(FinalVariables.RM_SENDER_PORT_TO_REPLICA);
            byte[] messageToReplica = request.getBytes();
            InetAddress hostReplica = InetAddress.getByName(FinalVariables.REPLICA_IP_ADDRESS);
            DatagramPacket responseToFE = new DatagramPacket(messageToReplica, messageToReplica.length, hostReplica, portNumber);

            datagramSocketRM.send(responseToFE);	
            System.out.println("sent response to Replica");
            
			// receive response from Replica
			byte[] buffer=new byte[1000];
			String messageFromReplica="";
			DatagramPacket datagramPacketFromReplica=new DatagramPacket(buffer,buffer.length);
			System.out.println("receiving response from Replica");
			datagramSocketRM.receive(datagramPacketFromReplica);
			messageFromReplica=new String(datagramPacketFromReplica.getData());
			System.out.println("received response from Replica: "+messageFromReplica.trim());
			return messageFromReplica.trim();
		}
		catch(Exception e) {
			System.out.println("Socket Exception: in sendRequestToReplica()"+e.getMessage());
		}
		finally {
			if(datagramSocketRM!=null) {
				datagramSocketRM.close();
			}
		}
		return "";
	}
	public void sendResponseDecision(String reply, int sequenceNumber) throws FileNotFoundException, IOException {
		readFromStatusFile();
		if (!ReplicaManagerData.isCrashed && !ReplicaManagerData.isSWFailed)
			this.sendResponseToFrontEnd(reply, sequenceNumber);
		else
			System.out.println("No Response Sent to FrontEnd");
		if (ReplicaManagerData.isCrashed && ReplicaManagerData.lastSuccessfulResponse_CrashFailure == sequenceNumber)
			writeToStatusFile("0:0");
		if (ReplicaManagerData.isSWFailed && ReplicaManagerData.lastSuccessfulResponse_CrashFailure == sequenceNumber)
			writeToStatusFile("0:0");

	}
	
	private void processRequestFromUser() throws ParseException, InterruptedException, FileNotFoundException, IOException {
		while(ReplicaManagerData.userRequestList.containsKey(expectedSequencerId))
		{
			String reply="";
			int servicePort;
			String[] partsOfMessageFromSequencer = ReplicaManagerData.userRequestList.get(expectedSequencerId).split(";");
			// Parsing response to respective parameters
			int sequenceNumber = Integer.parseInt(partsOfMessageFromSequencer[0].trim());
			String methodName = partsOfMessageFromSequencer[3];
		    String userID = partsOfMessageFromSequencer[4];
		    String appointmentID = partsOfMessageFromSequencer[5];    

		    switch(methodName) 
			{
			
			case "addAppointment":
				servicePort = this.getServicePort(appointmentID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("addAppointment - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "removeAppointment":
				servicePort = this.getServicePort(appointmentID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);		
				System.out.println("removeAppointment - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "listAppointmentAvailability":
				servicePort = this.getServicePort(MONTREAL);
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("listAppointmentAvailability - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "bookAppointment":
				servicePort = this.getServicePort(userID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("bookAppointment - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "getAppointmentSchedule":
				servicePort = this.getServicePort(userID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("getAppointmentSchedule - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "cancelAppointment":
				servicePort = this.getServicePort(userID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("cancelAppointment - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
				
			case "swapAppointment":
				servicePort = this.getServicePort(userID.substring(0, 3));
				reply = this.sendRequestToReplica(ReplicaManagerData.userRequestList.get(expectedSequencerId).trim(),servicePort);	
				System.out.println("swapAppointment - reply:\n"+reply);
				this.sendResponseDecision(reply, sequenceNumber);
				break;
			default:
				//log.info("Error occured in the Replica Manager");
				break;
			}
		 
		    expectedSequencerId++;
		}
	}
    private void listenForRequestFromSequencer() throws ParseException, InterruptedException, FileNotFoundException, IOException {
    	
		DatagramSocket datagramSocketFromSequencer=null;
		DatagramSocket datagramSocketToFE=null;
		
		while(true){
			this.processRequestFromUser();
			System.out.println("Receiving Request from Sequencer");
			try {
				// receive request from Sequencer
				datagramSocketFromSequencer=new DatagramSocket(FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
				byte[] buffer=new byte[1000];
				String messageFromSequencer="";
				DatagramPacket datagramPacketFromSequencer=new DatagramPacket(buffer,buffer.length);
				datagramSocketFromSequencer.receive(datagramPacketFromSequencer);
				messageFromSequencer=new String(datagramPacketFromSequencer.getData()).trim();
				//System.out.println("Message from Sequencer:\n "+messageFromSequencer);
				String[] partsOfMessageFromSequencer = messageFromSequencer.split(";");
				ReplicaManagerData.userRequestList.put(Integer.parseInt(partsOfMessageFromSequencer[0]), messageFromSequencer);
				//System.out.println(userRequestList.toString());
				this.writeToFile();
				
			}
			catch(Exception e) {
				System.out.println("Socket Exception: "+e.getMessage());
			}
			finally {
				if(datagramSocketFromSequencer!=null) {
					datagramSocketFromSequencer.close();
				}
				if(datagramSocketToFE!=null) {
					datagramSocketToFE.close();
				}
			}
		}
    }
    
	private void listenForCrashFailureFromFE() throws ParseException, InterruptedException {

		DatagramSocket datagramSocketFromFE = null;

		while (true) {
			
			System.out.println("Receiving Request from FrontEnd for Crash Failure");
			try {
				// receive request from Sequencer
				datagramSocketFromFE = new DatagramSocket(FinalVariables.RM_LISTENER_PORT_FOR_CRASH_FAILURE);
				byte[] buffer = new byte[1000];
				String crashFailureMessage = "";
				DatagramPacket datagramPacketFromSequencer = new DatagramPacket(buffer, buffer.length);
				datagramSocketFromFE.receive(datagramPacketFromSequencer);
				crashFailureMessage = new String(datagramPacketFromSequencer.getData()).trim();
				System.out.println("Message from FE for Crash Failure:\n " + crashFailureMessage);
				this.sendRequestToReplica("0;0;00;checkAlive;null;null;null;null;null;0",this.getServicePort(MONTREAL));
				this.writeToStatusFile(crashFailureMessage);
				System.out.print("Starting recovery from Crash Failure ");
				Thread.sleep(1000);
				AppointmentMontrealServer.main(null);
				AppointmentQuebecServer.main(null);
				AppointmentSherbrookeServer.main(null);
				System.out.print("recovery from Crash Failure is completed");
			} catch (Exception e) {
				System.out.println("Socket Exception in listenForCrashFailureFromFE(): " + e.getMessage());
			} finally {
				if (datagramSocketFromFE != null) {
					datagramSocketFromFE.close();
				}
			}
		}

	}
	
	private void listenForSoftwareFailureFromFE() throws ParseException, InterruptedException {

		DatagramSocket datagramSocketFromFE = null;

		while (true) {
			
			System.out.println("Receiving Request from FrontEnd for Software Failure");
			try {
				// receive request from Sequencer
				datagramSocketFromFE = new DatagramSocket(FinalVariables.RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE);
				byte[] buffer = new byte[1000];
				String softwareFailureMessage = "";
				DatagramPacket datagramPacketFromSequencer = new DatagramPacket(buffer, buffer.length);
				datagramSocketFromFE.receive(datagramPacketFromSequencer);
				softwareFailureMessage = new String(datagramPacketFromSequencer.getData()).trim();
				System.out.println("Message from FE for Software Failure:\n " + softwareFailureMessage);
				//String[] partsOfMessageFromFrontEnd = messageFromSequencer.split(";");
				// Message Parsing
				this.writeToStatusFile(softwareFailureMessage);
				System.out.print("Starting shutdown of server from Software Failure ");
				this.sendRequestToReplica("0;0;00;kill;null;null;null;null;null;0",FinalVariables.MONTREAL_SERVER_PORT);
				this.sendRequestToReplica("0;0;00;kill;null;null;null;null;null;0",FinalVariables.QUEBEC_SERVER_PORT);
				this.sendRequestToReplica("0;0;00;kill;null;null;null;null;null;0",FinalVariables.SHERBROOKE_SERVER_PORT);
				Thread.sleep(2000);
				System.out.print("Shutdown of server from Software Failure is done");
				System.out.print("Starting recovery from Software Failure ");
				NewAppointmentMontrealServer.main(null);
				NewAppointmentQuebecServer.main(null);
				NewAppointmentSherbrookeServer.main(null);
				System.out.print("recovery from Software Failure is completed");
			} catch (Exception e) {
				System.out.println("Socket Exception in listenForSoftwareFailureFromFE(): " + e.getMessage());
			} finally {
				if (datagramSocketFromFE != null) {
					datagramSocketFromFE.close();
				}
			}
		}

	}
	
	private void writeToFile() throws IOException {
		BufferedWriter bf = null;
		try {

			bf = new BufferedWriter(new FileWriter(outputFilePath));
			for (Map.Entry<Integer, String> entry : ReplicaManagerData.userRequestList.entrySet()) {
				bf.write(entry.getKey() + ":" + entry.getValue());
				bf.newLine();
			}

			bf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bf.close();
		}
	}

	public void readFromFile() throws FileNotFoundException, IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
			for (String line; (line = br.readLine()) != null;) {

				String[] keyValue = line.trim().split(":");
				ReplicaManagerData.userRequestList.put(Integer.parseInt(keyValue[0].trim()), keyValue[1].trim());
			}

			System.out.println("Hashmap after writing from file : " + ReplicaManagerData.userRequestList.toString());
		}
	}

	private void writeToStatusFile(String message) throws IOException {
		BufferedWriter bf = null;
		try {

			bf = new BufferedWriter(new FileWriter(outputStatusFilePath));
			bf.write(message);
			bf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bf.close();
		}

		String[] variableList = message.trim().split(":");
		ReplicaManagerData.lastSuccessfulResponse_CrashFailure = Integer.parseInt(variableList[0]);
		ReplicaManagerData.lastSuccessfulResponse_SWFailure = Integer.parseInt(variableList[1]);

		if (ReplicaManagerData.lastSuccessfulResponse_CrashFailure == 0)
			ReplicaManagerData.isCrashed = false;
		else
			ReplicaManagerData.isCrashed = true;

		if (ReplicaManagerData.lastSuccessfulResponse_SWFailure == 0)
			ReplicaManagerData.isSWFailed = false;
		else
			ReplicaManagerData.isSWFailed = true;

	}

	public void readFromStatusFile() throws FileNotFoundException, IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(inputStatusFilePath))) {
			for (String line; (line = br.readLine()) != null;) {
				String[] variableList = line.trim().split(":");
				ReplicaManagerData.lastSuccessfulResponse_CrashFailure = Integer.parseInt(variableList[0]);
				ReplicaManagerData.lastSuccessfulResponse_SWFailure = Integer.parseInt(variableList[1]);
			}
			if (ReplicaManagerData.lastSuccessfulResponse_CrashFailure == 0)
				ReplicaManagerData.isCrashed = false;
			else
				ReplicaManagerData.isCrashed = true;

			if (ReplicaManagerData.lastSuccessfulResponse_SWFailure == 0)
				ReplicaManagerData.isSWFailed = false;
			else
				ReplicaManagerData.isSWFailed = true;

		}
	}
	
	public static void main(String[] args) throws ParseException, InterruptedException {

		ReplicaManager replicaManager = new ReplicaManager();
		
		//listenForRequestFromSequencer
        Runnable taskOfReplicaManager = () -> {
        	try {
				replicaManager.listenForRequestFromSequencer();
			} catch (ParseException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        };
        Thread threadOfReplicaManager = new Thread(taskOfReplicaManager);
        threadOfReplicaManager.start();
       
        
        //listenForSoftwareFailureFromFE
		Runnable taskForSoftwareFailure = () -> {
			try {
				replicaManager.listenForSoftwareFailureFromFE();
			} catch (ParseException | InterruptedException e) {
				e.printStackTrace();
			}
		};
		Thread threadForSoftwareFailure = new Thread(taskForSoftwareFailure);
		threadForSoftwareFailure.start();
		
		//listenForCrashFailureFromFE
		Runnable taskForCrashFailure = () -> {
			try {
				replicaManager.listenForCrashFailureFromFE();
			} catch (ParseException | InterruptedException e) {
				e.printStackTrace();
			}
		};
		Thread threadForCrashFailure = new Thread(taskForCrashFailure);
		threadForCrashFailure.start();
		
	}
	
}
