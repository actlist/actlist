package org.silentsoft.actlist.plugin.about;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.silentsoft.actlist.plugin.ActlistPlugin;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.ui.viewer.AbstractViewerController;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class PluginAboutController extends AbstractViewerController {

	@FXML
	private VBox rootVBox;
	
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
					Object _isAvailableNewPlugin = parameters[1];
					Object _newPluginURI = parameters[2];
					
					if (_isAvailableNewPlugin instanceof Boolean) {
						boolean isAvailableNewPlugin = (boolean) _isAvailableNewPlugin;
						if (isAvailableNewPlugin && _newPluginURI != null && _newPluginURI instanceof URI) {
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
						} else {
							newVersionBox.setVisible(false);
							
							VBox parent = (VBox) newVersionBox.getParent();
							parent.getChildren().remove(newVersionBox);
						}
					}
				}
				
				if (haveContentToShow(plugin)) {
					TabPane tabPane = createContentTabPane();
					makeTabIfContentExists(tabPane, "Description", plugin.getPluginDescriptionURI(), plugin.getPluginDescription());
					makeTabIfContentExists(tabPane, "Change Log", plugin.getPluginChangeLogURI(), plugin.getPluginChangeLog());
					makeTabIfContentExists(tabPane, "License", plugin.getPluginLicenseURI(), plugin.getPluginLicense());
					viewer.getScene().getWindow().sizeToScene();
				}
			}
		}
	}
	
	private boolean haveContentToShow(ActlistPlugin plugin) {
		boolean result = false;
		if (ObjectUtil.isNotEmpty(plugin.getPluginDescriptionURI()) || ObjectUtil.isNotEmpty(plugin.getPluginDescription())) {
			result = true;
		} else if (ObjectUtil.isNotEmpty(plugin.getPluginChangeLogURI()) || ObjectUtil.isNotEmpty(plugin.getPluginChangeLog())) {
			result = true;
		} else if (ObjectUtil.isNotEmpty(plugin.getPluginLicenseURI()) || ObjectUtil.isNotEmpty(plugin.getPluginLicense())) {
			result = true;
		}
		return result;
	}
	
	private TabPane createContentTabPane() {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		ScrollPane scrollPane = new ScrollPane(tabPane);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setPrefWidth(330.0);
		scrollPane.setPrefHeight(310.0);
		
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		
		rootVBox.getChildren().add(scrollPane);
		
		return tabPane;
	}
	
	private void makeTabIfContentExists(TabPane tabPane, String title, URI uri, String text) {
		boolean existsContent = ObjectUtil.isNotEmpty(uri) || ObjectUtil.isNotEmpty(text);
		if (existsContent) {
			WebView webView = new WebView();
			
			if (ObjectUtil.isNotEmpty(uri)) {
				if ("jar".equals(uri.getScheme())) {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8))) {
						StringBuffer buffer = new StringBuffer();
						for (String value=null; (value=reader.readLine()) != null; ) {
							buffer.append(value.concat("\r\n"));
						}
						String content = HtmlRenderer.builder().build().render(Parser.builder().build().parse(buffer.toString()));
						webView.getEngine().loadContent(content);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					webView.getEngine().load(uri.toString());
				}
			} else if (ObjectUtil.isNotEmpty(text)) {
				String content = HtmlRenderer.builder().build().render(Parser.builder().build().parse(text));
				webView.getEngine().loadContent(content);
			}
			
			tabPane.getTabs().add(new Tab(title, webView));
		}
	}

}
