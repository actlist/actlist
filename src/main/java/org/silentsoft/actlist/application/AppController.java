package org.silentsoft.actlist.application;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.plugin.ActlistPlugin;
import org.silentsoft.actlist.plugin.PluginComponent;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.model.Delta;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;

public class AppController implements EventListener {

	@FXML
	private AnchorPane root;
	
	@FXML
	private AnchorPane head;
	
	@FXML
	private AnchorPane body;
	
	@FXML
	private Button appMinimizeBtn;
	
	@FXML
	private Button appMaximizeBtn;
	
	@FXML
	private Button appCloseBtn;
	
	@FXML
	private VBox componentBox;
	
	private MaximizeProperty maximizeProperty;
	
	protected void initialize() {
		EventHandler.addListener(this);
		
		root.setPrefWidth(ConfigUtil.getRootWidth());
		root.setPrefHeight(ConfigUtil.getRootHeight());
		
		maximizeProperty = new MaximizeProperty(App.getStage());
		
		makeDraggable(App.getStage(), head);
		makeNormalizable(App.getStage(), head);
		
		makeMinimizable(App.getStage(), appMinimizeBtn);
		makeMaximizable(App.getStage(), appMaximizeBtn);
		makeClosable(App.getStage(), appCloseBtn);
		
		makeResizable(App.getStage(), root);
		
		loadPlugins();
		
		SharedMemory.getDataMap().put(BizConst.KEY_COMPONENT_BOX, componentBox);
	}
	
