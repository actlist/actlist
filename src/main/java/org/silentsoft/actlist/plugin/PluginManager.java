package org.silentsoft.actlist.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.plugin.messagebox.MessageBox;
import org.silentsoft.io.memory.SharedMemory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class PluginManager {

	public static boolean install(File file) throws Exception {
		if (file == null) {
			return false;
		}
		
		HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
		
		boolean shouldCopy = true;
		if (file.getPath().equals(Paths.get(System.getProperty("user.dir"), "plugins", file.getName()).toString())) {
			if (pluginMap.containsKey(file.getName())) {
				MessageBox.showError(App.getStage(), "You can not select an already loaded plugin !");
				return false;
			}
			
			shouldCopy = false;
		}
		
		URLClassLoader urlClassLoader = null;
		Class<?> pluginClass = null;
		
		boolean isErrorOccur = false;
		try {
			urlClassLoader = new URLClassLoader(new URL[]{ file.toURI().toURL() });
			pluginClass = urlClassLoader.loadClass(BizConst.PLUGIN_CLASS_NAME);
			
			if (ActlistPlugin.class.isAssignableFrom(pluginClass) == false) {
				isErrorOccur = true;
			}
		} catch (Exception | Error e) {
			e.printStackTrace();
			isErrorOccur = true;
		}
		
		if (isErrorOccur) {
			MessageBox.showError(App.getStage(), "This file is not kind of Actlist plugin !");
			return false;
		}
		
		Path source = Paths.get(file.toURI());
		Path target = Paths.get(System.getProperty("user.dir"), "plugins");
		if (shouldCopy) {
			Files.copy(source, target.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
		
		return true;
	}
	
	public static void delete(String pluginFileName) throws Exception {
		unload(pluginFileName);
		Files.delete(Paths.get(System.getProperty("user.dir"), "plugins", pluginFileName));
	}
	
	public static void load(String pluginFileName, boolean activated) throws Exception {
		load(pluginFileName, activated, null);
	}
	
	public static void load(String pluginFileName, boolean activated, Integer index) throws Exception {
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ Paths.get(System.getProperty("user.dir"), "plugins", pluginFileName).toUri().toURL() });
		Class<?> pluginClass = urlClassLoader.loadClass(BizConst.PLUGIN_CLASS_NAME);
		
		if (ActlistPlugin.class.isAssignableFrom(pluginClass)) {
			HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
			boolean shouldClearPromptLabel = (pluginMap.size() == 0);
			pluginMap.put(pluginFileName, urlClassLoader);
			
			FXMLLoader fxmlLoader = new FXMLLoader(PluginComponent.class.getResource(PluginComponent.class.getSimpleName().concat(CommonConst.EXTENSION_FXML)));
			Node component = fxmlLoader.load();
			PluginComponent pluginComponent = ((PluginComponent) fxmlLoader.getController());
			
			pluginComponent.initialize(pluginFileName, (Class<? extends ActlistPlugin>) pluginClass, activated);
			component.setUserData(pluginComponent);
			
			VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
			if (shouldClearPromptLabel) {
				// remove the 'No plugins available.' prompt label
				componentBox.getChildren().clear();
			}
			if (index == null) {
				componentBox.getChildren().add(component);
			} else {
				componentBox.getChildren().add(index, component);
			}
		}
	}
	
	public static void unload(String pluginFileName) throws Exception {
		VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
		for (int i=0, j=componentBox.getChildren().size(); i<j; i++) {
			PluginComponent pluginComponent = (PluginComponent) componentBox.getChildren().get(i).getUserData();
			if (pluginComponent.getPluginFileName().equals(pluginFileName)) {
				pluginComponent.clear();
				
				componentBox.getChildren().remove(i);
				
				HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
				pluginMap.get(pluginFileName).close();
				pluginMap.remove(pluginFileName);
				
				break;
			}
		}
	}
	
}
