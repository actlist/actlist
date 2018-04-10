package org.silentsoft.actlist.plugin.about;

import java.awt.Desktop;

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
					/**
					 * TODO : plugin update check
					 */
					VBox parent = (VBox) newVersionBox.getParent();
					parent.getChildren().remove(newVersionBox);
				}
				
				try {
					if (ObjectUtil.isNotEmpty(plugin.getPluginDescriptionURI())) {
						descriptionView.getEngine().load(plugin.getPluginDescriptionURI().toString());
					} else if (ObjectUtil.isNotEmpty(plugin.getPluginDescription())) {
						descriptionView.getEngine().loadContent(plugin.getPluginDescription(), "text/plain");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (ObjectUtil.isNotEmpty(plugin.getPluginChangeLogURI())) {
						changeLogView.getEngine().load(plugin.getPluginChangeLogURI().toString());
					} else if (ObjectUtil.isNotEmpty(plugin.getPluginChangeLog())) {
						changeLogView.getEngine().loadContent(plugin.getPluginChangeLog(), "text/plain");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (ObjectUtil.isNotEmpty(plugin.getPluginLicenseURI())) {
						licenseView.getEngine().load(plugin.getPluginLicenseURI().toString());
					} else if (ObjectUtil.isNotEmpty(plugin.getPluginLicense())) {
						licenseView.getEngine().loadContent(plugin.getPluginLicense(), "text/plain");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
