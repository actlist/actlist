package org.silentsoft.actlist.plugin;

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.controlsfx.control.PopOver;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.comparator.VersionComparator;
import org.silentsoft.actlist.plugin.ActlistPlugin.Function;
import org.silentsoft.actlist.plugin.ActlistPlugin.SupportedPlatform;
import org.silentsoft.actlist.plugin.about.PluginAbout;
import org.silentsoft.actlist.plugin.messagebox.MessageBox;
import org.silentsoft.actlist.plugin.tray.TrayNotification;
import org.silentsoft.actlist.rest.RESTfulAPI;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.JSONUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tray.animations.AnimationType;


public class PluginComponent implements EventListener {

	@FXML
	private AnchorPane root;
	
	@FXML
	private JFXHamburger hand;
	
	@FXML
	private HBox pluginLoadingBox;
	
	@FXML
	private Label lblPluginName;

	@FXML
	private Label warningLabel;
	@FXML
	private Label updateAlarmLabel;
	@FXML
	private JFXToggleButton togActivator;
	
	@FXML
	private VBox contentLoadingBox;
	
	@FXML
	private VBox contentBox;
	
	private String pluginFileName;
	public String getPluginFileName() {
		return pluginFileName;
	}
	
	private ActlistPlugin plugin;
	
	private PopOver popOver;
	
	private ObservableList<Node> functions;
	
	private HashMap<org.silentsoft.actlist.plugin.tray.TrayNotification, tray.notification.TrayNotification> trayNotifications = new HashMap<org.silentsoft.actlist.plugin.tray.TrayNotification, tray.notification.TrayNotification>();
	
	private boolean isAvailableNewPlugin = false;
	private URI newPluginURI;
	
	public void initialize() {
		// This method is automatically called by FXMLLoader.
	}
	
