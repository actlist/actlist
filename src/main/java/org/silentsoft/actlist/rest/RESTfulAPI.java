package org.silentsoft.actlist.rest;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.ProxyMode;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.SystemUtil;

import com.github.markusbernhardt.proxy.ProxySearch;

public class RESTfulAPI extends org.silentsoft.net.rest.RESTfulAPI {

	static {
		init("", "");
		/**
		 * init("http://silentsoft.org", "/actlist");
		 * 
		 * it must not be set because of plugin's update check URI.
		 * that URI may not be a official silentsoft.org. it could be a private server.
		 */
	}
	
	public static void init() {
		// DO NOT WRITE CODE HERE.
	}
	
	public static <T> T doGet(String api, Object param, Class<T> returnType) throws Exception {
		return doGet(api, getProxyHost(), param, returnType, (request) -> {
			request.setHeaders(createHeaders());
		});
	}
	
	public static <T> T doPost(String api, Object param, Class<T> returnType) throws Exception {
		return doPost(api, getProxyHost(), param, returnType, (request) -> {
			request.setHeaders(createHeaders());
		});
	}
	
	// TODO : proxy host may needs caching logic.
	private static HttpHost getProxyHost() {
		HttpHost proxyHost = null;
		
		try {
			String proxyMode = ConfigUtil.getProxyMode();
			if (ProxyMode.AUTOMATIC.equals(proxyMode)) {
				List<Proxy> proxies = ProxySearch.getDefaultProxySearch().getProxySelector().select(URI.create("http://silentsoft.org"));
				if (proxies != null && proxies.isEmpty() == false) {
					for (Proxy proxy : proxies) {
						SocketAddress socketAddress = proxy.address();
						if (socketAddress instanceof InetSocketAddress) {
							InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
							proxyHost = new HttpHost(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
							break;
						}
					}
				}
			} else if (ProxyMode.MANUAL.equals(proxyMode)) {
				URI uri = URI.create(ConfigUtil.getProxyHost());
				proxyHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
			}
		} catch (Exception e) {
			
		}
		
		return proxyHost;
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
