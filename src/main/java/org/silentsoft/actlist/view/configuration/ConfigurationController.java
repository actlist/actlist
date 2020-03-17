package org.silentsoft.actlist.view.configuration;

import java.awt.event.InputEvent;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.KeyStroke;

import org.apache.http.conn.util.InetAddressUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.ProxyMode;
import org.silentsoft.actlist.util.ConfigUtil.Theme;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.ui.viewer.AbstractViewerController;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;

public class ConfigurationController extends AbstractViewerController {

	@FXML
	private AnchorPane root;
	
	/* General */
	
	@FXML
	private JFXSlider stageOpacity;
	
	@FXML
	private JFXCheckBox alwaysOnTop;
	
	@FXML
	private JFXRadioButton winTheme, macTheme;
	
	@FXML
	private JFXRadioButton enableDarkMode, disableDarkMode;
	
	@FXML
	private JFXComboBox<String> loggingLevel;
	
	@FXML
	private JFXTextField showHideActlistHotKey;
	
	/* @FXML private JFXCheckBox animationEffect;*/
	
	private AtomicBoolean hotKeyMakingMode;
	
	/* Proxy */
	
	@FXML
	private JFXRadioButton noneProxy, automaticProxy, manualProxy;
	
	@FXML
	private JFXComboBox<String> proxyScheme;
	
	@FXML
	private JFXTextField proxyAddress, proxyPort;
	
	@FXML
	private JFXCheckBox manualProxyValidator;
	
	private AtomicBoolean isValidAddress;
	private AtomicBoolean isValidPort;
	
