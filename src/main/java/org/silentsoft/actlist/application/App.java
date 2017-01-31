package org.silentsoft.actlist.application;

import java.awt.Desktop;
import java.awt.Robot;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.ImageIcon;

import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.configuration.Configuration;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.JSONUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.hotkey.HotkeyHandler;
import org.silentsoft.ui.tray.TrayIconHandler;
import org.silentsoft.ui.util.StageUtil;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class App extends Application implements HotkeyListener, EventListener {

	private static Stage stage;
	
	private static Parent app;
	
	private AppController appController;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getStage() {
		Stage currentStage = StageUtil.getCurrentStage();
		if (currentStage == null) {
			return stage;
		}
		return currentStage;
	}
	
	static Parent getParent() {
		return app;
	}
	
	public static List<Image> getIcons() {
		return new Function<int[], List<Image>>() {
			@Override
			public List<Image> apply(int[] values) {
				ArrayList<Image> images = new ArrayList<Image>();
				for (int size : values) {
					images.add(new Image(String.join("", "/images/icon/actlist_", String.valueOf(size), CommonConst.EXTENSION_PNG)));
				}
				return images;
			}
		}.apply(new int[]{24, 32, 48, 64, 128, 256});
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		initialize();
		
		stage.setTitle(BizConst.APPLICATION_NAME);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(app, Color.TRANSPARENT));
		stage.setWidth(ConfigUtil.getStageWidth());
		stage.setHeight(ConfigUtil.getStageHeight());
		stage.setOpacity(ConfigUtil.getStageOpacity());
		stage.setAlwaysOnTop(ConfigUtil.isAlwaysOnTop());
		
		if (ConfigUtil.isAnimationEffect()) {
			AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
		}
		stage.show();
	}
	
	private void initialize() throws Exception {
		Platform.setImplicitExit(false);
		
		StageUtil.registerStage(stage);
		
		EventHandler.addListener(this);
		
		// WARNING : DO NOT MODIFY FUNCTION CALL PRIORITY
		loadConfiguration();
		initIntellitype();
		checkSingleInstance();
		displayStageIcon();
		registerTrayIcon();
		registerHotkey();
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().concat(CommonConst.EXTENSION_FXML)));
		app = fxmlLoader.load();
		appController = fxmlLoader.getController();
		Platform.runLater(() -> {
			appController.initialize();
		});
	}
	
	private void loadConfiguration() {
		try {
			ActlistConfig actlistConfig = null;
			
			File configFile = Paths.get(System.getProperty("user.dir"), "actlist.jar.config").toFile();
			if (configFile.exists()) {
				String configContent = FileUtil.readFile(configFile);
				actlistConfig = JSONUtil.JSONToObject(configContent, ActlistConfig.class);
			} else {
				actlistConfig = new ActlistConfig();
				actlistConfig.put("rootWidth", 380.0);
				actlistConfig.put("rootHeight", 230.0);
				actlistConfig.put("stageWidth", 390.0);  // left shadow(5) + root(380) + right shadow(5)
				actlistConfig.put("stageHeight", 240.0); // top shadow(5) + root(230) + bottom shadow(5)
				actlistConfig.put("stageOpacity", 1.0);
				actlistConfig.put("showHideActlistHotKeyModifier", JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT);
				actlistConfig.put("showHideActlistHotKeyCode", (int)'A');
				actlistConfig.put("animationEffect", true);
				actlistConfig.put("alwaysOnTop", false);
			}
			
			SharedMemory.getDataMap().put(BizConst.KEY_ACTLIST_CONFIG, actlistConfig);
		} catch (Exception e) {
			
		}
	}
	
	private void initIntellitype() {
		JIntellitype.setLibraryLocation(Paths.get(System.getProperty("user.dir"), "libs", String.join("", "JIntellitype-", SystemUtil.getOSArchitecture(), CommonConst.EXTENSION_DLL)).toString());
		
		if (JIntellitype.isJIntellitypeSupported() == false) {
			System.exit(1); // Abnormal termination.
		}
	}
	
	private void checkSingleInstance() {
		if (SystemUtil.findProcessByImageName(String.join("", BizConst.APPLICATION_NAME, CommonConst.EXTENSION_EXE), SystemUtil.getCurrentProcessId())) {
			// Fire hot key to showing up the already running process.
			try {
				Robot robot = new Robot();
				
				int keyCode = ConfigUtil.getShowHideActlistHotKeyCode();
				int modifier = ConfigUtil.getShowHideActlistHotKeyModifier();
				
				// Press Key
				if ((modifier & JIntellitype.MOD_CONTROL) == JIntellitype.MOD_CONTROL) {
					robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				}
				if ((modifier & JIntellitype.MOD_ALT) == JIntellitype.MOD_ALT) {
					robot.keyPress(java.awt.event.KeyEvent.VK_ALT);
				}
				if ((modifier & JIntellitype.MOD_SHIFT) == JIntellitype.MOD_SHIFT) {
					robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
				}
				if ((modifier & JIntellitype.MOD_WIN) == JIntellitype.MOD_WIN) {
					robot.keyPress(java.awt.event.KeyEvent.VK_WINDOWS);
				}
				robot.keyPress(keyCode);
				
				// Release Key
				if ((modifier & JIntellitype.MOD_CONTROL) == JIntellitype.MOD_CONTROL) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				}
				if ((modifier & JIntellitype.MOD_ALT) == JIntellitype.MOD_ALT) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_ALT);
				}
				if ((modifier & JIntellitype.MOD_SHIFT) == JIntellitype.MOD_SHIFT) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
				}
				if ((modifier & JIntellitype.MOD_WIN) == JIntellitype.MOD_WIN) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_WINDOWS);
				}
				robot.keyRelease(keyCode);
			} catch (Exception e) {
				
			}
			
			System.exit(0); // Just termination.
		}
	}
	
	private void displayStageIcon() {
		// taskbar
		stage.getIcons().addAll(getIcons());
	}
	
	private void registerTrayIcon() {
		// system tray
		TrayIconHandler.registerTrayIcon(new ImageIcon(getClass().getResource("/images/icon/actlist_16.png")).getImage(), BizConst.APPLICATION_NAME, actionEvent -> {
			showOrHide();
		});
		
		TrayIconHandler.addItem(String.join("", "Show/Hide ", "(", ConfigUtil.getShowHideActlistHotKeyText().replaceAll(" ", ""), ")"), actionEvent -> {
			showOrHide();
		});
		
		TrayIconHandler.addItem("Configuration", actionEvent -> {
			showConfiguration();
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Open silentsoft.org", actionEvent -> {
			try {
				Desktop.getDesktop().browse(new URI("http://silentsoft.org"));
			} catch (Exception e) {
				/**
				 * if open the box this time, then user may think 'WTF? I have browser!'
				 * don't open the error message box. sometimes need to silent.
				 */
			}
		});
		TrayIconHandler.addItem("About", actionEvent -> {
			Platform.runLater(() -> {
				StringBuffer message = new StringBuffer();
				// TODO need to change to use BuildVersion class file(by ant) instead hard-coding. but, I dont have time.
				message.append("Version  : 1.2.1\r\n");
				message.append("\r\n");
				message.append("Homepage : silentsoft.org\r\n");
				message.append("\r\n");
				message.append("3rd party library\r\n");
				message.append(" JIntellitype\r\n");
				message.append(" Jidefx-common\r\n");
				message.append(" JFoenix\r\n");
				message.append(" ControlsFx\r\n");
				message.append(" Jackson\r\n");
				message.append(" Apache-commons\r\n");
				message.append(" JNA\r\n");
				message.append("\r\n");
				message.append("Open Source License\r\n");
				// TODO specify open source licenses here.
				message.append(" Apache License 2.0\r\n");
				
				Alert alert = new Alert(AlertType.INFORMATION);
				((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(getIcons());
				alert.setTitle("About");
				alert.setHeaderText("Actlist");
				alert.setGraphic(new ImageView("/images/icon/actlist_48.png"));
				alert.setContentText(message.toString());
				alert.showAndWait();
			});
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Exit", actionEvent -> {
			exit();
		});
	}
	
	private void registerHotkey() {
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().registerHotKey(BizConst.HOTKEY_SHOW_HIDE_ACTLIST, ConfigUtil.getShowHideActlistHotKeyModifier(), ConfigUtil.getShowHideActlistHotKeyCode());
		
		HotkeyHandler.getInstance().registerHotkey(KeyCode.ESCAPE, false, false, false, () -> {
			showOrHide();
		});
		
		stage.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, HotkeyHandler.getInstance());
	}
	
	private void showOrHide() {
		Platform.runLater(() -> {
			if (stage.isIconified()) {
				stage.setIconified(false); // just bring it up to front from taskbar.
			} else {
				if (stage.isShowing()) {
					if (stage.isFocused()) {
						if (ConfigUtil.isAnimationEffect()) {
							Transition animation = AnimationUtils.createTransition(app, AnimationType.BOUNCE_OUT_DOWN);
			    			animation.setOnFinished(actionEvent -> {
			    				stage.hide();
			    			});
			    			animation.play();
						} else {
							stage.hide();
						}
					} else {
						stage.requestFocus(); // do not hide. just bring it up to front.
					}
				} else {
					if (ConfigUtil.isAnimationEffect()) {
						AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
					}
					stage.show();
				}
			}
		});
	}
	
	private Stage configurationStage;
	private void showConfiguration() {
		Platform.runLater(() -> {
			if (configurationStage == null) {
				configurationStage = new Stage();
				configurationStage.setTitle("Actlist Configuration");
				configurationStage.setScene(new Scene(new Configuration().getViewer()));
				configurationStage.setResizable(false);
				configurationStage.getIcons().addAll(new Function<int[], List<Image>>() {
					@Override
					public List<Image> apply(int[] values) {
						ArrayList<Image> images = new ArrayList<Image>();
						for (int size : values) {
							images.add(new Image(String.join("", "/images/icon/actlist_", String.valueOf(size), CommonConst.EXTENSION_PNG)));
						}
						return images;
					}
				}.apply(new int[]{24, 32, 48, 64, 128, 256}));
			}
			configurationStage.show();
		});
	}
	
	private void exit() {
		JIntellitype.getInstance().cleanUp();
		System.exit(0);
	}

	@Override
	public void onHotKey(int identifier) {
		switch (identifier) {
		case BizConst.HOTKEY_SHOW_HIDE_ACTLIST:
			showOrHide();
			break;
		}
	}

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_REGISTER_TRAY_ICON:
			registerTrayIcon();
			break;
		case BizConst.EVENT_APPLICATION_SHOW_HIDE:
			showOrHide();
			break;
		case BizConst.EVENT_APPLICATION_EXIT:
			exit();
			break;
		}
	}
	
}