	/**
	 * makes a stage draggable using a given node.
	 * @param stage
	 * @param byNode
	 */
    private void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        
        byNode.setOnMousePressed(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		dragDelta.setX(stage.getX() - mouseEvent.getScreenX());
                dragDelta.setY(stage.getY() - mouseEvent.getScreenY());
                
                byNode.setOpacity(0.8);
        	}
        });
        
        byNode.setOnMouseDragged(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		if (maximizeProperty.isMaximized()) {
        			double x = (mouseEvent.getScreenX() - stage.getX());
        			double y = (mouseEvent.getScreenY() - stage.getY());

        			changeMaximizeProperty(stage);
        			
        			stage.setX(x);
                    stage.setY(y);
                    
                    dragDelta.setX(-1 * (stage.getWidth() / 2));
                    dragDelta.setY(-1 * (mouseEvent.getSceneY()));
        		} else {
        			stage.setX(mouseEvent.getScreenX() + dragDelta.getX());
                    stage.setY(mouseEvent.getScreenY() + dragDelta.getY());
        		}
    		}
        });
        
        byNode.setOnMouseReleased(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		byNode.setOpacity(1.0);
        	}			
		});
    }
    
    /**
     * makes a stage normalizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeNormalizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getClickCount() >= 2) {
    			changeMaximizeProperty(stage);
    		}
    	});
    }
    
    /**
     * makes a stage minimizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMinimizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			/**
    			 * EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_SHOW_HIDE);
    			 * 
    			 * Do not call the above event. make sure to stay on taskbar.
    			 */
    			stage.setIconified(true);
    		}
    	});
    }
    
    /**
     * makes a stage maximizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMaximizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			changeMaximizeProperty(stage);
    		}
    	});
    }
    
    /**
     * makes a stage closable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeClosable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			Transition animation = AnimationUtils.createTransition(App.getParent(), AnimationType.BOUNCE_OUT_DOWN);
    			animation.setOnFinished(actionEvent -> {
    				stage.hide();
    			});
    			animation.play();
    		}
    	});
    }
    
    private void makeResizable(final Stage stage, final Region region) {
    	StageDragResizer.makeResizable(stage, region, 7, () -> {
    		try {
    			ConfigUtil.setRootWidth(region.getWidth());
    			ConfigUtil.setRootHeight(region.getHeight());
    			
    			ConfigUtil.setStageWidth(App.getStage().getWidth());
    			ConfigUtil.setStageHeight(App.getStage().getHeight());
    		} catch (Exception e) {
    			
    		}
    	});
    }
    
    private void changeMaximizeProperty(Stage stage) {
    	maximizeProperty.setMaximized(stage, !maximizeProperty.isMaximized());
		if (maximizeProperty.isMaximized()) {
			// This option is recommended when maximized.
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			
			AnchorPane.setLeftAnchor(head, 0.0);
			AnchorPane.setRightAnchor(head, 0.0);
			AnchorPane.setTopAnchor(head, 0.0);
			
			AnchorPane.setLeftAnchor(body, 0.0);
			AnchorPane.setRightAnchor(body, 0.0);
			AnchorPane.setTopAnchor(body, 25.0);
			AnchorPane.setBottomAnchor(body, 0.0);
		} else {
			// Showing shadow when normalized.
			AnchorPane.setLeftAnchor(root, 5.0);
			AnchorPane.setRightAnchor(root, 5.0);
			AnchorPane.setTopAnchor(root, 5.0);
			AnchorPane.setBottomAnchor(root, 5.0);
			
			// Make offset for change the size of application via mouse.
			AnchorPane.setLeftAnchor(head, 2.0);
			AnchorPane.setRightAnchor(head, 2.0);
			AnchorPane.setTopAnchor(head, 2.0);
			AnchorPane.setLeftAnchor(body, 2.0);
			AnchorPane.setRightAnchor(body, 2.0);
			AnchorPane.setTopAnchor(body, 27.0);
			AnchorPane.setBottomAnchor(body, 2.0);
		}
    }
    
    private List<String> readDeactivatedPlugins() {
    	return FileUtil.readFileByLine(Paths.get(System.getProperty("user.dir"), "plugins", "deactivated.ini"), true);
    }
    
    private void saveDeactivatedPlugins() {
    	try {
    		StringBuffer buffer = new StringBuffer();
    		List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
    		for (String deactivatedPlugin : deactivatedPlugins) {
    			buffer.append(deactivatedPlugin);
    			buffer.append("\r\n");
    		}
    		
            FileUtil.saveFile(Paths.get(System.getProperty("user.dir"), "plugins", "deactivated.ini"), buffer.toString());
    	} catch (Exception e) {
    		
    	}
    }
	
    private List<String> readPriorityOfPlugins() {
    	return FileUtil.readFileByLine(Paths.get(System.getProperty("user.dir"), "plugins", "priority.ini"), true);
    }
    
    private void savePriorityOfPlugins() {
    	try {
    		StringBuffer buffer = new StringBuffer();
    		
    		List<String> priorityOfPlugins;
    		if (componentBox.getChildren().isEmpty()) {
    			priorityOfPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_PRIORITY_OF_PLUGINS);
        	} else {
        		priorityOfPlugins = new ArrayList<String>();
        		for (Node node : componentBox.getChildrenUnmodifiable()) {
        			priorityOfPlugins.add(((PluginComponent) node.getUserData()).getPluginFileName());
        		}
        	}
    		
    		for (String priorityOfPlugin : priorityOfPlugins) {
    			buffer.append(priorityOfPlugin);
    			buffer.append("\r\n");
    		}
    		
            FileUtil.saveFile(Paths.get(System.getProperty("user.dir"), "plugins", "priority.ini"), buffer.toString());
    	} catch (Exception e) {
    		
    	}
    }
    
	private void loadPlugins() {
		componentBox.getChildren().clear();
		try {
			List<String> deactivatedPlugins = readDeactivatedPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_DEACTIVATED_PLUGINS, deactivatedPlugins);
			
			List<String> priorityOfPlugins = readPriorityOfPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_PRIORITY_OF_PLUGINS, priorityOfPlugins);
			
			// Do I need to clean up the /plugins/config if not exists at /plugins/(.jar) ?
			
			// extract plugins
			List<String> plugins = new ArrayList<String>();
			Files.walk(Paths.get(System.getProperty("user.dir"), "plugins"), 1).forEach(path -> {
				if (isAssignableFromJarFile(path)) {
					plugins.add(path.getFileName().toString());
				}
			});
			
			// transform priority
			for (int i = priorityOfPlugins.size() - 1; i >= 0; i--) {
				String plugin = priorityOfPlugins.get(i);
				
				if (plugins.contains(plugin)) {
					plugins.remove(plugin);
					plugins.add(0, plugin);
				} else {
					priorityOfPlugins.remove(i);
				}
			}
			priorityOfPlugins.clear();
			priorityOfPlugins.addAll(plugins);
			savePriorityOfPlugins();
			
			// load plugins
			for (String plugin : plugins) {
				try {
					Path path = Paths.get(System.getProperty("user.dir"), "plugins", plugin);
					if (isAssignableFromJarFile(path)) {
						loadPlugin(path);
					}
				} catch (Exception e) {
					
				}
			}
			
			if (componentBox.getChildren().isEmpty()) {
				Label label = new Label();
				label.setText("No plugins available.");
				
				HBox hBox = new HBox(label);
				hBox.setAlignment(Pos.CENTER);
				AnchorPane.setTopAnchor(hBox, 0.0);
				AnchorPane.setRightAnchor(hBox, 0.0);
				AnchorPane.setBottomAnchor(hBox, 0.0);
				AnchorPane.setLeftAnchor(hBox, 0.0);
				
				AnchorPane pane = new AnchorPane(hBox);
				pane.setStyle("-fx-background-color: #ffffff;");
				pane.setPrefWidth(200);
				pane.setPrefHeight(140);

				componentBox.getChildren().add(pane);
			}
		} catch (Exception e) {
			
		}
		
		App.getStage().showingProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == false && newValue == true) {
				EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_ACTIVATED);
			} else if (oldValue == true && newValue == false) {
				EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_DEACTIVATED);
			}
		});
		App.getStage().iconifiedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == false && newValue == true) {
				EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_DEACTIVATED);
			} else if (oldValue == true && newValue == false) {
				EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_ACTIVATED);
			}
		});
		App.getStage().setOnCloseRequest(windowEvent -> {
			EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_CLOSE_REQUESTED, false);
			EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_EXIT);
		});
	}
	
	private boolean isAssignableFromJarFile(Path path) {
		File file = path.toFile();
		if (file.isFile()) {
			String fileName = file.getName();
			if (fileName.contains(".")) {
				String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
				if (CommonConst.EXTENSION_JAR.equalsIgnoreCase(extension)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void loadPlugin(Path path) throws Exception {
		@SuppressWarnings("resource") // Do not close the urlClassLoader for control their graphic things on each plugin.
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ path.toUri().toURL() });
		Class<?> pluginClass = urlClassLoader.loadClass(BizConst.PLUGIN_CLASS_NAME);
		
		if (ActlistPlugin.class.isAssignableFrom(pluginClass)) {
			FXMLLoader fxmlLoader = new FXMLLoader(PluginComponent.class.getResource(PluginComponent.class.getSimpleName().concat(CommonConst.EXTENSION_FXML)));
			Node component = fxmlLoader.load();
			PluginComponent pluginComponent = ((PluginComponent) fxmlLoader.getController());
			
			String fileName = path.getFileName().toString();
			List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
			pluginComponent.initialize(fileName, (Class<? extends ActlistPlugin>) pluginClass, !deactivatedPlugins.contains(fileName));
			
			component.setUserData(pluginComponent);
			
			componentBox.getChildren().add(component);
		}
	}

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_SAVE_DEACTIVATED_PLUGINS:
			saveDeactivatedPlugins();
			
			break;
		case BizConst.EVENT_SAVE_PRIORITY_OF_PLUGINS:
			savePriorityOfPlugins();
			
			break;
		}
	}
	
}
