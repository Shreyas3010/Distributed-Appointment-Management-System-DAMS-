package Project.Authenticate;

import java.util.HashMap;
import java.util.logging.Logger;

public class Authentication{
	
	public static HashMap<String, String> adminDB = new HashMap<>();

	public static HashMap<String, String> patientDB = new HashMap<>();
	
	private static Logger logger = Logger.getLogger("AuthenticationLog");

	static {
		adminDB.put("MTLA1234", "MA1234");
		adminDB.put("QUEA1234", "QA1234");
		adminDB.put("SHEA1234", "SA1234");
		
		patientDB.put("MTLP1235", "MP1235");	
		patientDB.put("QUEP1235", "QP1235");
		patientDB.put("SHEP1235", "SP1235");

		patientDB.put("MTLP1236", "MP1236");
		patientDB.put("QUEP1236", "QP1236");
		patientDB.put("SHEP1236", "SP1236");
		

	}

	

	public boolean login(String userId, String password, boolean isUserPatient){
		

		boolean flag;
		if(isUserPatient)
		{
			if(!patientDB.containsKey(userId))
			{
							
				return false;
			}
			
			flag =password.equals(patientDB.get(userId));
			return flag;
		}
		
		if(!adminDB.containsKey(userId))
		{

			
			return false;
		}
		
		flag=password.equals(adminDB.get(userId));
		
		return flag;
		
	}

	public boolean isPatientIdValid(String patientId){
		boolean flag=patientDB.containsKey(patientId);
		
		return flag;
	}

}
