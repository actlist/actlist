package org.silentsoft.actlist.plugin.about;

import java.net.URI;

import org.silentsoft.actlist.plugin.ActlistPlugin;
import org.silentsoft.ui.viewer.AbstractViewer;

public class PluginAbout extends AbstractViewer {
	
	public PluginAbout(ActlistPlugin actlistPlugin, boolean isAvailableNewPlugin, URI newPluginURI) {
		super(actlistPlugin, isAvailableNewPlugin, newPluginURI);
	}
	
}
