package org.silentsoft.actlist.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class SystemUtil {

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
	
}
