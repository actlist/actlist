package org.silentsoft.actlist.rest;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.ProxyMode;
import org.silentsoft.core.util.ActionUtil;
import org.silentsoft.io.memory.SharedMemory;

import com.github.markusbernhardt.proxy.ProxySearch;

public class RESTfulAPI extends org.silentsoft.net.rest.RESTfulAPI {

	public static <T> T doGet(String uri, Object param, Class<T> returnType) throws Exception {
		AtomicReference<T> result = new AtomicReference<T>();
		
		AtomicInteger tryCount = new AtomicInteger(1);
		AtomicReference<Exception> exception = new AtomicReference<Exception>();
		boolean failed = !ActionUtil.doAction(() -> {
			try {
				result.set(doGet(uri, (tryCount.getAndIncrement() == 1) ? getProxyHost() : null, param, returnType, (request) -> {
					request.setHeaders(createHeaders());
				}));
			} catch (Exception e) {
				exception.set(e);
				throw new RuntimeException();
			}
		}, 1);
		if (failed) {
			throw exception.get();
		}
		
		return result.get();
	}

	public static <T> T doGet(String uri, Object param, Class<T> returnType, Consumer<HttpRequest> beforeRequest) throws Exception {
		AtomicReference<T> result = new AtomicReference<T>();

		AtomicInteger tryCount = new AtomicInteger(1);
		AtomicReference<Exception> exception = new AtomicReference<Exception>();
		boolean failed = !ActionUtil.doAction(() -> {
			try {
				result.set(doGet(uri, (tryCount.getAndIncrement() == 1) ? getProxyHost() : null, param, returnType, (request) -> {
					if (beforeRequest != null) {
						beforeRequest.accept(request);
					}

					request.setHeaders(createHeaders(request.getAllHeaders()));
				}));
			} catch (Exception e) {
				exception.set(e);
				throw new RuntimeException();
			}
		}, 1);
		if (failed) {
			throw exception.get();
		}
		
		return result.get();
	}

	public static void doGet(String uri, Consumer<HttpRequest> beforeRequest, Consumer<HttpResponse> afterResponse) throws Exception {
		AtomicInteger tryCount = new AtomicInteger(1);
		AtomicReference<Exception> exception = new AtomicReference<Exception>();
		boolean failed = !ActionUtil.doAction(() -> {
			try {
				doGet(uri, (tryCount.getAndIncrement() == 1) ? getProxyHost() : null, null, (request) -> {
					if (beforeRequest != null) {
						beforeRequest.accept(request);
					}
	
					request.setHeaders(createHeaders(request.getAllHeaders()));
				}, afterResponse);
			} catch (Exception e) {
				exception.set(e);
				throw new RuntimeException();
			}
		}, 1);
		if (failed) {
			throw exception.get();
		}
	}

	public static <T> T doPost(String uri, Object param) throws Exception {
		AtomicReference<T> result = new AtomicReference<T>();
		
		AtomicInteger tryCount = new AtomicInteger(1);
		AtomicReference<Exception> exception = new AtomicReference<Exception>();
		boolean failed = !ActionUtil.doAction(() -> {
			try {
				result.set(doPost(uri, (tryCount.getAndIncrement() == 1) ? getProxyHost() : null, param, null, (request) -> {
					request.setHeaders(createHeaders());
				}));
			} catch (Exception e) {
				exception.set(e);
				throw new RuntimeException();
			}
		}, 1);
		if (failed) {
			throw exception.get();
		}
		
		return result.get();
	}

	// TODO : proxy host may needs caching logic.
	public static HttpHost getProxyHost() {
		HttpHost proxyHost = null;

		try {
			String proxyMode = ConfigUtil.getProxyMode();
			if (ProxyMode.AUTOMATIC.equals(proxyMode)) {
				List<Proxy> proxies = ProxySearch.getDefaultProxySearch().getProxySelector().select(URI.create("http://actlist.silentsoft.org"));
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
		return createHeaders(null);
	}

	private static Header[] createHeaders(Header[] append) {
		ArrayList<Header> headers = new ArrayList<Header>();

		if (append != null) {
			headers.addAll(Arrays.asList(append));
		}

		headers.add(new BasicHeader("user-agent", String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_USER_AGENT))));

		return headers.toArray(new Header[headers.size()]);
	}

}
