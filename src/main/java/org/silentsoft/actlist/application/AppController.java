package org.silentsoft.actlist.application;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.plugin.ActlistPlugin;
import org.silentsoft.actlist.plugin.PluginComponent;
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
		
		maximizeProperty = new MaximizeProperty(App.getStage());
		
		makeDraggable(App.getStage(), head);
		makeNormalizable(App.getStage(), head);
		
		makeMinimizable(App.getStage(), appMinimizeBtn);
		makeMaximizable(App.getStage(), appMaximizeBtn);
		makeClosable(App.getStage(), appCloseBtn);
		
		StageDragResizer.makeResizable(App.getStage(), root);
		
		loadPlugins();
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
    
    private void changeMaximizeProperty(Stage stage) {
    	maximizeProperty.setMaximized(stage, !maximizeProperty.isMaximized());
		if (maximizeProperty.isMaximized()) {
			// This option is recommended when maximized.
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
		} else {
			// Showing shadow when normalized.
			AnchorPane.setLeftAnchor(root, 5.0);
			AnchorPane.setRightAnchor(root, 5.0);
			AnchorPane.setTopAnchor(root, 5.0);
			AnchorPane.setBottomAnchor(root, 5.0);
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
	
	@SuppressWarnings("unchecked")
	private void loadPlugins() {
		componentBox.getChildren().clear();
		try {
			List<String> deactivatedPlugins = readDeactivatedPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_DEACTIVATED_PLUGINS, deactivatedPlugins);
			
			// Do I need to clean up the /plugins/config if not exists at /plugins/(.jar) ?
			
			Files.walk(Paths.get(System.getProperty("user.dir"), "plugins"), 1).forEach(path -> {
				try {
					File file = path.toFile();
					if (file.isFile()) {
						String name = file.getName();
						if (name.contains(".")) {
							String extension = name.substring(name.lastIndexOf("."), name.length());
							if (CommonConst.EXTENSION_JAR.equalsIgnoreCase(extension)) {
								URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ path.toUri().toURL() });
								Class<?> pluginClass = urlClassLoader.loadClass(BizConst.PLUGIN_CLASS_NAME);
								if (ActlistPlugin.class.isAssignableFrom(pluginClass)) {
									FXMLLoader fxmlLoader = new FXMLLoader(PluginComponent.class.getResource(PluginComponent.class.getSimpleName().concat(CommonConst.EXTENSION_FXML)));
									Node component = fxmlLoader.load();
									((PluginComponent) fxmlLoader.getController()).initialize((Class<? extends ActlistPlugin>) pluginClass, name, !deactivatedPlugins.contains(name));
									componentBox.getChildren().add(component);
								}
							}
						}
					}
				} catch (Exception e) {
					
				}
			});
			
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
	}

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_SAVE_DEACTIVATED_PLUGINS:
			saveDeactivatedPlugins();
			break;
		}
	}
	
}
