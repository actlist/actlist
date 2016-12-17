package org.silentsoft.actlist.plugin.config;

import java.util.Map;

public class PluginConfigMap implements PluginConfig {

	private Map<String, String> config;

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}
	
}
