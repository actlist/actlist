package org.silentsoft.actlist.configuration;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.fxml.FXML;
import javafx.scene.Parent;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.ui.viewer.AbstractViewerController;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.melloware.jintellitype.JIntellitype;

public class ConfigurationController extends AbstractViewerController {

	@FXML
	private JFXSlider stageOpacity;
	
	@FXML
	private JFXTextField showHideActlistHotKey;
	
	@FXML
	private JFXCheckBox animationEffect;
	
	@FXML
	private JFXCheckBox alwaysOnTop;
	
	private AtomicBoolean hotKeyMakingMode;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		stageOpacity.setValue(ConfigUtil.getStageOpacity() * 100);
		showHideActlistHotKey.setText(ConfigUtil.getShowHideActlistHotKeyText());
		animationEffect.setSelected(ConfigUtil.isAnimationEffect());
		alwaysOnTop.setSelected(ConfigUtil.isAlwaysOnTop());
		
		hotKeyMakingMode = new AtomicBoolean(false);
		
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
		showHideActlistHotKey.setOnKeyPressed(keyEvent -> {
			hotKeyMakingMode.set(true);
		});
		showHideActlistHotKey.setOnKeyReleased(keyEvent -> {
			try {
				if (hotKeyMakingMode.get() && (keyEvent.isControlDown() || keyEvent.isAltDown() || keyEvent.isShiftDown()) && keyEvent.getCode().getName().length() == 1) {
					int modifier = 0;
					String hotKeyText = "";
					if (keyEvent.isControlDown()) {
						modifier += JIntellitype.MOD_CONTROL;
						hotKeyText = hotKeyText.concat("Ctrl + ");
					}
					if (keyEvent.isAltDown()) {
						modifier += JIntellitype.MOD_ALT;
						hotKeyText = hotKeyText.concat("Alt + ");
					}
					if (keyEvent.isShiftDown()) {
						modifier += JIntellitype.MOD_SHIFT;
						hotKeyText = hotKeyText.concat("Shift + ");
					}
					hotKeyText = hotKeyText.concat(keyEvent.getCode().getName());
					
					ConfigUtil.setShowHideActlistHotKeyModifier(modifier);
					ConfigUtil.setShowHideActlistHotKeyCode(keyEvent.getCode().getName().charAt(0));
					
					JIntellitype.getInstance().unregisterHotKey(BizConst.HOTKEY_SHOW_HIDE_ACTLIST);
					JIntellitype.getInstance().registerHotKey(BizConst.HOTKEY_SHOW_HIDE_ACTLIST, modifier, keyEvent.getCode().getName().charAt(0));
					
					showHideActlistHotKey.setText(hotKeyText);
					hotKeyMakingMode.set(false);
					
					EventHandler.callEvent(getClass(), BizConst.EVENT_REGISTER_TRAY_ICON);
				}
			} catch (Exception e) {
				
			}
		});
	}
	
	@FXML
	private void animationEffect() throws Exception {
		ConfigUtil.setAnimationEffect(animationEffect.selectedProperty().get());
	}
	
	@FXML
	private void alwaysOnTop() throws Exception {
		ConfigUtil.setAlwaysOnTop(alwaysOnTop.selectedProperty().get());
		App.getStage().setAlwaysOnTop(alwaysOnTop.selectedProperty().get());
	}
	
}
