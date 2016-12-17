package org.silentsoft.actlist;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.JSONUtil;

public class ActlistConfig {

	private HashMap<String, Object> config;
	
	public ActlistConfig() {
		config = new HashMap<String, Object>();
	}

	public HashMap<String, Object> getConfig() {
		return config;
	}

	public void setConfig(HashMap<String, Object> config) {
		this.config = config;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) config.get(key);
	}
	
	public void put(String key, Object value) throws Exception {
		config.put(key, value);
		commit();
	}
	
	public void remove(String key) throws Exception {
		config.remove(key);
		commit();
	}

	final void commit() throws Exception {
		File configFile = Paths.get(System.getProperty("user.dir"), "actlist.jar.config").toFile();
		
		if (configFile.getParentFile().exists() == false) {
			configFile.getParentFile().mkdirs();
		}
		
		if (configFile.exists() == false) {
			configFile.createNewFile();
		}
		
		FileUtil.saveFile(configFile, JSONUtil.ObjectToString(this));
	}
	
}
