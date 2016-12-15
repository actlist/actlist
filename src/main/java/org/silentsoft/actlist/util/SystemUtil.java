package org.silentsoft.actlist.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.silentsoft.actlist.CommonConst;

public class SystemUtil {
	
	private static final int IMAGE_NAME = 0;
	private static final int PID = 1;
	private static final int SESSION_NAME = 2;
	private static final int SESSION_ID = 3;
	private static final int MEMORY_USAGE = 4;
	private static final int STATUS = 5;
	private static final int USER_NAME = 6;
	private static final int CPU_TIME = 7;
	private static final int WINDOW_TITLE = 8;

	private static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw;

		StreamReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
				;
			}
		}

		String getResult() {
			return sw.toString();
		}
	}
	
	public static class ProcessInfo {
		private String imageName;
		private String pid;
		private String sessionName;
		private String sessionId;
		private String memoryUsage;
		private String status;
		private String userName;
		private String cpuTime;
		private String windowTitle;
		
		ProcessInfo(String imageName, String pid, String sessionName, String sessionId, String memoryUsage, String status, String userName, String cpuTime, String windowTitle) {
			this.imageName = imageName;
			this.pid = pid;
			this.sessionName = sessionName;
			this.sessionId = sessionId;
			this.memoryUsage = memoryUsage;
			this.status = status;
			this.userName = userName;
			this.cpuTime = cpuTime;
			this.windowTitle = windowTitle;
		}
		
		public String getImageName() {
			return imageName;
		}
		
		public String getPid() {
			return pid;
		}
		
		public String getSessionName() {
			return sessionName;
		}
		
		public String getSessionId() {
			return sessionId;
		}
		
		public String getMemoryUsage() {
			return memoryUsage;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getUserName() {
			return userName;
		}
		
		public String getCpuTime() {
			return cpuTime;
		}
		
		public String getWindowTitle() {
			return windowTitle;
		}
	}
	
	/**
	 * Returns <tt>"x64"</tt> if the current operating system is based on 64-bit. otherwise returns <tt>"x86"</tt>.
	 * @return
	 */
	public static String getOSArchitecture() {
		String osArchitecture = "";
		
		try {
			Process process = Runtime.getRuntime().exec("cmd /C wmic OS get OSArchitecture");
			StreamReader reader = new StreamReader(process.getInputStream());
			
			reader.start();
			process.waitFor();
			reader.join();
			
			osArchitecture = reader.getResult().trim().split("\r\n")[1].contains("64") ? "x64" : "x86";
		} catch (Exception e) {
			;
		}
		
		return osArchitecture;
	}
	
	/**
	 * Returns <tt>true</tt> if the specific <code>imageName</code> process is exists, otherwise <tt>false</tt>.
	 * @param imageName
	 * @return
	 */
	public static boolean findProcessByImageName(String imageName) {
		return getProcessInfoByImageName(imageName).isEmpty() ? false : true;
	}
	
	/**
	 * Returns specific <code>imageName</code> processes as {@link ProcessInfo} list.
	 * if there are no specific <code>imageName</code> processes, returns an empty list.
	 * @param imageName
	 * @return
	 */
	public static List<ProcessInfo> getProcessInfoByImageName(String imageName) {
		return findProcess("IMAGENAME", imageName);
	}
	
	/**
	 * Find process by given <code>command</code>(could be "IMAGENAME" or "PID") and <code>target</code>(could be image name or PID value).
	 * and then returns {@link ProcessInfo} as list that contains process information(image name, PID, session name, memory usage, status, user name, CPU time, window title).
	 * @param command
	 * @param target
	 * @return
	 */
	private static List<ProcessInfo> findProcess(String command, String target) {
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
		
		try {
			if (target != null && target.length() > 0) {
				Process process = runCommand(String.join("", "tasklist /V /FO \"CSV\" /FI \"", command, " eq ", target, "\" | find /I \"", target, "\""));
				StreamReader reader = new StreamReader(process.getInputStream());
				
				reader.start();
				process.waitFor();
				reader.join();
				
				String[] rows = reader.getResult().split(CommonConst.ENTER);
				for (String row : rows) {
					if ("".equals(row) || row.trim().length() == 0) {
						continue;
					}
					
					String[] cols = row.split("\",\"");
					cols[0] = cols[0].replaceAll(CommonConst.QUOTATION_MARK_DOUBLE, CommonConst.NULL_STR);
					cols[cols.length-1] = cols[cols.length-1].replaceAll(CommonConst.QUOTATION_MARK_DOUBLE, CommonConst.NULL_STR);
					
					processes.add(new ProcessInfo(cols[IMAGE_NAME], cols[PID], cols[SESSION_NAME], cols[SESSION_ID], cols[MEMORY_USAGE], cols[STATUS], cols[USER_NAME], cols[CPU_TIME], cols[WINDOW_TITLE]));
				}
			}
		} catch (Exception e) {
			;
		}
		
		return processes;
	}
	
	/**
	 * Run windows CMD command.
	 * @param command pure CMD command
	 * @throws IOException
	 */
	public static Process runCommand(String command) throws IOException {
		return Runtime.getRuntime().exec("cmd /C " + command);
	}
	
}
