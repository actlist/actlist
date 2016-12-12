package org.silentsoft.actlist.plugin;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.silentsoft.actlist.plugin.ActlistPlugin.Function;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;

public class PluginComponent implements EventListener {

	@FXML
	private AnchorPane root;

	@FXML
	private JFXHamburger hand;
	
	@FXML
	private Label lblPluginName;
	
	@FXML
	private JFXToggleButton togActivator;
	
	@FXML
	private VBox contentBox;
	
	private String pluginFileName;
	
	private ActlistPlugin plugin;
	
	private ContextMenu contextMenu;
	
	public void initialize() {
		// This method is automatically called by FXMLLoader.
	}
	
	public void initialize(ActlistPlugin plugin, String pluginFileName, boolean activated) {
		EventHandler.addListener(this);
		
		this.plugin = plugin;
		
		this.pluginFileName = pluginFileName;
		String pluginName = plugin.getPluginName();
		String pluginDescription = plugin.getPlguinDescription();
		
		lblPluginName.setText(pluginName);
		if (pluginDescription != null && "".equals(pluginDescription) == false) {
			lblPluginName.setTooltip(new Tooltip(pluginDescription));
		}
		
		togActivator.setSelected(activated);
		
		if (activated) {
			activated();
		}
		
		contextMenu = new ContextMenu();
		for (Function function : plugin.getFunctionMap().values()) {
			MenuItem menuItem = new MenuItem("", function.graphic);
			menuItem.setOnAction((actionEvent) -> {
				try {
					if (function.action != null) {
						function.action.run();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			contextMenu.getItems().add(menuItem);
		}
	}
	
	private boolean loadPluginGraphic() {
		boolean result = true;
		try {
			/*
			<VBox fx:id="contentBox" layoutX="35.0" layoutY="50.0" prefWidth="380.0" AnchorPane.leftAnchor="35.0" AnchorPane.rightAnchor="20.0">
			   <children>
			      <BorderPane fx:id="contentPane" />
			      <Separator fx:id="contentLine" prefWidth="215.0">
			         <padding>
			            <Insets top="5.0" />
			         </padding>
			      </Separator>
			   </children>
			</VBox>
			*/
			
			contentBox.getChildren().clear();
			Node pluginContent = plugin.getGraphic();
			if (pluginContent != null) {
				contentBox.getChildren().add(new BorderPane(pluginContent));
				Separator contentLine = new Separator();
				contentLine.setPrefWidth(215.0);
				contentLine.setPadding(new Insets(5.0, 0.0, 0.0, 0.0));
				contentBox.getChildren().add(contentLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	@FXML
	private void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseButton.SECONDARY) {
			if (togActivator.selectedProperty().get()) {
				contextMenu.show(root, e.getScreenX(), e.getScreenY());
			}
		}
	}
	
	@FXML
	private void toggleOnAction() {
		// TODO I think need to remove this method (toogleOnAction) on fxml.
		if (togActivator.selectedProperty().get()) {
			activated();
		} else {
			deactivated();
		}
	}
	
	private void activated() {
		plugin.pluginActivated();
		loadPluginGraphic();
		
		List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get("DEACTIVATED_PLUGINS");
		deactivatedPlugins.remove(pluginFileName);
		EventHandler.callEvent(getClass(), "SAVE_DEACTIVATED_PLUGINS");
	}
	
	private void deactivated() {
		plugin.pluginDeactivated();
		contentBox.getChildren().clear();
		contextMenu.hide();
		
		List<String> deactivatedPlugins = (List<String>) SharedMemory.getDataMap().get("DEACTIVATED_PLUGINS");
		deactivatedPlugins.add(pluginFileName);
		EventHandler.callEvent(getClass(), "SAVE_DEACTIVATED_PLUGINS");
	}

	@Override
	public void onEvent(String event) {
		if (togActivator.selectedProperty().get()) {
			try {
				switch (event) {
				case "APPLICATION_ACTIVATED":
					plugin.applicationActivated();
					break;
				case "APPLICATION_DEACTIVATED":
					plugin.applicationDeactivated();
					break;
				}
			} catch (Exception e) {
				
			}
		}
	}
}
