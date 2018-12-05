package org.silentsoft.actlist.view.explore;

import java.awt.Desktop;
import java.net.URI;

import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;


public class ExploreController extends AbstractViewerController {

	@FXML
	private WebView webView;
	
	@FXML
	private VBox loadingBox;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		new Thread(() -> {
			{
				StringBuffer userAgent = new StringBuffer();
				userAgent.append("Actlist-");
				
				userAgent.append(BuildVersion.VERSION);
				
				if (SystemUtil.isWindows()) {
					userAgent.append(" windows-");
				} else if (SystemUtil.isMac()) {
					userAgent.append(" macosx-");
				} else if (SystemUtil.isLinux()) {
					userAgent.append(" linux-");
				} else {
					userAgent.append(" unknown-");
				}
				userAgent.append(SystemUtil.getOSArchitecture());
				
				userAgent.append(" platform-");
				userAgent.append(SystemUtil.getPlatformArchitecture());
				
				Platform.runLater(() -> {
					webView.getEngine().setUserAgent(userAgent.toString());
				});
			}
			
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
									    String href = anchorElement.getHref();

									    if (Desktop.isDesktopSupported()) {
									    	try {
									    		Desktop.getDesktop().browse(URI.create(href));
									    	} catch (Exception e) {
									    		e.printStackTrace();
									    	}
									    }

									    event.preventDefault();
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
			Platform.runLater(() -> {
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
