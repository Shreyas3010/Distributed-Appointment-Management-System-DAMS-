package Project.ReplicaManager;

import java.util.concurrent.ConcurrentHashMap;

public class ReplicaManagerData {
	
	static ConcurrentHashMap<Integer, String> userRequestList = new ConcurrentHashMap<>();
	static int lastSuccessfulResponse_CrashFailure = 0;
	static int lastSuccessfulResponse_SWFailure = 0;
	static boolean isCrashed = false;
	static boolean isSWFailed = false;

	public static ConcurrentHashMap<Integer, String> getUserRequestList() {
		return userRequestList;
	}

	public static void setUserRequestList(ConcurrentHashMap<Integer, String> userRequestList) {
		ReplicaManagerData.userRequestList = userRequestList;
	}

	public int getLastSuccessfulResponse_CrashFailure() {
		return lastSuccessfulResponse_CrashFailure;
	}

	public int getLastSuccessfulResponse_SWFailure() {
		return lastSuccessfulResponse_SWFailure;
	}
	
}
