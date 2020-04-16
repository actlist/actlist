package org.silentsoft.actlist.application;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.plugin.PluginComponent;
import org.silentsoft.actlist.plugin.PluginManager;
import org.silentsoft.actlist.plugin.messagebox.MessageBox;
import org.silentsoft.actlist.rest.RESTfulAPI;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.Theme;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.actlist.view.about.About;
import org.silentsoft.actlist.view.configuration.Configuration;
import org.silentsoft.actlist.view.explore.Explore;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.model.Delta;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;

import de.codecentric.centerdevice.glass.AdapterContext;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AppController implements EventListener {

	@FXML
	private AnchorPane root;
	
	@FXML
	private BorderPane head;
	
	@FXML
	private Label headMinimizeButton, headMaximizeButton, headCloseButton;
	
	@FXML
	private BorderPane body;
	
	@FXML
	private Pane handPaneMac;
	
	@FXML
	private VBox sideArea;
	
	@FXML
	private HBox sideControls;
	
	@FXML
	private Label sideMinimizeButton, sideMaximizeButton, sideCloseButton;
	
	@FXML
	private VBox sideNav;
	
	@FXML
	private Region navPluginsMac, navExploreMac, navConsoleMac, navAboutMac, navConfigurationMac;
	
	@FXML
	private Region navPluginsWin, navExploreWin, navConsoleWin, navAboutWin, navConfigurationWin;
	
	@FXML
	private Label newPluginsAlarmLabelMac;
	
	@FXML
	private Label newPluginsAlarmLabelWin;
	
	@FXML
	private Label appUpdateAlarmLabelMac;
	
	@FXML
	private Label appUpdateAlarmLabelWin;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private BorderPane contentPane;
	
//	@FXML
	private Pagination componentBoxPagination;
	
//	@FXML
	private VBox componentBox;
	
	private TextArea consoleTextArea;
	
	private PopOver updatePopOver;
	
	private MaximizeProperty maximizeProperty;
	
	private HashMap<String, URLClassLoader> pluginMap;
	
	protected void initialize() {
		EventHandler.addListener(this);
		
		initComponentBox();
		initConsole();
		
		root.setPrefWidth(ConfigUtil.getRootWidth());
		root.setPrefHeight(ConfigUtil.getRootHeight());
		
		maximizeProperty = new MaximizeProperty(App.getStage());
		pluginMap = new HashMap<String, URLClassLoader>();
		
		{
			makeDraggable(App.getStage(), head);
			makeNormalizable(App.getStage(), head);
			
			makeMinimizable(App.getStage(), headMinimizeButton);
			makeMaximizable(App.getStage(), headMaximizeButton);
			makeClosable(App.getStage(), headCloseButton);
		}
		{
			makeDraggable(App.getStage(), sideNav);
			makeNormalizable(App.getStage(), sideNav);
			
			makeMinimizable(App.getStage(), sideMinimizeButton);
			makeMaximizable(App.getStage(), sideMaximizeButton);
			makeClosable(App.getStage(), sideCloseButton);
		}
		{
			// for native drag experience on Mac theme
			makeDraggable(App.getStage(), handPaneMac);
			makeNormalizable(App.getStage(), handPaneMac);
		}
		applyTheme();
		applyDarkMode();
		
		makeResizable(App.getStage(), root);
		
		initUpdatePopOver();
		checkUpdate();
		
		SharedMemory.getDataMap().put(BizConst.KEY_PLUGIN_MAP, pluginMap);
		//SharedMemory.getDataMap().put(BizConst.KEY_COMPONENT_BOX, componentBox);
		
		showPluginsView();
		loadPlugins();
		
		enableContextMenu();
		enableDragAndDrop();
	}
	
	public void notifyFocusState(boolean isFocused) {
		if (ConfigUtil.isMacTheme()) {
			if (isFocused) {
				sideCloseButton.setStyle("-fx-background-color: red; -fx-background-radius: 5em;");
				sideMinimizeButton.setStyle("-fx-background-color: orange; -fx-background-radius: 5em;");
				sideMaximizeButton.setStyle("-fx-background-color: #59bf53; -fx-background-radius: 5em;");
			} else {
				sideCloseButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
				sideMinimizeButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
				sideMaximizeButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
			}
		}
	}
	
	private void initComponentBox() {
		componentBox = new VBox();
		{
			BorderPane.setAlignment(componentBox, Pos.CENTER);
		}
		SharedMemory.getDataMap().put(BizConst.KEY_COMPONENT_BOX, componentBox);
		
		componentBoxPagination = new Pagination();
		componentBoxPagination.setPageCount(5);
		componentBoxPagination.setMaxPageIndicatorCount(20);
		componentBoxPagination.getStyleClass().addAll("bullet", "componentBoxPagination");
		componentBoxPagination.setOnDragDetected(mouseEvent -> {
			componentBoxPagination.setUserData(mouseEvent.getSceneX());
			componentBoxPagination.startFullDrag();
		});
		componentBoxPagination.setOnMouseDragReleased(mouseDragEvent -> {
			double startedX = (double) componentBoxPagination.getUserData();
			double currentX = mouseDragEvent.getSceneX();
			if (Math.abs(startedX - currentX) >= 20) {
				if (startedX < currentX) {
					componentBoxPagination.setCurrentPageIndex(componentBoxPagination.getCurrentPageIndex()-1); // left to right
				} else {
					componentBoxPagination.setCurrentPageIndex(componentBoxPagination.getCurrentPageIndex()+1); // right to left
				}
			}
		});
		componentBoxPagination.setOnScroll(event -> System.out.println(event));
		
		/**
		 * TODO
		 * 
		 * App::Load -> read index from file system
		 * componentBoxPagination.currentPageIndex.addListener -> save index to file system
		 */
		
		componentBoxPagination.setPageFactory((pageIndex) -> {
			System.out.println("page factory .. = " + pageIndex + " isempty = " + componentBox.getChildren().isEmpty() + " count = " + componentBox.getChildren().size());
			return componentBox;
			
//			VBox page = new VBox();
//			if (componentBox.getChildren().isEmpty() == false && componentBox.getChildren().size() > pageIndex) {
//				componentBox.getChildren().forEach(node -> {
//					node.setVisible(false);
//				});
//				
//				//page.getChildren().add(componentBox.getChildren().get(pageIndex));
//				for (int i=0, j=componentBox.getChildren().size(); i<j; i++) {
//					if (pageIndex == i) {
//						componentBox.getChildren().get(i).setVisible(true);
//					}
//				}
//			} else {
//				//page.getChildren().add(new Label(pageIndex+""));
//			}
//			return componentBox;
			
			
//			List<Node> nodes = ((VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX)).getChildren();
//			VBox page = new VBox();
//			if (pageIndex == 0) {
//				page.getChildren().addAll(((PluginComponent) nodes.get(0).getUserData()).getNode(), ((PluginComponent) nodes.get(1).getUserData()).getNode());
//			} else if (pageIndex == 1) {
//				page.getChildren().addAll(((PluginComponent) nodes.get(2).getUserData()).getNode(), ((PluginComponent) nodes.get(3).getUserData()).getNode());
//			} else {
//				page.getChildren().addAll(nodes);
//			}
//			return page;
		});
		BorderPane.setAlignment(componentBoxPagination, Pos.CENTER);
	}
	
	private void initConsole() {
		consoleTextArea = new TextArea();
		{
			MenuItem clearMenuItem = new MenuItem("Clear");
			clearMenuItem.setOnAction(event -> EventHandler.callEvent(getClass(), BizConst.EVENT_CLEAR_CONSOLE_LOG));
			consoleTextArea.setContextMenu(new ContextMenu(clearMenuItem));
		}
		consoleTextArea.setEditable(false);
		consoleTextArea.setFont(Font.font("Consolas", 13.0));
		{
			StringBuffer style = new StringBuffer();
			style.append("-fx-control-inner-background: rgb(40, 40, 40); ");
			style.append("-fx-background-color: -fx-control-inner-background; ");			
			style.append("-fx-background-radius: 0; ");
			style.append("-fx-faint-focus-color: transparent;");
			
			consoleTextArea.setStyle(style.toString());
		}
		SharedMemory.getDataMap().put(BizConst.KEY_CONSOLE_TEXT_AREA, consoleTextArea);
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
                
                byNode.setOpacity(0.98);
        	}
        });
        
        byNode.setOnMouseDragged(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		if (maximizeProperty.isMaximized()) {
        			double x = (mouseEvent.getScreenX() - stage.getX());
        			double y = (mouseEvent.getScreenY() - stage.getY());

        			changeMaximizeProperty(stage);
        			
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
    			
    			// TODO MenuBar still disappearing on Mac
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
    			if (SystemUtil.isMac()) {
					AdapterContext.getContext().getApplicationAdapter().hide();
				} else {
//	    			Transition animation = AnimationUtils.createTransition(App.getParent(), AnimationType.BOUNCE_OUT_DOWN);
//	    			animation.setOnFinished(actionEvent -> {
	    				stage.hide();
//	    			});
//	    			animation.play();					
				}
    		}
    	});
    }
    
    private void makeResizable(final Stage stage, final Region region) {
    	StageDragResizer.makeResizable(stage, region, 7, 10, () -> {
    		try {
    			ConfigUtil.setRootWidth(region.getWidth());
    			ConfigUtil.setRootHeight(region.getHeight());
    			
    			ConfigUtil.setStageWidth(App.getStage().getWidth());
    			ConfigUtil.setStageHeight(App.getStage().getHeight());
    		} catch (Exception e) {
    			e.printStackTrace();
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
			
			AnchorPane.setLeftAnchor(body, 0.0);
			AnchorPane.setRightAnchor(body, 0.0);
			AnchorPane.setTopAnchor(body, 0.0);
			AnchorPane.setBottomAnchor(body, 0.0);
		} else {
			// Showing shadow when normalized.
			AnchorPane.setLeftAnchor(root, 5.0);
			AnchorPane.setRightAnchor(root, 5.0);
			AnchorPane.setTopAnchor(root, 5.0);
			AnchorPane.setBottomAnchor(root, 5.0);
			
			// Make offset for change the size of application via mouse.
			AnchorPane.setLeftAnchor(body, 2.0);
			AnchorPane.setRightAnchor(body, 2.0);
			AnchorPane.setTopAnchor(body, 2.0);
			AnchorPane.setBottomAnchor(body, 2.0);
		}
		stage.getScene().getRoot().requestLayout();
    }
    
    private void applyTheme() {
    	if (isWinTheme()) {
    		body.setTop(head);
    		body.setLeft(null);
    		
    		handPaneMac.setVisible(false);
    	} else if (isMacTheme()) {
    		body.setTop(null);
    		body.setLeft(sideArea);
    		
    		handPaneMac.setVisible(true);
    	}
    }
    
    private void applyDarkMode() {
    	String css = getClass().getResource("App-darkmode.css").toExternalForm();
    	
    	if (ConfigUtil.isDarkMode()) {
    		if (root.getStylesheets().contains(css) == false) {
    			root.getStylesheets().add(css);
    		}
    	} else {
    		root.getStylesheets().remove(css);
    	}
    }
    
    private void playNewPluginsAlarm() {
    	Platform.runLater(() -> {
    		newPluginsAlarmLabelWin.setVisible(true);
    		newPluginsAlarmLabelMac.setVisible(true);
    		
    		FadeTransition fadeTransition = null;
    		if (isWinTheme()) {
    			fadeTransition = new FadeTransition(Duration.millis(400), newPluginsAlarmLabelWin);
    		} else if (isMacTheme()) {
    			fadeTransition = new FadeTransition(Duration.millis(400), newPluginsAlarmLabelMac);
    		}
    		
    		if (fadeTransition != null) {
    			fadeTransition.setFromValue(1.0);
    			fadeTransition.setToValue(0.3);
    			fadeTransition.setCycleCount(6);
    			fadeTransition.setAutoReverse(true);
    			
    			fadeTransition.play();
    		}
    	});
    }
    
    @FXML
    private void showPluginsView() {
		toggleNav(navPluginsWin, navPluginsMac);
		
		newPluginsAlarmLabelWin.setVisible(false);
		newPluginsAlarmLabelMac.setVisible(false);
    	
    	//contentPane.setCenter(componentBox);
		contentPane.setCenter(componentBoxPagination);
    }
    
    @FXML
    private void showExploreView() {
    	toggleNav(navExploreWin, navExploreMac);
    	
    	contentPane.setCenter(new Explore().getViewer());
    }
    
    @FXML
    private void showConsoleView() {
    	toggleNav(navConsoleWin, navConsoleMac);
    	
    	contentPane.setCenter(consoleTextArea);
    }
    
    @FXML
    private void showAboutView() {
    	toggleNav(navAboutWin, navAboutMac);
    	
    	contentPane.setCenter(new About().getViewer());
    }
    
    @FXML
    private void showConfigurationView() {
    	toggleNav(navConfigurationWin, navConfigurationMac);
    	
    	contentPane.setCenter(new Configuration().getViewer());
    }
    
    @FXML
    private void eventNavMouseEntered(MouseEvent mouseEvent) {
    	if (mouseEvent.getSource() instanceof Region) {
    		Region region = (Region) mouseEvent.getSource();
    		if (region.getOpacity() == 1.0) {
    			// do nothing
    		} else {
    			region.setOpacity(0.9);
    		}
    	}
    }
    
    @FXML
    private void eventNavMouseExited(MouseEvent mouseEvent) {
    	if (mouseEvent.getSource() instanceof Region) {
    		Region region = (Region) mouseEvent.getSource();
    		if (region.getOpacity() == 1.0) {
    			// do nothing
    		} else {
    			region.setOpacity(0.75);
    		}
    	}
    }
    
    private void toggleNav(Region targetWin, Region targetMac) {
    	Arrays.asList(navPluginsWin, navExploreWin, navConsoleWin, navAboutWin, navConfigurationWin, navPluginsMac, navExploreMac, navConsoleMac, navAboutMac, navConfigurationMac).forEach(region -> {
    		if (region == targetWin || region == targetMac) {
    			region.setOpacity(1.0);
    		} else {
    			region.setOpacity(0.75);
    		}
    	});
    }
    
    private boolean isWinTheme() {
    	return Theme.WIN.equals(ConfigUtil.getTheme());
    }
    
    private boolean isMacTheme() {
    	return Theme.MAC.equals(ConfigUtil.getTheme());
    }
    
    private void initUpdatePopOver() {
    	updatePopOver = new PopOver();
		
		Label title = new Label("Actlist Update Alarm");
		title.setFont(Font.font("Verdana", FontWeight.BOLD, 12.0));
		title.setTextFill(Paint.valueOf("#000000"));
		title.setPadding(new Insets(10.0, 0.0, 0.0, 0.0));
		
		Label message = new Label("New Actlist is available. Would you like to browse now ?");
		message.setWrapText(true);
		message.setFont(Font.font("Verdana", 12.0));
		message.setTextFill(Paint.valueOf("#000000"));
		message.setTextAlignment(TextAlignment.CENTER);
		message.setPrefWidth(194.0);
		message.setPrefHeight(40.0);
		
		JFXButton notNowButton = new JFXButton("Not Now");
		notNowButton.setCursor(Cursor.HAND);
		notNowButton.setPrefWidth(97.0);
		notNowButton.setButtonType(ButtonType.RAISED);
		notNowButton.setRipplerFill(Paint.valueOf("#eeeeee"));
		notNowButton.setFont(Font.font("Verdana", 12.0));
		notNowButton.setTextFill(Paint.valueOf("#0b7aea"));
		notNowButton.setOnMouseClicked(mouseEvent -> {
			hideUpdatePopOver();
		});
		
		JFXButton browseButton = new JFXButton("Browse");
		browseButton.setCursor(Cursor.HAND);
		browseButton.setPrefWidth(97.0);
		browseButton.setButtonType(ButtonType.RAISED);
		browseButton.setRipplerFill(Paint.valueOf("#eeeeee"));
		browseButton.setFont(Font.font("Verdana", FontWeight.BOLD, 12.0));
		browseButton.setTextFill(Paint.valueOf("#1c81f9"));
		browseButton.setOnMouseClicked(mouseEvent -> {
			try {
				Desktop.getDesktop().browse(new URI("http://actlist.silentsoft.org/archives/"));
			} catch (Exception e) {
				
			}
			hideUpdatePopOver();
		});
		
		HBox hBox = new HBox(notNowButton, browseButton);
		hBox.setAlignment(Pos.CENTER);
		hBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");
		
		VBox vBox = new VBox(title, message, hBox);
		vBox.setAlignment(Pos.CENTER);
		vBox.setStyle("-fx-background-color: white;");
		vBox.setSpacing(5.0);
		vBox.setPrefWidth(214.0);
		vBox.setPrefHeight(103.0);
		
		updatePopOver.setContentNode(vBox);
    }
    
    private void checkUpdate() {
    	new Thread(() -> {
    		Runnable checkUpdate = () -> {
    			try {
        			boolean isAvailableNewActlist = false;
        			
        			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        			param.add(new BasicNameValuePair("version", BuildVersion.VERSION));
        			param.add(new BasicNameValuePair("os", SystemUtil.getOSName()));
        			param.add(new BasicNameValuePair("architecture", SystemUtil.getPlatformArchitecture()));
        			
        			Map<String, Object> result = RESTfulAPI.doGet("http://actlist.silentsoft.org/api/update/check", param, Map.class);
        			if (result == null) {
        				return;
        			}
        			
        			if (result.containsKey("available")) {
        				isAvailableNewActlist = Boolean.parseBoolean(String.valueOf(result.get("available")));
        			}
        			
        			SharedMemory.getDataMap().put(BizConst.KEY_IS_AVAILABLE_NEW_ACTLIST, isAvailableNewActlist);
        			
        			if (isAvailableNewActlist) {
        				Platform.runLater(() -> {
    						appUpdateAlarmLabelWin.setVisible(true);
    						appUpdateAlarmLabelMac.setVisible(true);
        					
        					FadeTransition fadeTransition = null;
        					if (isWinTheme()) {
        						fadeTransition = new FadeTransition(Duration.millis(400), appUpdateAlarmLabelWin);
        					} else if (isMacTheme()) {
        						fadeTransition = new FadeTransition(Duration.millis(400), appUpdateAlarmLabelMac);
        					}
        					
        					if (fadeTransition != null) {
								fadeTransition.setFromValue(1.0);
								fadeTransition.setToValue(0.3);
								fadeTransition.setCycleCount(6);
								fadeTransition.setAutoReverse(true);
								
								fadeTransition.play();
        					}
        				});
        			}
        		} catch (Exception e) {
        			
        		}
    		};
    		
    		boolean shouldCheck = true;
			Date latestCheckDate= null;
			while (true) {
				if (shouldCheck) {
					checkUpdate.run();
					latestCheckDate = Calendar.getInstance().getTime();
				}
				try {
					Thread.sleep((long)Duration.minutes(10).toMillis());
				} catch (InterruptedException ie) {
					
				} finally {
					shouldCheck = DateUtil.getDifferenceHoursFromNow(latestCheckDate) >= 24;
				}
			}
    	}).start();
    }
    
    @FXML
    private void showControls() {
    	((SVGPath) sideCloseButton.getGraphic()).setFill(Paint.valueOf("rgb(35, 35, 35)"));
    	((SVGPath) sideMinimizeButton.getGraphic()).setFill(Paint.valueOf("rgb(35, 35, 35)"));
    	((SVGPath) sideMaximizeButton.getGraphic()).setFill(Paint.valueOf("rgb(35, 35, 35)"));
    }
    
    @FXML
    private void hideControls() {
    	((SVGPath) sideCloseButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    	((SVGPath) sideMinimizeButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    	((SVGPath) sideMaximizeButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    }
    
    @FXML
    private void showUpdatePopOver() {
    	if (updatePopOver.isShowing() == false) {
    		if (isWinTheme()) {
    			updatePopOver.setArrowLocation(ArrowLocation.TOP_RIGHT);
    			updatePopOver.show(appUpdateAlarmLabelWin);
    		} else if (isMacTheme()) {
    			updatePopOver.setArrowLocation(ArrowLocation.BOTTOM_LEFT);
    			updatePopOver.show(appUpdateAlarmLabelMac);
    		}
		}
    }
    
    private void hideUpdatePopOver() {
    	updatePopOver.hide();
    	
		appUpdateAlarmLabelWin.setVisible(false);
		appUpdateAlarmLabelMac.setVisible(false);
    }
    
	private void loadPlugins() {
		componentBox.getChildren().clear();
		try {
			File pluginsDirectory = Paths.get(System.getProperty("user.dir"), "plugins").toFile();
			if (pluginsDirectory.exists() == false) {
				pluginsDirectory.mkdirs();
			}
			
			List<String> purgeTargetPlugins = readPurgeTargetPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_PURGE_TARGET_PLUGINS, purgeTargetPlugins);
			
			List<String> deactivatedPlugins = readDeactivatedPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_DEACTIVATED_PLUGINS, deactivatedPlugins);
			
			List<String> priorityOfPlugins = readPriorityOfPlugins();
			SharedMemory.getDataMap().put(BizConst.KEY_PRIORITY_OF_PLUGINS, priorityOfPlugins);
			
			// purge
			for (int i=purgeTargetPlugins.size()-1; i>=0; i--) {
				Path path = Paths.get(System.getProperty("user.dir"), "plugins", purgeTargetPlugins.get(i));
				if (Files.exists(path)) {
					try {
						Files.delete(path);
						purgeTargetPlugins.remove(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					purgeTargetPlugins.remove(i);
				}
			}
			savePurgeTargetPlugins();
			
			// extract plugins
			List<String> plugins = new ArrayList<String>();
			{
				Stream<Path> pathStream = Files.walk(Paths.get(System.getProperty("user.dir"), "plugins"), 1);
				pathStream.forEach(path -> {
					if (isAssignableFromJarFile(path)) {
						plugins.add(path.getFileName().toString());
					}
				});
				pathStream.close();
			}
			
			// remove not exist deactivated plugin
			for (int i=deactivatedPlugins.size()-1; i>=0; i--) {
				String plugin = deactivatedPlugins.get(i);
				if (plugins.contains(plugin) == false) {
					deactivatedPlugins.remove(i);
				}
			}
			saveDeactivatedPlugins();
			
			// delete unused config file
			Path configDirectory = Paths.get(System.getProperty("user.dir"), "plugins", "config");
			if (Files.exists(configDirectory) && Files.isDirectory(configDirectory)) {
				Stream<Path> pathStream = Files.walk(configDirectory, 1);
				pathStream.forEach(path -> {
					String fileName = path.getFileName().toString();
					if (fileName.toLowerCase().endsWith(".config")) {
						fileName = fileName.substring(0, fileName.length()-".config".length());
						if (plugins.contains(fileName) == false) {
							try {
								Files.delete(path);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
				pathStream.close();
			}
			
			// transform priority
			for (int i=priorityOfPlugins.size()-1; i>=0; i--) {
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
			
			// preloader things
			SharedMemory.getDataMap().put(BizConst.KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS, plugins.size());
			EventHandler.callEvent(getClass(), BizConst.EVENT_NOTIFY_PRELOADER_PREPARING_PLUGINS);
			
			// load plugins
			for (String plugin : plugins) {
				try {
					Path path = Paths.get(System.getProperty("user.dir"), "plugins", plugin);
					if (isAssignableFromJarFile(path)) {
						loadPlugin(path);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					EventHandler.callEvent(getClass(), BizConst.EVENT_NOTIFY_PRELOADER_COUNT_DOWN_PLUGIN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// I have to show the main stage even if weird things happens
			if (SharedMemory.getDataMap().get(BizConst.KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS) == null) {
				SharedMemory.getDataMap().put(BizConst.KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS, 0);
				EventHandler.callEvent(getClass(), BizConst.EVENT_NOTIFY_PRELOADER_PREPARING_PLUGINS);
			}
			
			createPromptComponent();
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
			EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_EXIT);
		});
	}
	
	private void enableContextMenu() {
		MenuItem menuAddNewPlugin = new MenuItem("Add a new plugin");
		menuAddNewPlugin.setOnAction(actionEvent -> {
			ExtensionFilter jarFilter = new ExtensionFilter("Actlist Plugin File", "*.jar");
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select a new Actlist plugin file");
			fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir"), "plugins").toFile());
			fileChooser.getExtensionFilters().add(jarFilter);
			fileChooser.setSelectedExtensionFilter(jarFilter);
			
			File file = fileChooser.showOpenDialog(App.getStage());
			if (file == null) {
				return;
			}
			
			installAndLoadThePlugin(file, true);
		});
		
		ContextMenu contextMenu = new ContextMenu(menuAddNewPlugin);
		/*scrollPane.setOnMouseReleased(mouseEvent -> {*/
		componentBoxPagination.setOnMouseReleased(mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {
//				contextMenu.getItems().clear();
//				if (componentBoxPagination.getCurrentPageIndex() == 0) {
//					contextMenu.getItems().addAll(menuAddNewPlugin, menuSeparator, menuAddPage);
//				} else {
//					contextMenu.getItems().addAll(menuAddNewPlugin, menuSeparator, menuAddPage, menuDeletePage);
//				}
				contextMenu.show(App.getStage(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
		});
	}
	
	private void enableDragAndDrop() {
		Predicate<Dragboard> containsJarFileOnly = (dragboard) -> {
			if (dragboard.hasFiles()) {
				List<File> files = dragboard.getFiles();
				for (File file : files) {
					if (file.isFile() == false || file.getName().toLowerCase().endsWith(".jar") == false) {
						return false;
					}
				}
			} else {
				return false;
			}
			
			return true;
		};
		
		scrollPane.setOnDragOver(dragEvent -> {
			if (containsJarFileOnly.test(dragEvent.getDragboard())) {
				dragEvent.acceptTransferModes(TransferMode.COPY);
			} else {
				dragEvent.consume();
			}
		});
		scrollPane.setOnDragDropped(dragEvent -> {
			dragEvent.setDropCompleted(false);
			if (containsJarFileOnly.test(dragEvent.getDragboard())) {
				HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
				int numberOfPluginsBeforeInstall = pluginMap.size();
				
				for (File file : dragEvent.getDragboard().getFiles()) {
					installAndLoadThePlugin(file, false);
					
					dragEvent.setDropCompleted(true);
				}
				
				int numberOfPluginsAfterInstall = pluginMap.size();
				if (numberOfPluginsBeforeInstall != numberOfPluginsAfterInstall) {
					showPluginsView(); // notice to user that there is a new plugin.
				}
			}
			dragEvent.consume();
		});
	}
	
	private void installAndLoadThePlugin(File file, boolean strict) {
		try {
			Path path = PluginManager.install(file, strict);
			if (path != null) {
				PluginManager.load(String.valueOf(path.getFileName()), true);
				
				savePriorityOfPlugins();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(App.getStage(), "Oops... something is weird !");
		}
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
	
	private List<String> readPurgeTargetPlugins() {
		return FileUtil.readFileByLine(Paths.get(System.getProperty("user.dir"), "plugins", "purge.ini"), true).stream().distinct().collect(Collectors.toList());
	}
	
	private void savePurgeTargetPlugins() {
		try {
			List<String> purgeTargetPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_PURGE_TARGET_PLUGINS);
			purgeTargetPlugins = purgeTargetPlugins.stream().distinct().collect(Collectors.toList());
			SharedMemory.getDataMap().put(BizConst.KEY_PURGE_TARGET_PLUGINS, purgeTargetPlugins);
			
			StringBuffer buffer = new StringBuffer();
			for (String purgeTargetPlugin : purgeTargetPlugins) {
				buffer.append(purgeTargetPlugin);
				buffer.append("\r\n");
			}
			
			FileUtil.saveFile(Paths.get(System.getProperty("user.dir"), "plugins", "purge.ini"), buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> readDeactivatedPlugins() {
    	return FileUtil.readFileByLine(Paths.get(System.getProperty("user.dir"), "plugins", "deactivated.ini"), true).stream().distinct().collect(Collectors.toList());
    }
    
    private void saveDeactivatedPlugins() {
    	try {
    		List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
    		deactivatedPlugins = deactivatedPlugins.stream().distinct().collect(Collectors.toList());
    		SharedMemory.getDataMap().put(BizConst.KEY_DEACTIVATED_PLUGINS, deactivatedPlugins);
    		
    		StringBuffer buffer = new StringBuffer();
    		for (String deactivatedPlugin : deactivatedPlugins) {
    			buffer.append(deactivatedPlugin);
    			buffer.append("\r\n");
    		}
    		
            FileUtil.saveFile(Paths.get(System.getProperty("user.dir"), "plugins", "deactivated.ini"), buffer.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
    private List<String> readPriorityOfPlugins() {
    	return FileUtil.readFileByLine(Paths.get(System.getProperty("user.dir"), "plugins", "priority.ini"), true).stream().distinct().collect(Collectors.toList());
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
    		e.printStackTrace();
    	}
    }
	
	private void loadPlugin(Path path) throws Exception {
		String fileName = path.getFileName().toString();
		List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
		PluginManager.load(fileName, !deactivatedPlugins.contains(fileName));
	}
	
	private void createPromptComponent() {
		Runnable action = () -> {
			synchronized (componentBox) {
				if (componentBox.getChildren().isEmpty()) {
					Label firstLine = new Label();
					firstLine.setText("No plugins available.");
					
					Hyperlink explore = new Hyperlink();
					explore.setText("Explore");
					explore.setOnMouseReleased(mouseEvent -> {
						explore.setVisited(false);
						
						showExploreView();
					});
					
					Label dragAndDrop = new Label();
					dragAndDrop.setText("or drag and drop.");
					
					HBox secondLine = new HBox(explore, dragAndDrop);
					secondLine.setAlignment(Pos.CENTER);
					
					VBox vBox = new VBox(firstLine, secondLine);
					vBox.setAlignment(Pos.CENTER);
					vBox.setSpacing(15.0);
					AnchorPane.setTopAnchor(vBox, 0.0);
					AnchorPane.setRightAnchor(vBox, 0.0);
					AnchorPane.setBottomAnchor(vBox, 0.0);
					AnchorPane.setLeftAnchor(vBox, 0.0);
					
					AnchorPane pane = new AnchorPane(vBox);
					pane.setPrefWidth(310);
					pane.setPrefHeight(310);

					componentBox.getChildren().add(pane);
				}
			}
		};
		if (Platform.isFxApplicationThread()) {
			action.run();
		} else {
			Platform.runLater(action);
		}
	}

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_APPLY_THEME:
			applyTheme();
			break;
		case BizConst.EVENT_APPLY_DARK_MODE:
			applyDarkMode();
			break;
		case BizConst.EVENT_PLAY_NEW_PLUGINS_ALARM:
			playNewPluginsAlarm();
			break;
		case BizConst.EVENT_SHOW_PLUGINS_VIEW:
			showPluginsView();
			break;
		case BizConst.EVENT_SHOW_EXPLORE_VIEW:
			showExploreView();
			break;
		case BizConst.EVENT_SHOW_ABOUT_VIEW:
			showAboutView();
			break;
		case BizConst.EVENT_SHOW_CONFIGURATION_VIEW:
			showConfigurationView();
			break;
		case BizConst.EVENT_SAVE_PURGE_TARGET_PLUGINS:
			savePurgeTargetPlugins();
			break;
		case BizConst.EVENT_SAVE_DEACTIVATED_PLUGINS:
			saveDeactivatedPlugins();
			break;
		case BizConst.EVENT_SAVE_PRIORITY_OF_PLUGINS:
			savePriorityOfPlugins();
			break;
		case BizConst.EVENT_CREATE_PROMPT_COMPONENT:
			createPromptComponent();
			break;
		}
	}
	
}
