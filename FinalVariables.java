package Project.FinalVariables;

public class FinalVariables {

	//System IP Address
	public static final String SYSTEM_IP_ADDRESS = "192.168.54.67"; // NEED TO CHANGE WHEN CONNECTION GETS CHANGED
	
	//Port Number
    public static final int MONTREAL_SERVER_PORT = 6767;
    public static final int SHERBROOKE_SERVER_PORT = 6768;
    public static final int QUEBEC_SERVER_PORT = 6769;
    public static final int AUTHENTICATION_SERVER = 6770;

    
    public static final int MONTREAL_INTER_SERVER_PORT = 6771;
    public static final int SHERBROOKE_INTER_SERVER_PORT = 6772;
    public static final int QUEBEC_INTER_SERVER_PORT = 6773;  
    
    public static final int FRONT_PORT_WS = 8081;
    public static final int FRONT_PORT = 8083;
	public static final int FRONT_LISTENER_PORT_FROM_RM = 8079;
	public static final int SEQUENCER_PORT = 8082;
	public static final int RM_LISTENER_PORT_FROM_SEQUENCER = 8095; // ALL RMs WILL USE THIS PORT NUMBER
	public static final int RM_LISTENER_PORT_FOR_CRASH_FAILURE = 8097; // ALL RMs WILL USE THIS PORT NUMBER
	public static final int RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE = 8098; // ALL RMs WILL USE THIS PORT NUMBER
	public static final int FE_PORT_FOR_CRASH_FAILURE = 8101; 
	public static final int FE_PORT_FOR_SOFTWARE_FAILURE = 8102; 
	public static final int RM_SENDER_PORT_TO_REPLICA = 8103;
	public static final int RM_SENDER_PORT_TO_FE = 8096;
	
	public static final int SEQUENCER_PORT_SEND_TO_FE = 8084;
	public static final int SEQUENCER_PORT_SEND_TO_RM1 = 8085;
	public static final int SEQUENCER_PORT_SEND_TO_RM2 = 8086;
	public static final int RM_PORT = 9090; 
	
	public static final int REPLICA_LISTENER_PORT_FROM_RM = 8110; 
	public static final int RM_LISTENER_PORT_FROM_REPLICA = 8111; 
	
    //IP Address
    public static final String FRONT_IP_ADDRESS = SYSTEM_IP_ADDRESS;
    public static final String SEQUENCER_IP_ADDRESS = SYSTEM_IP_ADDRESS;
    public static final String RM_IP_ADDRESS = SYSTEM_IP_ADDRESS;
    public static final String REPLICA_IP_ADDRESS = SYSTEM_IP_ADDRESS;
    
    public static final String RM1_IP_ADDRESS = SYSTEM_IP_ADDRESS;
    public static final String RM2_IP_ADDRESS = "192.168.54.1"; // NEED TO CHANGE - YASH'S SYSTEM IP ADDRESS
    public static final String RM3_IP_ADDRESS = "192.168.54.170"; // NEED TO CHANGE - ANURAG's SYSTEM IP ADDRESS
    public static final String RM4_IP_ADDRESS = "192.168.54.176"; // NEED TO CHANGE - RAHUL'S SYSTEM IP ADDRESS

    
}

