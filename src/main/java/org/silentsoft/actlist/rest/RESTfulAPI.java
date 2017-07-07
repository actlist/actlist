package org.silentsoft.actlist.rest;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.SystemUtil;

public class RESTfulAPI extends org.silentsoft.net.rest.RESTfulAPI {

	static {
		init("http://silentsoft.org", "/actlist");
	}
	
	public static void init() {
		// DO NOT WRITE CODE HERE.
	}
	
	public static <T> T doGet(String api, Class<T> returnType) throws Exception {
		return doGet(api, returnType, (request) -> {
			request.setHeaders(createHeaders());
		});
	}
	
	public static <T> T doPost(String api, Object param, Class<T> returnType) throws Exception {
		return doPost(api, param, returnType, (request) -> {
			request.setHeaders(createHeaders());
		});
	}
	
	private static Header[] createHeaders() {
		ArrayList<Header> headers = new ArrayList<Header>();
		
		StringBuffer userAgent = new StringBuffer();
		userAgent.append("Actlist-");
		
		userAgent.append(BuildVersion.VERSION);
		
		if (SystemUtil.isWindows()) {
			userAgent.append(" windows-");
		} else if (SystemUtil.isMac()) {
			userAgent.append(" macosx-");
		} else if (SystemUtil.isLinux()) {
			userAgent.append(" linux-");
		} else {
			userAgent.append(" unknown-");
		}
		userAgent.append(SystemUtil.getOSArchitecture());
		
		userAgent.append(" platform-");
		userAgent.append(SystemUtil.getPlatformArchitecture());
		
		headers.add(new BasicHeader("user-agent", userAgent.toString()));
		
		return headers.size() == 0 ? null : headers.toArray(new Header[headers.size()]);
	}
	
}
