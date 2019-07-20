package org.silentsoft.actlist.view.explore;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.plugin.PluginManager;
import org.silentsoft.actlist.plugin.messagebox.MessageBox;
import org.silentsoft.actlist.rest.RESTfulAPI;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLElement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class ExploreController extends AbstractViewerController {

	@FXML
	private WebView webView;
	
	@FXML
	private VBox loadingBox;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		new Thread(() -> {
			Platform.runLater(() -> {
				webView.getEngine().setUserAgent(String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_USER_AGENT)));
				
				webView.getEngine().setCreatePopupHandler((popupFeatures) -> {
					WebEngine webEngine = new WebEngine();
					webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							if (Desktop.isDesktopSupported()) {
								try {
									Desktop.getDesktop().browse(URI.create(newValue));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								webView.getEngine().load(newValue);
							}
						}
					});
					return webEngine;
				});
				
				ChangeListener<Worker.State> stateChangeListener = new ChangeListener<Worker.State>() {
					@Override
					public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
						if (State.SUCCEEDED.equals(newValue)) {
							loadingBox.setVisible(false);
							
					        Document document = webView.getEngine().getDocument();
					        NodeList anchors = document.getElementsByTagName("a");
					        for (int i=0, j=anchors.getLength(); i<j; i++) {
					        	Node anchor = anchors.item(i);
					        	if (anchor instanceof EventTarget) {
					        		((EventTarget) anchor).addEventListener("click", new EventListener() {
										@Override
										public void handleEvent(Event event) {
											HTMLAnchorElement anchorElement = (HTMLAnchorElement)event.getCurrentTarget();
											String className = anchorElement.getClassName();
											if (className != null) {
												if (className.contains("actlist-plugin-download")) {
													String href = anchorElement.getHref();
												    if (href != null) {
												    	if (href.toLowerCase().endsWith(".jar")) {
												    		Consumer<String> changeChildNodeClassName = (value) -> {
												    			if (anchorElement.getChildNodes().getLength() > 0) {
													    			Node node = anchorElement.getChildNodes().item(0);
													    			if (node instanceof HTMLElement) {
													    				HTMLElement iElement = (HTMLElement) node;
													    				iElement.setClassName(value);
													    			}
													    		}
												    		};
												    		
												    		changeChildNodeClassName.accept("fa fa-spinner fa-pulse");
												    		anchorElement.setClassName(className.replace("actlist-plugin-download", "actlist-plugin-installing"));
												    		
												    		new Thread(() -> {
												    			AtomicBoolean succeedToAutoInstall = new AtomicBoolean(false);
												    			AtomicBoolean networkUnavailable = new AtomicBoolean(false);
												    			AtomicInteger responseStatusCode = new AtomicInteger();
												    			AtomicReference<String> responseReasonPhrase = new AtomicReference<>();
												    			AtomicReference<Exception> exception = new AtomicReference<>();
											    				try {
									    							RESTfulAPI.doGet(href, (beforeRequest) -> {
									    								
									    							}, (afterResponse) -> {
									    								try {
									    									StatusLine statusLine = afterResponse.getStatusLine();
									    									if (statusLine != null) {
									    										responseStatusCode.set(statusLine.getStatusCode());
									    										responseReasonPhrase.set(statusLine.getReasonPhrase());
									    									}
									    									
									    									HttpEntity entity = afterResponse.getEntity();
										    								if (entity != null) {
										    									InputStream content = entity.getContent();
										    									
										    									// create a partial file
										    									String uuid = UUID.randomUUID().toString().replaceAll("-", "");
										    									Path partialFilePath = Paths.get(System.getProperty("java.io.tmpdir"), uuid.concat(".partial"));
										    									if (Files.notExists(partialFilePath.getParent())) {
											    									Files.createDirectories(partialFilePath.getParent());							    										
										    									}
										    									Files.createFile(partialFilePath);
										    									
										    									// write into partial file
										    									OutputStream fileStream = new FileOutputStream(partialFilePath.toString());
										    									IOUtils.copy(content, fileStream);
										    									fileStream.close();
										    									
										    									// test valid jar or not
										    									new JarFile(partialFilePath.toString()).close();
										    									
										    									// determine jar file name
										    									String _newPluginFileName = "";
										    									{
										    										try {
										    											Header contentDispositionHeader = afterResponse.getFirstHeader("Content-Disposition");
											    										if (contentDispositionHeader != null && contentDispositionHeader.getValue() != null) {
											    											_newPluginFileName = URLDecoder.decode(StringUtils.substringAfterLast(contentDispositionHeader.getValue().toLowerCase().replaceAll("\"", ""), "filename="), "UTF-8").trim();
											    										}
										    										} catch (Exception e) {
										    											
										    										} finally {
										    											if (ObjectUtil.isEmpty(_newPluginFileName)) {
										    												_newPluginFileName = String.valueOf(Paths.get(URI.create(href).getPath()).getFileName());
										    											}
										    											
										    											if (ObjectUtil.isEmpty(_newPluginFileName)) {
										    												_newPluginFileName = UUID.randomUUID().toString().replaceAll("-", "");
										    											}
										    										}
										    									}
										    									if (_newPluginFileName.toLowerCase().endsWith(".jar") == false) {
										    										_newPluginFileName = _newPluginFileName.concat(".jar");
										    									}
										    									
										    									// check whether duplicated or not and if so, pick a uuid as a file name
										    									Path newPluginFilePath = Paths.get(System.getProperty("user.dir"), "plugins", _newPluginFileName);
										    									if (Files.exists(newPluginFilePath)) {
										    										newPluginFilePath = Paths.get(System.getProperty("user.dir"), "plugins", uuid.concat(".jar"));
										    									}
										    									final String newPluginFileName = String.valueOf(newPluginFilePath.getFileName());
										    									
										    									// move the partial file to the plugins directory
										    									Files.move(partialFilePath, newPluginFilePath);
										    									
										    									CountDownLatch latch = new CountDownLatch(1);
										    									Platform.runLater(() -> {
										    										try {
										    											PluginManager.load(newPluginFileName, true);
												    									
												    									EventHandler.callEvent(getClass(), BizConst.EVENT_SAVE_PRIORITY_OF_PLUGINS);
										    										} catch (Exception | Error e) {
										    											e.printStackTrace();
										    										} finally {
										    											latch.countDown();
										    										}
										    									});
										    									latch.await();
										    									
										    									succeedToAutoInstall.set(true);
										    								}
									    								} catch (Exception e) {
									    									System.out.println(String.format("Failed to install (%s -> status : %d - %s)", href, responseStatusCode.get(), responseReasonPhrase.get()));
									    									e.printStackTrace();
									    									exception.set(e);
									    								}
									    							});
									    						} catch (Exception e) {
									    							e.printStackTrace();
									    							networkUnavailable.set(true);
									    						}
								    							
								    							if (succeedToAutoInstall.get()) {
								    								EventHandler.callEvent(getClass(), BizConst.EVENT_PLAY_NEW_PLUGINS_ALARM);
								    								
								    								Platform.runLater(() -> {
								    									Optional<ButtonType> result = MessageBox.showConfirm(App.getStage(), "Succeed to install", "Do you want to check it out the new plugin ?");
									    								if (result.isPresent() && result.get() == ButtonType.OK) {
									    									EventHandler.callEvent(getClass(), BizConst.EVENT_SHOW_PLUGINS_VIEW, false);
									    								}
								    								});
								    							} else {
								    								if (networkUnavailable.get()) {
								    									if (Desktop.isDesktopSupported()) {
																			try {
																				Desktop.getDesktop().browse(URI.create(href));
																			} catch (Exception e) {
																				e.printStackTrace();
																			}
																		}
								    								} else {
								    									MessageBox.showException(App.getStage(), String.format("Failed to install (%d)", responseStatusCode.get()), responseReasonPhrase.get(), exception.get());
								    								}
								    							}
												    			
												    			Platform.runLater(() -> {
												    				changeChildNodeClassName.accept("fa fa-download");
												    				anchorElement.setClassName(className.replace("actlist-plugin-installing", "actlist-plugin-download"));
												    			});
												    		}).start();
													    } else {
															if (Desktop.isDesktopSupported()) {
																try {
																	Desktop.getDesktop().browse(URI.create(href));
																} catch (Exception e) {
																	e.printStackTrace();
																}
															}
													    }
												    }
												    
												    event.preventDefault();
												} else if (className.contains("actlist-plugin-installing")) {
													event.preventDefault();
												}
											}
										}
									}, false);
					        	}
					        }
					    } else if (State.FAILED.equals(newValue)) {
					    	webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
					    	
					    	showFailureContent();
					    }
					}
				};
				webView.getEngine().getLoadWorker().stateProperty().addListener(stateChangeListener);
				
				webView.getEngine().load("http://actlist.silentsoft.org/explore/");
			});
		}).start();
	}
	
	private void showFailureContent() {
    	StringBuffer html = new StringBuffer();
    	html.append("<html>");
    	html.append("    <head>");
    	html.append("        <style>");
    	html.append("            .container {");
    	html.append("                display: table;");
    	html.append("                width: 100%;");
    	html.append("                height: 100%;");
    	html.append("            ");
    	html.append("            }");
    	html.append("            .content {");
    	html.append("                display: table-cell;");
    	html.append("                vertical-align: middle;");
    	html.append("                text-align: center;");
    	html.append("            ");
    	html.append("            }");
    	html.append("            span {");
    	html.append("                font-family: Verdana;");
    	html.append("            ");
    	html.append("            }");
    	html.append("        </style>");
    	html.append("    </head>");
    	html.append("    <body>");
    	html.append("        <div class='container'>");
    	html.append("            <div class='content'>");
    	html.append("                <div>");
    	html.append("                    <svg width='24' height='24'><path d='M12 2.02c-5.51 0-9.98 4.47-9.98 9.98s4.47 9.98 9.98 9.98 9.98-4.47 9.98-9.98S17.51 2.02 12 2.02zM11.48 20v-6.26H8L13 4v6.26h3.35L11.48 20z'/></svg>");
    	html.append("                </div>");
    	html.append("                <div>");
    	html.append("                    <span>Network unavailable.</span>");
    	html.append("                </div>");
    	html.append("            </div>");
    	html.append("        </div>");
    	html.append("    </body>");
    	html.append("</html>");
    	
    	webView.getEngine().loadContent(html.toString());		
	}
	
}
