package Project.Sequencer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import Project.FinalVariables.*;
public class Sequencer {
     
	static int sequenceId=1;
	DatagramSocket datagramSocketGlobal=null;
	DatagramPacket datagramPacketGlobal=null;
	static ConcurrentHashMap<Integer, String> userRequestList = new ConcurrentHashMap<>();

	@SuppressWarnings("null")
	private static void sendMulticastToRMs(String messageToRMs)
	{
		DatagramSocket datagramSocketToRMs=null;
		try {
			
			datagramSocketToRMs = new DatagramSocket(FinalVariables.SEQUENCER_PORT_SEND_TO_RM1);
			byte[] messageToRM = messageToRMs.getBytes();
			
			// Unicast to RM1
			System.out.println("sending to RM1");
	        InetAddress hostRM1 = InetAddress.getByName(FinalVariables.RM1_IP_ADDRESS);
	        DatagramPacket datagramPacketToRM1 = new DatagramPacket(messageToRM, messageToRM.length, hostRM1, FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
	        datagramSocketToRMs.send(datagramPacketToRM1);
	        System.out.println("sent to RM1");

	        // Unicast to RM2
	        System.out.println("sending to RM2");
	        InetAddress hostRM2 = InetAddress.getByName(FinalVariables.RM2_IP_ADDRESS);
	        DatagramPacket datagramPacketToRM2 = new DatagramPacket(messageToRM, messageToRM.length, hostRM2, FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
	        datagramSocketToRMs.send(datagramPacketToRM2);
	        System.out.println("sent to RM2");
	        
	        // Unicast to RM3
	        System.out.println("sending to RM3");
	        InetAddress hostRM3 = InetAddress.getByName(FinalVariables.RM3_IP_ADDRESS);
	        DatagramPacket datagramPacketToRM3 = new DatagramPacket(messageToRM, messageToRM.length, hostRM3, FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
	        datagramSocketToRMs.send(datagramPacketToRM3);
	        System.out.println("sent to RM3");
	                
	        // Unicast to RM4
	        System.out.println("sending to RM4");
	        InetAddress hostRM4 = InetAddress.getByName(FinalVariables.RM4_IP_ADDRESS);
	        DatagramPacket datagramPacketToRM4 = new DatagramPacket(messageToRM, messageToRM.length, hostRM4, FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
	        datagramSocketToRMs.send(datagramPacketToRM4);
	        System.out.println("sent to RM4");

		}
		catch(Exception e) {
			System.out.println("Socket Exception in sendMulticastToRMs: "+e.getMessage());
		}
		finally {
			if(datagramSocketToRMs!=null) {
				datagramSocketToRMs.close();
			}
			
		}

	}
	public static void main(String[] args) {
		
		//Sequencer sequencer=new Sequencer();
		
		Runnable task1=()-> {
			DatagramSocket datagramSocketFromFE=null;
			DatagramSocket datagramSocketToFE=null;
			while(true){
				System.out.println("Receiving Request from FrontEnd");
				try {
					
					// receive request from FrontEnd
					datagramSocketFromFE=new DatagramSocket(FinalVariables.SEQUENCER_PORT);
					byte[] buffer=new byte[1000];
					String messageFromFE="";
					DatagramPacket datagramPacketFromFE=new DatagramPacket(buffer,buffer.length);
					datagramSocketFromFE.receive(datagramPacketFromFE);
					messageFromFE=new String(datagramPacketFromFE.getData());
					//System.out.println("Message from FrontEnd:\n "+messageFromFE);
					userRequestList.put(sequenceId, messageFromFE);
					messageFromFE=String.valueOf(sequenceId)+";"+messageFromFE;
					//System.out.println("Message from FrontEnd with sequenceID:\n "+messageFromFE);
				
					// send sequenceID to FrontEnd
					datagramSocketToFE = new DatagramSocket(FinalVariables.SEQUENCER_PORT_SEND_TO_FE);
		            byte[] messageToFE = String.valueOf(sequenceId).getBytes();
		            InetAddress hostFE = InetAddress.getByName(FinalVariables.FRONT_IP_ADDRESS);
		            DatagramPacket requestToSequencer = new DatagramPacket(messageToFE, messageToFE.length, hostFE, FinalVariables.FRONT_PORT);

		            datagramSocketToFE.send(requestToSequencer);
		            
					//multicast to RM 
		            sendMulticastToRMs(messageFromFE);
		            sequenceId++;		
					
				}
				catch(Exception e) {
					System.out.println("Socket Exception in main: "+e.getMessage());
				}
				finally {
					if(datagramSocketFromFE!=null) {
						datagramSocketFromFE.close();
					}
					if(datagramSocketToFE!=null) {
						datagramSocketToFE.close();
					}
				}
			}
		};
		
		Thread thread=new Thread(task1);
		thread.start();
		
	}

}