	public void initialize(String pluginFileName, Class<? extends ActlistPlugin> pluginClass, boolean activated) {
		this.pluginFileName = pluginFileName;
		
		new Thread(() -> {
			AtomicBoolean shouldTraceException = new AtomicBoolean(true);
			try {
				makeConsumable();
				makeDraggable();
				
				plugin = pluginClass.newInstance();
				
				popOver = new PopOver(new VBox());
				((VBox) popOver.getContentNode()).setPadding(new Insets(3, 3, 3, 3));
				popOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
				
				SupportedPlatform currentPlatform = null;
				{
					if (SystemUtil.isWindows()) {
						currentPlatform = SupportedPlatform.WINDOWS;
					} else if (SystemUtil.isMac()) {
						currentPlatform = SupportedPlatform.MACOSX;
					} /* else if (SystemUtil.isLinux()) {
						currentPlatform = SupportedPlatform.LINUX;
					} else {
						currentPlatform = SupportedPlatform.UNKNOWN;
					} */
				}
				plugin.currentPlatformObject().set(currentPlatform);
				
				SupportedPlatform[] supportedPlatforms = plugin.getSupportedPlatforms();
				if (supportedPlatforms != null) {
					if (supportedPlatforms.length > 0 && Arrays.asList(supportedPlatforms).contains(currentPlatform) == false) {
						shouldTraceException.set(false);
						
						List<String> listOfSupportedPlatform = Arrays.stream(supportedPlatforms).map(Enum::name).collect(Collectors.toList());
						String errorMessage = String.join("", "This plugin only supports ", String.join(", ", listOfSupportedPlatform));
						Runnable errorDialog = () -> {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							alert.setHeaderText("Unsupported platform");
							alert.setContentText(errorMessage);
							alert.showAndWait();
						};
						lblPluginName.setOnMouseClicked(mouseEvent -> {
							if (mouseEvent.getButton() == MouseButton.PRIMARY) {
								errorDialog.run();
							}
						});
						warningLabel.setOnMouseClicked(mouseEvent -> {
							errorDialog.run();
						});
						warningLabel.setVisible(true);
						playFadeTransition(warningLabel);
						
						throw new Exception(errorMessage);
					}
				}
				
				String minimumCompatibleVersion = plugin.getMinimumCompatibleVersion();
				if (minimumCompatibleVersion != null) {
					if (VersionComparator.getInstance().compare(BuildVersion.VERSION, minimumCompatibleVersion) < 0) {
						shouldTraceException.set(false);
						
						String errorMessage = String.join("", "This plugin requires at least Actlist ", minimumCompatibleVersion);
						Runnable confirmDialog = () -> {
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Confirmation");
							alert.setHeaderText(errorMessage);
							alert.setContentText("Would you like to check latest Actlist now ?");
							Optional<ButtonType> result = alert.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.OK) {
								try {
	        						Desktop.getDesktop().browse(new URI("http://silentsoft.org/actlist/archives/"));
	        					} catch (Exception e) {
	        						
	        					}
							}
						};
						lblPluginName.setOnMouseClicked(mouseEvent -> {
							if (mouseEvent.getButton() == MouseButton.PRIMARY) {
								confirmDialog.run();
							}
						});
						warningLabel.setOnMouseClicked(mouseEvent -> {
							confirmDialog.run();
						});
						warningLabel.setVisible(true);
						playFadeTransition(warningLabel);
						
						throw new Exception(errorMessage);
					}
				}
				
				HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
				plugin.classLoaderObject().set(pluginMap.get(pluginFileName));
				
				plugin.proxyHostObject().set(RESTfulAPI.getProxyHost());
				
				plugin.setPluginConfig(new PluginConfig(pluginFileName));
				File configFile = Paths.get(System.getProperty("user.dir"), "plugins", "config", pluginFileName.concat(".config")).toFile();
				if (configFile.exists()) {
					String configContent = FileUtil.readFile(configFile);
					PluginConfig pluginConfig = JSONUtil.JSONToObject(configContent, PluginConfig.class);
					if (pluginConfig != null) {
						plugin.setPluginConfig(pluginConfig);
					}
				}
				
				Platform.runLater(() -> {
					try {
						String pluginName = plugin.getPluginName();
						String pluginDescription = plugin.getPluginDescription();
						
						lblPluginName.setText((pluginName == null || pluginName.trim().isEmpty()) ? pluginFileName : pluginName);
						if (ObjectUtil.isNotEmpty(pluginDescription)) {
							lblPluginName.setTooltip(new Tooltip(pluginDescription));
						}
						
						String warningText = plugin.getWarningText();
						if (ObjectUtil.isNotEmpty(warningText)) {
							warningLabel.setVisible(true);
							playFadeTransition(warningLabel);
						} else {
							warningLabel.setVisible(false);
						}
						
						if (plugin.isOneTimePlugin()) {
							togActivator.setSelected(false);
						} else {
							togActivator.setSelected(activated);
						}
						
//						popOver = new PopOver(new VBox());
//						((VBox) popOver.getContentNode()).setPadding(new Insets(3, 3, 3, 3));
//						popOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
						
						plugin.shouldShowLoadingBar().addListener((observable, oldValue, newValue) -> {
							if (oldValue == newValue) {
								return;
							}
							
							displayLoadingBar(newValue);
						});
						plugin.exceptionObject().addListener((observable, oldValue, newValue) -> {
							if (newValue != null) {
								makeDisable(newValue, true);
								
								plugin.exceptionObject().set(null);
							}
						});
						plugin.showTrayNotificationObject().addListener((observable, oldValue, newValue) -> {
							if (newValue != null) {
								tray.notification.TrayNotification trayNotification = new tray.notification.TrayNotification();
								
								synchronized (trayNotifications) {
									trayNotifications.put(newValue, trayNotification);
								}
								
								trayNotification.setRectangleFill(Paint.valueOf("#222222"));
								trayNotification.setImage(App.getIcons().get(4)); // 128x128
								trayNotification.setAnimationType(AnimationType.POPUP);
								
								String titleValue = (plugin.getPluginName() == null || plugin.getPluginName().trim().isEmpty()) ? pluginFileName : plugin.getPluginName();
								String titlePrefix = String.format("[%s] ", titleValue);
								if (newValue.getTitle() == null || newValue.getTitle().trim().isEmpty()) {
									trayNotification.setTitle(titlePrefix.concat(""));
								} else {
									trayNotification.setTitle(titlePrefix.concat(newValue.getTitle()));
								}
								
								if (newValue.getMessage() == null || newValue.getMessage().trim().isEmpty()) {
									trayNotification.setMessage("(empty message)");
								} else {
									trayNotification.setMessage(newValue.getMessage());
								}
								
								if (newValue.getDuration() == null) {
									trayNotification.setOnDismiss((actionEvent) -> {
										synchronized (trayNotifications) {
											trayNotifications.remove(trayNotification);
										}
										
										EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_BRING_TO_FRONT);
										/*AnimationUtils.createTransition(lblPluginName, jidefx.animation.AnimationType.FLASH).play();*/
										// TODO : scrollTo
									});
									
									trayNotification.showAndWait();
								} else {
									trayNotification.showAndDismiss(newValue.getDuration());
								}
								
								plugin.showTrayNotificationObject().set(null);
							}
						});
						plugin.dismissTrayNotificationObject().addListener((observable, oldValue, newValue) -> {
							if (newValue != null) {
								synchronized (trayNotifications) {
									if (trayNotifications.containsKey(newValue)) {
										tray.notification.TrayNotification trayNotification = trayNotifications.get(newValue);
										trayNotification.setOnDismiss((actionEvent) -> {
											trayNotifications.remove(newValue);
										});
										trayNotification.dismiss();
									}
								}
								
								plugin.dismissTrayNotificationObject().set(null);
							}
						});
						plugin.shouldDismissTrayNotifications().addListener((observable, oldValue, newValue) -> {
							if (newValue) {
								synchronized (trayNotifications) {
									for (Entry<TrayNotification, tray.notification.TrayNotification> entrySet : trayNotifications.entrySet()) {
										tray.notification.TrayNotification trayNotification = entrySet.getValue();
										trayNotification.setOnDismiss((actionEvent) -> {
											trayNotifications.remove(newValue);
										});
										trayNotification.dismiss();
									}
									
									plugin.shouldDismissTrayNotifications().set(false);
								}
							}
						});
						plugin.shouldBrowseActlistArchives().addListener((observable, oldValue, newValue) -> {
							if (newValue) {
								try {
	        						Desktop.getDesktop().browse(new URI("http://silentsoft.org/actlist/archives/"));
	        					} catch (Exception e) {
	        						
	        					}
								
								plugin.shouldBrowseActlistArchives().set(false);
							}
						});
						plugin.shouldRequestShowActlist().addListener((observable, oldValue, newValue) -> {
							if (newValue) {
								if (App.isHidden()) {
									// request show
									EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_SHOW_HIDE, false);
								}
							} else {
								if (App.isShown()) {
									// request hide
									EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_SHOW_HIDE, false);
								}
							}
						});
						plugin.shouldRequestDeactivate().addListener((observable, oldValue, newValue) -> {
							if (newValue) {
								if (isActivated()) {
									togActivator.setSelected(false);
									deactivated();
								}
								
								plugin.shouldRequestDeactivate().set(false);
							}
						});
						
						/**
						 * Exception will raised when if control their graphic node on initialize() method.
						 * so, need to mapping controller to plugin before call initialize method.
						 * below getGraphic() method will mapping controller to plugin.
						 */
						if (plugin.existsGraphic()) {
							plugin.getGraphic();
						}
						
						plugin.initialize();
						
						functions = FXCollections.observableArrayList();
						for (Function function : plugin.getFunctionMap().values()) {
							addFunction(function);
						}
						
						if (activated && plugin.isOneTimePlugin() == false) {
							activated();
						}
						
						EventHandler.addListener(this);
					} catch (Throwable e) {
						lblPluginName.setText(pluginFileName);
						
						makeDisable(e, shouldTraceException.get());
					} finally {
						pluginLoadingBox.setVisible(false);
						
						new Thread(() -> {
							Runnable checkUpdate = () -> {
								try {
									URI pluginUpdateCheckURI = plugin.getPluginUpdateCheckURI();
									if (pluginUpdateCheckURI != null) {
										Map<String, Object> result = null;
										
										ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
						    			param.add(new BasicNameValuePair("version", plugin.getPluginVersion()));
						    			/* below values are unnecessary. version value is enough.
						    			param.add(new BasicNameValuePair("os", SystemUtil.getOSName()));
						    			param.add(new BasicNameValuePair("architecture", SystemUtil.getPlatformArchitecture()));
						    			*/
										
										String uri = pluginUpdateCheckURI.toString();
										if (uri.matches("(?i).*\\.js")) {
											StringBuffer script = new StringBuffer();
											script.append(String.format("var version = '%s';", plugin.getPluginVersion())).append("\r\n");
											script.append(RESTfulAPI.doGet(pluginUpdateCheckURI.toString(), param, String.class));
											
											ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
											Object _result = scriptEngine.eval(script.toString());
											if (_result instanceof Map) {
												result = (Map) _result;
											}
										} else {
							    			result = RESTfulAPI.doGet(pluginUpdateCheckURI.toString(), param, Map.class);
										}
						    			
						    			if (result == null) {
						    				return;
						    			}
						    			
						    			if (result.containsKey("available")) {
						    				isAvailableNewPlugin = Boolean.parseBoolean(String.valueOf(result.get("available")));
						    				if (isAvailableNewPlugin) {
						    					if (result.containsKey("url")) {
						    						try {
						    							newPluginURI = new URI(String.valueOf(result.get("url")));
						        					} catch (Exception e) {
						        						e.printStackTrace();
						        					}
						    					}
						    					
						    					try {
						    						plugin.pluginUpdateFound();
						    					} catch (Exception e) {
						    						e.printStackTrace();
						    					}

						    					URI pluginArchivesURI = plugin.getPluginArchivesURI();
					    						if (pluginArchivesURI != null) {
					    							newPluginURI = pluginArchivesURI;
					    						}
						    					
						    					if (newPluginURI != null) {
						    						updateAlarmLabel.setVisible(true);
						    						playFadeTransition(updateAlarmLabel);
						    					} else {
						    						updateAlarmLabel.setVisible(false);
						    					}
						    				}
						    			}
						    			
						    			if (result.containsKey("killSwitch")) {
						    				boolean hasTurnedOnKillSwitch = "on".equalsIgnoreCase(String.valueOf(result.get("killSwitch")).trim());
						    				if (hasTurnedOnKillSwitch) {
						    					String message = "The plugin's kill switch has turned on by the author.";
						    					
						    					makeDisable(new Exception(message), false);
						    					
						    					warningLabel.setOnMouseClicked(mouseEvent -> {
													MessageBox.showInformation(App.getStage(), message);
												});
												warningLabel.setVisible(true);
												playFadeTransition(warningLabel);
						    				}
						    			}
						    			
						    			if (result.containsKey("endOfService")) {
						    				boolean hasEndOfService = Boolean.parseBoolean(String.valueOf(result.get("endOfService")));
											if (hasEndOfService) {
												warningLabel.setOnMouseClicked(mouseEvent -> {
													MessageBox.showInformation(App.getStage(), "This plugin has reached end of service by the author.");
												});
												warningLabel.setVisible(true);
												playFadeTransition(warningLabel);
											}
						    			}
									}
								} catch (Exception e) {
									e.printStackTrace(); // print stack trace only ! do nothing ! b/c of its not kind of critical exception.
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
				});
			} catch (Throwable e) {
				Platform.runLater(() -> {
					lblPluginName.setText(pluginFileName);
					
					makeDisable(e, shouldTraceException.get());
					
					pluginLoadingBox.setVisible(false);
				});
			}
		}).start();
	}
	
	void clear() {
		if (isActivated()) {
			try {
				clearPluginGraphicAndDeactivate();
			} catch (Exception e) {
				
			}
		}
		
		plugin.classLoaderObject().set(null);
		plugin = null;
	}
	
	private void playFadeTransition(Node node) {
		Runnable action = () -> {
			if (node.isVisible()) {
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), node);
				fadeTransition.setFromValue(1.0);
				fadeTransition.setToValue(0.3);
				fadeTransition.setCycleCount(6);
				fadeTransition.setAutoReverse(true);
				 
				fadeTransition.play();
			}
		};
		if (Platform.isFxApplicationThread()) {
			action.run();
		} else {
			Platform.runLater(() -> {
				action.run();
			});
		}
	}
	
	private void makeDisable(Throwable throwable, boolean shouldTraceException) {
		new Thread(() -> {
			if (togActivator.selectedProperty().get()) {
				try {
					// wait for animation to the end.
					Thread.sleep(100);
				} catch (Exception e) {
					
				}
			}
			
			Platform.runLater(() -> {
				lblPluginName.setCursor(Cursor.HAND);
				
				if (shouldTraceException) {
					Runnable exceptionDialog = () -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Exception Dialog");
						alert.setHeaderText(pluginFileName);

						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						throwable.printStackTrace(pw);
						String exceptionText = sw.toString();

						TextArea textArea = new TextArea(exceptionText);
						textArea.setEditable(false);
						textArea.setWrapText(true);

						textArea.setMaxWidth(Double.MAX_VALUE);
						textArea.setMaxHeight(Double.MAX_VALUE);
						GridPane.setVgrow(textArea, Priority.ALWAYS);
						GridPane.setHgrow(textArea, Priority.ALWAYS);

						GridPane content = new GridPane();
						content.setMaxWidth(Double.MAX_VALUE);
						content.add(textArea, 0, 0);

						alert.getDialogPane().setContent(content);

						alert.showAndWait();
					};
					
					lblPluginName.setTooltip(new Tooltip("Click to show the exception log."));
					lblPluginName.setOnMouseClicked(mouseEvent -> {
						if (mouseEvent.getButton() == MouseButton.PRIMARY) {
							exceptionDialog.run();
						}
					});
					
					warningLabel.setOnMouseClicked(mouseEvent -> {
						exceptionDialog.run();
					});
					warningLabel.setVisible(true);
					playFadeTransition(warningLabel);
				} else {
					lblPluginName.setTooltip(new Tooltip(throwable.getMessage()));
				}
				
				togActivator.setUnToggleLineColor(Paint.valueOf("#da4242"));
				togActivator.setDisable(true);
				togActivator.setOpacity(1.0); // remove disable effect.
				togActivator.setSelected(false);
				
				clearPluginGraphic();
				
				EventHandler.removeListener(this);
			});
		}).start();
	}
	
	private void makeConsumable() {
		// This code prevents mouse events from going to the bottom scroll pane component.
		root.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
			mouseEvent.consume();
		});
	}
	
	private void makeDraggable() {
		hand.setOnDragDetected(mouseEvent -> {
			createSnapshot(mouseEvent);
			
			root.startFullDrag();
		});
		root.addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, mouseDragEvent -> {
			VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
			if (componentBox.getChildren().contains(mouseDragEvent.getGestureSource())) {
				root.setStyle("-fx-background-color: #f2f2f2;");
			}
		});
		root.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, mouseDragEvent -> {
			VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
			if (componentBox.getChildren().contains(mouseDragEvent.getGestureSource())) {
				deleteSnapshot();
				
				// move index of dragging node to index of drop target.
				int indexOfDraggingNode = componentBox.getChildren().indexOf(mouseDragEvent.getGestureSource());
				int indexOfDropTarget = componentBox.getChildren().indexOf(root);
				if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
					final Node node = componentBox.getChildren().remove(indexOfDraggingNode);
					componentBox.getChildren().add(indexOfDropTarget, node);
				}
				
				EventHandler.callEvent(getClass(), BizConst.EVENT_SAVE_PRIORITY_OF_PLUGINS);
			}
		});
		root.addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, mouseDragEvent -> {
			VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
			if (componentBox.getChildren().contains(mouseDragEvent.getGestureSource())) {
				root.setStyle("-fx-background-color: #ffffff;");
			}
		});
		hand.setOnMouseReleased(mouseEvent -> {
			// in most cases, the 'root' will consume the mouse drag event. so will not being called this event.
			// but the meaning of this event being called is that it is outside the drag area. so, must remove the snapshot.
			deleteSnapshot();
		});
	}
	
	private void createSnapshot(MouseEvent mouseEvent) {
		ImageView snapshot = new ImageView(root.snapshot(null, null));
		snapshot.setManaged(false);
		snapshot.setMouseTransparent(true);
		snapshot.setEffect(new DropShadow(3.0, 0.0, 1.5, Color.valueOf("#333333")));
		
		VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
		componentBox.getChildren().add(snapshot);
		componentBox.setUserData(snapshot);
		componentBox.setOnMouseDragged(event -> {
			snapshot.relocate(event.getX() - mouseEvent.getX(), event.getY() - mouseEvent.getY());
		});
	}
	
	private void deleteSnapshot() {
		VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
		componentBox.setOnMouseDragged(null);
		componentBox.getChildren().remove(componentBox.getUserData());
		componentBox.setUserData(null);
	}
	
	private void addFunction(Function function) {
		functions.add(createFunctionBox(new Label("", function.graphic), mouseEvent -> {
			try {
				if (function.action != null) {
					function.action.run();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				popOver.hide();
			}
		}));
	}
	
	private HBox createFunctionBox(Node node, javafx.event.EventHandler<? super MouseEvent> action) {
		HBox hBox = new HBox(node);
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(3, 3, 3, 3));
		hBox.setStyle("-fx-background-color: white;");
		hBox.setOnMouseEntered(mouseEvent -> {
			hBox.setStyle("-fx-background-color: lightgray;");
		});
		hBox.setOnMouseExited(mouseEvent -> {
			hBox.setStyle("-fx-background-color: white;");
		});
		hBox.setOnMouseClicked(action);
		
		return hBox;
	}
	
	private Stage aboutStage;
	private HBox createAboutFunction() {
		return createFunctionBox(new Label("About"), mouseEvent -> {
			showAboutStage();
		});
	}
	@FXML
	private void showAboutStage() {
		/**
		 * this aboutStage must be closed when if already opened.
		 * because the newPluginURI variable will be set by another thread.
		 */
		if (aboutStage != null) {
			aboutStage.close();
			aboutStage = null;
		}
		
		aboutStage = new Stage();
		aboutStage.initOwner(App.getStage());
		aboutStage.initStyle(StageStyle.UTILITY);
		{
			BorderPane scene = new BorderPane();
			scene.setCenter(new PluginAbout(this.plugin, this.isAvailableNewPlugin, this.newPluginURI).getViewer());
			
			aboutStage.setScene(new Scene(scene));
		}
		aboutStage.show();
	}
	
	@FXML
	private void showWarningText() {
		try {
			String warningText = plugin.getWarningText();
			if (ObjectUtil.isNotEmpty(warningText)) {
				MessageBox.showWarning(App.getStage(), warningText);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HBox createUpgradeFunction() {
		return createFunctionBox(new Label("Upgrade"), mouseEvent -> {
			ExtensionFilter jarFilter = new ExtensionFilter("Actlist Plugin File", "*.jar");
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select a new Actlist plugin file to upgrade");
			fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.dir"), "plugins").toFile());
			fileChooser.getExtensionFilters().add(jarFilter);
			fileChooser.setSelectedExtensionFilter(jarFilter);
			
			File file = fileChooser.showOpenDialog(App.getStage());
			
			if (file == null) {
				return;
			}
			
			if (file.getName().equals(pluginFileName) == false && Paths.get(System.getProperty("user.dir"), "plugins", file.getName()).toFile().exists()) {
				HashMap<String, URLClassLoader> pluginMap = (HashMap<String, URLClassLoader>) SharedMemory.getDataMap().get(BizConst.KEY_PLUGIN_MAP);
				if (pluginMap.containsKey(file.getName())) {
					MessageBox.showError(App.getStage(), "The selected file name is already in use by another plugin !");
					return;
				}
			}
			
			try {
				pluginLoadingBox.setVisible(true);
				
				boolean succeedToInstall = PluginManager.install(file);
				if (succeedToInstall) {
					VBox componentBox = (VBox) SharedMemory.getDataMap().get(BizConst.KEY_COMPONENT_BOX);
					int indexOfThisPlugin = componentBox.getChildren().indexOf(root);
					
					PluginManager.delete(pluginFileName);
					
					PluginManager.load(file.getName(), true, indexOfThisPlugin);
					
					EventHandler.callEvent(getClass(), BizConst.EVENT_SAVE_PRIORITY_OF_PLUGINS);
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showError(App.getStage(), "Oops... something is weird !");
			} finally {
				pluginLoadingBox.setVisible(false);
			}
		});
	}
	
	private HBox createDeleteFunction() {
		Label label = new Label("Delete");
		label.setTextFill(Paint.valueOf("#db0018"));
		
		return createFunctionBox(label, mouseEvent -> {
			Optional<ButtonType> result = MessageBox.showConfirm(App.getStage(), "Are you sure you want to delete this plugin ?");
			if (result.isPresent() && result.get() == ButtonType.OK) {
				try {
					PluginManager.delete(pluginFileName);
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.showError(App.getStage(), e.getMessage());
				}
			}
		});
	}
	
	private void displayLoadingBar(boolean shouldShowLoadingBar) {
		if (plugin.existsGraphic()) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					contentLoadingBox.getChildren().clear();
					
					if (shouldShowLoadingBar) {
						contentLoadingBox.getChildren().add(new JFXSpinner());
					}
					
					contentLoadingBox.setVisible(shouldShowLoadingBar);
				}
			};
			
			if (Platform.isFxApplicationThread()) {
				runnable.run();
			} else {
				Platform.runLater(() -> {
					runnable.run();
				});
			}
		}
	}
	
	@FXML
	private void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseButton.SECONDARY) {
			if (popOver != null) { // if the plugin had thrown an Exception then popOver might be null.
				((VBox) popOver.getContentNode()).getChildren().clear();
				
				((VBox) popOver.getContentNode()).getChildren().add(createAboutFunction());
				/**
				 * NO ! It's not works properly on windows system yet !
				   ((VBox) popOver.getContentNode()).getChildren().add(createUpgradeFunction());
				 */
				
				if (isActivated()) {
					if (plugin.getFunctionMap().size() > 0) {
						((VBox) popOver.getContentNode()).getChildren().add(new Separator(Orientation.HORIZONTAL));
					}
					
					((VBox) popOver.getContentNode()).getChildren().addAll(functions);
					
					/**
					 * NO ! It's not works properly on windows system yet !
					   if (plugin.getFunctionMap().size() > 0) {
					       ((VBox) popOver.getContentNode()).getChildren().add(new Separator(Orientation.HORIZONTAL));
					   }
					 */
				}
				
				/*
				 * NO ! It's not works properly on windows system yet !
				   ((VBox) popOver.getContentNode()).getChildren().add(createDeleteFunction());
				 */
				
				// reason of why the owner is pluginLoadingBox is for hiding automatically when lost focus.
				popOver.show(pluginLoadingBox, e.getScreenX(), e.getScreenY());
			}
		}
	}
	
	@FXML
	private void toggleOnAction() {
		if (isActivated()) {
			activated();
		} else {
			deactivated();
		}
	}
	
	private void loadPluginGraphic() {
		/*
		  <VBox fx:id="contentBox" layoutX="35.0" layoutY="50.0" prefWidth="380.0" AnchorPane.leftAnchor="35.0" AnchorPane.rightAnchor="20.0">
		     <children>
		        <!-- Generate by code. 
		        <BorderPane fx:id="contentPane" />
		        <Separator prefWidth="215.0">
		           <padding>
		              <Insets top="5.0" />
		           </padding>
		        </Separator>
		        -->
		     </children>
		  </VBox>
		  <VBox fx:id="contentLoadingBox" visible="false" alignment="CENTER" layoutX="35.0" layoutY="50.0" prefWidth="380.0" style="-fx-background-color: white;" AnchorPane.leftAnchor="35.0" AnchorPane.rightAnchor="0.0">
		     <children>
		        <!-- Generate by code. 
		        <JFXSpinner />
		        -->
		     </children>
		  </VBox>
		 */
		
		try {
			if (plugin.existsGraphic()) {
				Node pluginContent = plugin.getGraphic();
				if (pluginContent != null) {
					contentBox.getChildren().add(new BorderPane(pluginContent));
					Separator contentLine = new Separator();
					contentLine.setPrefWidth(215.0);
					contentLine.setPadding(new Insets(5.0, 0.0, 0.0, 0.0));
					contentBox.getChildren().add(contentLine);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearPluginGraphic() {
		contentBox.getChildren().clear();
		contentLoadingBox.getChildren().clear();
		
		// it could be null if occur error during initialize
		if (popOver != null) {
			popOver.hide();
		}
	}
	
	boolean isActivated() {
		return togActivator.selectedProperty().get();
	}
	
	void clearPluginGraphicAndDeactivate() throws Exception {
		clearPluginGraphic();
		plugin.pluginDeactivated();
	}
	
	private void activated() {
		displayLoadingBar(true);
		
		new Thread(() -> {
			Platform.runLater(() -> {
				try {
					plugin.pluginActivated();
					loadPluginGraphic();
					
					if (plugin.isOneTimePlugin() == false) {
						List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
						deactivatedPlugins.remove(pluginFileName);
						EventHandler.callEvent(getClass(), BizConst.EVENT_SAVE_DEACTIVATED_PLUGINS);
					}
				} catch (Throwable e) {
					makeDisable(e, true);
				} finally {
					displayLoadingBar(false);
				}
			});
		}).start();
	}
	
	private void deactivated() {
		new Thread(() -> {
			Platform.runLater(() -> {
				try {
					if (plugin.isOneTimePlugin() == false) {
						List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get(BizConst.KEY_DEACTIVATED_PLUGINS);
						deactivatedPlugins.add(pluginFileName);
						EventHandler.callEvent(getClass(), BizConst.EVENT_SAVE_DEACTIVATED_PLUGINS);
					}
					
					clearPluginGraphicAndDeactivate();
				} catch (Throwable e) {
					makeDisable(e, true);
				}
			});
		}).start();
	}
	
	@Override
	public void onEvent(String event) {
		if (isActivated()) {
			try {
				switch (event) {
				case BizConst.EVENT_APPLICATION_ACTIVATED:
					plugin.applicationActivated();
					break;
				case BizConst.EVENT_APPLICATION_DEACTIVATED:
					plugin.applicationDeactivated();
					break;
				case BizConst.EVENT_APPLICATION_CLOSE_REQUESTED:
					plugin.applicationCloseRequested();
					break;
				case BizConst.EVENT_UPDATE_PROXY_HOST:
					plugin.proxyHostObject().set(RESTfulAPI.getProxyHost());
					break;
				}
			} catch (Exception e) {
				
			}
		}
	}
}