	public void initialize() {
		applyDarkMode();
	}
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		{
			/* General */
			
			stageOpacity.setValue(ConfigUtil.getStageOpacity() * 100);
			loggingLevel.setItems(FXCollections.observableArrayList("All", "Trace", "Debug", "Info", "Warn", "Error", "Off"));
			loggingLevel.getSelectionModel().select(ConfigUtil.getLoggingLevel());
			showHideActlistHotKey.setText(ConfigUtil.getShowHideActlistHotKeyText());
			/* animationEffect.setSelected(ConfigUtil.isAnimationEffect()); */
			alwaysOnTop.setSelected(ConfigUtil.isAlwaysOnTop());
			
			hotKeyMakingMode = new AtomicBoolean(false);
			
			String theme = ConfigUtil.getTheme();
			switch (theme) {
			case Theme.WIN:
				winTheme.setSelected(true);
				break;
			case Theme.MAC:
				macTheme.setSelected(true);
				break;
			}
			
			if (ConfigUtil.isDarkMode()) {
				enableDarkMode.setSelected(true);
			} else {
				disableDarkMode.setSelected(true);
			}
			
			stageOpacity.valueProperty().addListener((observable, oldValue, newValue) -> {
				App.getStage().setOpacity(newValue.doubleValue() / 100);
			});
			stageOpacity.setOnMouseReleased(mouseEvent -> {
				try {
					ConfigUtil.setStageOpacity(stageOpacity.getValue() / 100);
				} catch (Exception e) {
					
				}
			});
			stageOpacity.setOnKeyReleased(keyEvent -> {
				try {
					ConfigUtil.setStageOpacity(stageOpacity.getValue() / 100);
				} catch (Exception e) {
					
				}
			});
			loggingLevel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				try {
					if (newValue != null) {
						LogManager.getRootLogger().setLevel(Level.toLevel(newValue));
						
						ConfigUtil.setLoggingLevel(newValue);
					}
				} catch (Exception e) {
					
				}
			});
			showHideActlistHotKey.setOnKeyPressed(keyEvent -> {
				hotKeyMakingMode.set(true);
			});
			showHideActlistHotKey.setOnKeyReleased(keyEvent -> {
				try {
					if (hotKeyMakingMode.get() && (keyEvent.isControlDown() || keyEvent.isAltDown() || keyEvent.isShiftDown()) && keyEvent.getCode().getName().length() == 1) {
						int modifier = 0;
						String hotKeyText = "";
						if (keyEvent.isControlDown()) {
							modifier += InputEvent.CTRL_DOWN_MASK;
							hotKeyText = hotKeyText.concat("Ctrl + ");
						}
						if (keyEvent.isAltDown()) {
							modifier += InputEvent.ALT_DOWN_MASK;
							hotKeyText = hotKeyText.concat("Alt + ");
						}
						if (keyEvent.isShiftDown()) {
							modifier += InputEvent.SHIFT_DOWN_MASK;
							hotKeyText = hotKeyText.concat("Shift + ");
						}
						if (keyEvent.isMetaDown()) {
							modifier += InputEvent.META_DOWN_MASK;
							if (SystemUtil.isWindows()) {
								hotKeyText = hotKeyText.concat("Win + ");
							} else if (SystemUtil.isMac()) {
								hotKeyText = hotKeyText.concat("Cmd + ");
							} else {
								hotKeyText = hotKeyText.concat("Meta + ");
							}
						}
						
						hotKeyText = hotKeyText.concat(keyEvent.getCode().getName());
						
						ConfigUtil.setShowHideActlistHotKeyModifier(modifier);
						ConfigUtil.setShowHideActlistHotKeyCode(keyEvent.getCode().getName().charAt(0));
						
						App.getProvider().reset();
						App.getProvider().register(KeyStroke.getKeyStroke(keyEvent.getCode().getName().charAt(0), modifier), new HotKeyListener() {
							@Override
							public void onHotKey(HotKey arg0) {
								EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_SHOW_HIDE);
							}
						});
						
						showHideActlistHotKey.setText(hotKeyText);
						hotKeyMakingMode.set(false);
						
						EventHandler.callEvent(getClass(), BizConst.EVENT_REGISTER_TRAY_ICON);
					}
				} catch (Exception e) {
					
				}
			});
		}
		{
			/* Proxy */
			
			proxyScheme.setItems(FXCollections.observableArrayList("http", "https"));
			proxyScheme.getSelectionModel().select("http");
			proxyScheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				try {
					if (newValue != null) {
						/**
						 * Do not use isValidAddress property and isValidPort property. (for better look and feel at first time)
						 */
						if (proxyAddress.validate() && proxyPort.validate()) {
							saveProxy();
						}
					}
				} catch (Exception e) {
					
				}
			});
			
			isValidAddress = new AtomicBoolean(false);
			proxyAddress.getValidators().add(new ValidatorBase() {
				{
					setMessage("Invalid IPv4");
				}
				
				@Override
				protected void eval() {
					hasErrors.set(true);
					
					try {
						String text = ((TextInputControl) srcControl.get()).getText();
						hasErrors.set(!InetAddressUtils.isIPv4Address(text));
					} catch (Exception e) {
						
					}
				}
			});
			proxyAddress.focusedProperty().addListener((observable, oldValue, newValue) -> {
				try {
					if (!newValue) {
						isValidAddress.set(proxyAddress.validate());
						
						if (proxyPort.getText().isEmpty() == false) {
							isValidPort.set(proxyPort.validate());
						}
						
						if (isValidAddress.get() && isValidPort.get()) {
							saveProxy();
							manualProxyValidator.setSelected(true);
						} else {
							manualProxyValidator.setSelected(false);
						}
					}
				} catch (Exception e) {
					
				}
			});
			
			isValidPort = new AtomicBoolean(false);
			proxyPort.getValidators().add(new ValidatorBase() {
				{
					setMessage("Invalid");
				}
				
				@Override
				protected void eval() {
					hasErrors.set(true);
					
					try {
						String text = ((TextInputControl) srcControl.get()).getText();
						int port = Integer.parseInt(text);
						if (port >= 0 && port <= 65535) {
							hasErrors.set(false);
						}
					} catch (Exception e) {
						
					}
				}
			});
			proxyPort.focusedProperty().addListener((observable, oldValue, newValue) -> {
				try {
					if (!newValue) {
						isValidPort.set(proxyPort.validate());
						
						if (proxyAddress.getText().isEmpty() == false) {
							isValidAddress.set(proxyAddress.validate());
						}
						
						if (isValidAddress.get() && isValidPort.get()) {
							saveProxy();
							manualProxyValidator.setSelected(true);
						} else {
							manualProxyValidator.setSelected(false);
						}
					}
				} catch (Exception e) {
					
				}
			});
			
			manualProxyValidator.setSelected(false);
			
			String proxyMode = ConfigUtil.getProxyMode();
			switch (proxyMode) {
			case ProxyMode.NONE:
				noneProxy.setSelected(true);
				break;
			case ProxyMode.AUTOMATIC:
				automaticProxy.setSelected(true);
				break;
			case ProxyMode.MANUAL:
				manualProxy.setSelected(true);
				URI uri = URI.create(ConfigUtil.getProxyHost());
				proxyScheme.getSelectionModel().select(uri.getScheme());
				proxyAddress.setText(uri.getHost());
				proxyPort.setText(String.valueOf(uri.getPort()));
				break;
			}
			proxyScheme.setDisable(!ProxyMode.MANUAL.equals(proxyMode));
			proxyAddress.setDisable(!ProxyMode.MANUAL.equals(proxyMode));
			proxyPort.setDisable(!ProxyMode.MANUAL.equals(proxyMode));
		}
	}
	
	/*
	@FXML
	private void animationEffect() throws Exception {
		ConfigUtil.setAnimationEffect(animationEffect.selectedProperty().get());
	}
	*/
	
	@FXML
	private void alwaysOnTop() throws Exception {
		ConfigUtil.setAlwaysOnTop(alwaysOnTop.selectedProperty().get());
		App.getStage().setAlwaysOnTop(alwaysOnTop.selectedProperty().get());
	}
	
	@FXML
	private void theme() throws Exception {
		if (winTheme.selectedProperty().get()) {
			ConfigUtil.setTheme(Theme.WIN);
		} else if (macTheme.selectedProperty().get()) {
			ConfigUtil.setTheme(Theme.MAC);
		}
		
		EventHandler.callEvent(getClass(), BizConst.EVENT_APPLY_THEME, false);
	}
	
	@FXML
	private void darkMode() throws Exception {
		ConfigUtil.setDarkMode(enableDarkMode.selectedProperty().get());
		
		applyDarkMode();
		
		EventHandler.callEvent(getClass(), BizConst.EVENT_APPLY_DARK_MODE, false);
	}
	
	private void applyDarkMode() {
		String css = getClass().getResource("Configuration-darkmode.css").toExternalForm();
    	
    	if (ConfigUtil.isDarkMode()) {
    		if (root.getStylesheets().contains(css) == false) {
    			root.getStylesheets().add(css);
    		}
    	} else {
    		root.getStylesheets().remove(css);
    	}
	}
	
	@FXML
	private void proxy() throws Exception {
		boolean isSelectedManualProxy = manualProxy.selectedProperty().get();
		if (isSelectedManualProxy == false) {
			proxyAddress.setText("");
			proxyPort.setText("");
			isValidAddress.set(false);
			isValidPort.set(false);
			manualProxyValidator.setSelected(false);
		}
		
		proxyScheme.setDisable(!isSelectedManualProxy);
		proxyAddress.setDisable(!isSelectedManualProxy);
		proxyPort.setDisable(!isSelectedManualProxy);
		
		if (noneProxy.selectedProperty().get() || automaticProxy.selectedProperty().get()) {
			saveProxy();
		} else {
			// Do not save yet.
		}
	}
	
	private void saveProxy() throws Exception {
		if (noneProxy.selectedProperty().get()) {
			ConfigUtil.setProxyMode(ProxyMode.NONE);
			ConfigUtil.setProxyHost("");
		} else if (automaticProxy.selectedProperty().get()) {
			ConfigUtil.setProxyMode(ProxyMode.AUTOMATIC);
			ConfigUtil.setProxyHost("");
		} else if (manualProxy.selectedProperty().get()) {
			ConfigUtil.setProxyMode(ProxyMode.MANUAL);
			ConfigUtil.setProxyHost(String.join("", proxyScheme.getSelectionModel().getSelectedItem(), "://", proxyAddress.getText(), ":", proxyPort.getText()));
		}
		
		EventHandler.callEvent(getClass(), BizConst.EVENT_UPDATE_PROXY_HOST);
	}
	
}
