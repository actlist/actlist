package org.silentsoft.actlist.plugin;

import java.net.URL;
import java.net.URLClassLoader;

final class PluginClassLoader extends URLClassLoader {
	
	public PluginClassLoader(URL[] urls) {
		super(urls);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> clazz = findLoadedClass(name);
			if (clazz == null) {
				if (sholudLoadClassFromApplication(name)) {
					clazz = loadClassFromApplication(name, resolve);
				} else {
					clazz = loadClassFromPlugin(name, resolve);
				}
			}
			
			if (resolve) {
				resolveClass(clazz);
			}
			
			return clazz;
		}
	}
	
	private boolean sholudLoadClassFromApplication(String name) {
		if (name.startsWith("org.silentsoft.actlist")) {
			return true;
		} else if (name.startsWith("org.slf4j")) {
			return true;
		} else if (name.startsWith("ch.qos.logback")) {
			return true;
		}
		
		return false;
	}
	
	private Class<?> loadClassFromApplication(String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}
	
	private Class<?> loadClassFromPlugin(String name, boolean resolve) throws ClassNotFoundException {
		try {
			return findClass(name);
		} catch (ClassNotFoundException e) {
			return loadClassFromApplication(name, resolve);
		}
	}

}
