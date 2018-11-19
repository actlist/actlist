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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.web.WebView;


public class ExploreController extends AbstractViewerController {

	@FXML
	private WebView webView;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
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
			
			webView.getEngine().setUserAgent(userAgent.toString());
		}
		
		webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (State.SUCCEEDED.equals(newValue)) {
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
			    }
			}
		});
		
		webView.getEngine().load("http://actlist.silentsoft.org/explore/");
	}
	
}
