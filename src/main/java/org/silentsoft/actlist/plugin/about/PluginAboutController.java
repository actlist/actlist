package org.silentsoft.actlist.plugin.about;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.silentsoft.actlist.plugin.ActlistPlugin;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.ui.viewer.AbstractViewerController;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class PluginAboutController extends AbstractViewerController {

	@FXML
	private ImageView iconImage;
	
	@FXML
	private Label name;
	
	@FXML
	private HBox versionAndAuthorBox;
	
	@FXML
	private Label version;
	
	@FXML
	private Label authorText;
	
	@FXML
	private Hyperlink authorLink;
	
	@FXML
	private HBox newVersionBox;
	
	@FXML
	private Hyperlink newVersionLink;
	
	@FXML
	private ScrollPane masterPane;
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private WebView descriptionView;
	
	@FXML
	private WebView changeLogView;
	
	@FXML
	private WebView licenseView;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		if (parameters != null && parameters.length > 0) {
			Object _plugin = parameters[0];
			if (_plugin instanceof ActlistPlugin) {
				ActlistPlugin plugin = (ActlistPlugin) _plugin;
				
				if (plugin.existsIcon()) {
					try {
						iconImage.setImage(plugin.getIcon().getImage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (ObjectUtil.isNotEmpty(plugin.getPluginName())) {
					name.setText(plugin.getPluginName());
				}
				
				if (ObjectUtil.isNotEmpty(plugin.getPluginVersion())) {
					version.setText(plugin.getPluginVersion());
				}
				
				if (ObjectUtil.isNotEmpty(plugin.getPluginAuthor())) {
					if (ObjectUtil.isNotEmpty(plugin.getPluginAuthorURI())) {
						authorLink.setText(plugin.getPluginAuthor());
						authorLink.setOnAction(actionEvent -> {
							authorLink.setVisited(false);
							
							try {
								Desktop.getDesktop().browse(plugin.getPluginAuthorURI());
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					} else {
						authorText.setText(" by ".concat(plugin.getPluginAuthor()));
					}
				}
				
				{
					Object _newPluginURI = parameters[1];
					if (_newPluginURI == null || (_newPluginURI instanceof URI) == false) {
						newVersionBox.setVisible(false);
						
						VBox parent = (VBox) newVersionBox.getParent();
						parent.getChildren().remove(newVersionBox);
					} else {
						newVersionBox.setVisible(true);
						
						URI newPluginURI = (URI) _newPluginURI;
						newVersionLink.setOnAction(actionEvent -> {
							newVersionLink.setVisited(false);
							
							try {
								Desktop.getDesktop().browse(newPluginURI);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					}
				}
				
				setContentToWebView(descriptionView, plugin.getPluginDescriptionURI(), plugin.getPluginDescription());
				setContentToWebView(changeLogView, plugin.getPluginChangeLogURI(), plugin.getPluginChangeLog());
				setContentToWebView(licenseView, plugin.getPluginLicenseURI(), plugin.getPluginLicense());
			}
		}
	}
	
	private void setContentToWebView(WebView webView, URI uri, String text) {
		BufferedReader reader = null;
		try {
			if (ObjectUtil.isNotEmpty(uri)) {
				if ("jar".equals(uri.getScheme())) {
					reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
					StringBuffer buffer = new StringBuffer();
					for (String value=null; (value=reader.readLine()) != null; ) {
						buffer.append(value.concat("\r\n"));
					}
					webView.getEngine().loadContent(buffer.toString(), "text/plain");
				} else {
					webView.getEngine().load(uri.toString());
				}
			} else if (ObjectUtil.isNotEmpty(text)) {
				webView.getEngine().loadContent(text, "text/plain");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
