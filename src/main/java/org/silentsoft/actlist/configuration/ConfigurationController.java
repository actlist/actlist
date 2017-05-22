package org.silentsoft.actlist.configuration;

import java.awt.event.InputEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.KeyStroke;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.application.App;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.ui.viewer.AbstractViewerController;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import javafx.fxml.FXML;
import javafx.scene.Parent;

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
