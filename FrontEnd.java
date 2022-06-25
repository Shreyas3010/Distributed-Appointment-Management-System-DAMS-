package Project.FrontEnd;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.Endpoint;
import Project.FinalVariables.*;
import Project.Model.ResponseDetails;

public class FrontEnd {
	
	public static ConcurrentHashMap<Integer, ResponseDetails> responseList = new ConcurrentHashMap<>();
	
	public static void main(String[] args) {
		try { 
			Endpoint ep = Endpoint.create(new FrontEndImpl());
			ep.publish("http://"+FinalVariables.FRONT_IP_ADDRESS+":"+FinalVariables.FRONT_PORT_WS+"/FrontEnd");
			if(ep.isPublished())
				System.out.println(" FrontEnd Published...");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Failure!! FrontEnd Not Published...");
		}

	}

}
