package org.silentsoft.actlist.about;

import java.awt.Desktop;
import java.net.URI;

import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.ui.viewer.AbstractViewerController;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AboutController extends AbstractViewerController {

	@FXML
	private Label version;
	
	@FXML
	private Hyperlink homepageLink;

	@FXML
	private VBox sources;
	
	@FXML
	private VBox libraries;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		version.setText(BuildVersion.VERSION);
		
		sources.getChildren().add(createLink("Actlist", "https://github.com/silentsoft/actlist"));
		sources.getChildren().add(createLink("Actlist-plugin", "https://github.com/silentsoft/actlist-plugin"));
		sources.getChildren().add(createLink("Silentsoft-core", "https://github.com/silentsoft/silentsoft-core"));
		sources.getChildren().add(createLink("Silentsoft-io", "https://github.com/silentsoft/silentsoft-io"));
		sources.getChildren().add(createLink("Silentsoft-ui", "https://github.com/silentsoft/silentsoft-ui"));
		sources.getChildren().add(createLink("Silentsoft-net", "https://github.com/silentsoft/silentsoft-net"));
		
		libraries.getChildren().add(createLink("Apache-commons", "https://commons.apache.org"));
		libraries.getChildren().add(createLink("Apache-httpclient", "https://hc.apache.org"));
		libraries.getChildren().add(createLink("Proxy-vole", "https://github.com/MarkusBernhardt/proxy-vole"));
		libraries.getChildren().add(createLink("JKeyMaster", "https://github.com/tulskiy/jkeymaster"));
		libraries.getChildren().add(createLink("Jidefx-common", "http://github.com/jidesoft/jidefx-oss"));
		libraries.getChildren().add(createLink("JFoenix", "https://github.com/jfoenixadmin/JFoenix"));
		libraries.getChildren().add(createLink("ControlsFx", "https://bitbucket.org/controlsfx/controlsfx/"));
		libraries.getChildren().add(createLink("Centerdevice-nsmenufx", "https://github.com/codecentric/NSMenuFX"));
		libraries.getChildren().add(createLink("PlusHaze-TrayNotification", "https://github.com/PlusHaze/TrayNotification"));
		libraries.getChildren().add(createLink("Jackson", "https://github.com/FasterXML/jackson"));
		libraries.getChildren().add(createLink("Json", "https://github.com/stleary/JSON-java"));
		libraries.getChildren().add(createLink("JNA", "https://github.com/java-native-access/jna"));
		libraries.getChildren().add(createLink("Slf4j", "https://github.com/qos-ch/slf4j"));
		libraries.getChildren().add(createLink("Log4j", "https://github.com/apache/log4j"));
		libraries.getChildren().add(createLink("Junit", "https://github.com/junit-team/junit4"));
		
	}
	
	private Label createLink(String name, String link) {
		Label label = new Label(name);
		label.setCursor(Cursor.HAND);
		label.setUnderline(true);
		label.setPadding(Insets.EMPTY);
		label.setOnMouseClicked(mouseEvent -> {
			try {
				Desktop.getDesktop().browse(new URI(link));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return label;
	}
	
	@FXML
	private void browseHomepage() {
		homepageLink.setVisited(false);
		
		try {
			Desktop.getDesktop().browse(new URI("http://silentsoft.org"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
