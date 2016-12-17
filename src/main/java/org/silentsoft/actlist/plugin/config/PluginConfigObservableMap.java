package org.silentsoft.actlist.plugin.config;

import javafx.collections.ObservableMap;

public class PluginConfigObservableMap implements PluginConfig {

	private ObservableMap<String, String> config;

	public ObservableMap<String, String> getConfig() {
		return config;
	}

	public void setConfig(ObservableMap<String, String> config) {
		this.config = config;
	}
	
}
