package org.silentsoft.actlist.application;

import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.configuration.Configuration;
import org.silentsoft.actlist.console.Console;
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

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

public class App extends Application implements EventListener {

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
	
	private static Provider provider;
	public static Provider getProvider() {
		if (provider == null) {
			provider = Provider.getCurrentProvider(false);
		}
		
		return provider;
	}
	
	private Alert getAboutAlert() {
		StringBuffer message = new StringBuffer();
		// TODO need to change to use BuildVersion class file(by ant) instead hard-coding. but, I dont have time.
		message.append("Version  : 1.2.3\r\n");
		message.append("\r\n");
		message.append("Homepage : silentsoft.org\r\n");
		message.append("\r\n");
		message.append("3rd party library\r\n");
		message.append(" Apache-commons\r\n");
		message.append(" JKeyMaster\r\n");
		message.append(" Jidefx-common\r\n");
		message.append(" JFoenix\r\n");
		message.append(" ControlsFx\r\n");
		message.append(" Centerdevice-nsmenufx\r\n");
		message.append(" Jackson\r\n");
		message.append(" Json\r\n");
		message.append(" JNA\r\n");
		message.append(" Junit\r\n");
		message.append(" Log4j\r\n");
		message.append("\r\n");
		message.append("Open Source License\r\n");
		// TODO specify open source licenses here.
		message.append(" Apache License 2.0\r\n");
		
		Alert aboutAlert = new Alert(AlertType.INFORMATION);
		((Stage) aboutAlert.getDialogPane().getScene().getWindow()).getIcons().addAll(getIcons());
		aboutAlert.setTitle("About");
		aboutAlert.setHeaderText("Actlist");
		aboutAlert.setGraphic(new ImageView("/images/icon/actlist_48.png"));
		aboutAlert.setContentText(message.toString());
		
		return aboutAlert;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		initialize();
		
		stage.setTitle(BizConst.APPLICATION_NAME);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(createScene());
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

		checkSingleInstance();
		
		initConsole();
		displayStageIcon();
		registerTrayIcon();
		registerHotkey();
		registerMenu();
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().concat(CommonConst.EXTENSION_FXML)));
		app = fxmlLoader.load();
		appController = fxmlLoader.getController();
		Platform.runLater(() -> {
			appController.initialize();
		});
	}
	
	private Scene createScene() {
		Scene scene = null;
		
		if (SystemUtil.isMac()) {
			BorderPane root = new BorderPane();
			root.setStyle("-fx-background-color: transparent;");
			root.setTop(createMenuBar());
			root.setCenter(app);
			
			scene = new Scene(root, Color.TRANSPARENT);
		}
		
		return (scene == null) ? (new Scene(app, Color.TRANSPARENT)) : scene;
	}
	
	private MenuBar createMenuBar() {
		return null;
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
				actlistConfig.put("showHideActlistHotKeyModifier", InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK);
				actlistConfig.put("showHideActlistHotKeyCode", (int)'A');
				actlistConfig.put("animationEffect", true);
				actlistConfig.put("alwaysOnTop", false);
			}
			
			SharedMemory.getDataMap().put(BizConst.KEY_ACTLIST_CONFIG, actlistConfig);
		} catch (Exception e) {
			
		}
	}
	
	private void checkSingleInstance() {
		String imageName = BizConst.APPLICATION_NAME;
		if (SystemUtil.isWindows()) {
			imageName = imageName + CommonConst.EXTENSION_EXE;
		}
		
		if (SystemUtil.findProcessByImageName(imageName, SystemUtil.getCurrentProcessId())) {
			// Fire hot key to showing up the already running process.
			try {
				Robot robot = new Robot();
				
				int keyCode = ConfigUtil.getShowHideActlistHotKeyCode();
				int modifier = ConfigUtil.getShowHideActlistHotKeyModifier();
				
				// Press Key
				if ((modifier & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
					robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				}
				if ((modifier & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK) {
					robot.keyPress(java.awt.event.KeyEvent.VK_ALT);
				}
				if ((modifier & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
					robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
				}
				if ((modifier & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK) {
					robot.keyPress(java.awt.event.KeyEvent.VK_WINDOWS);
				}
				robot.keyPress(keyCode);
				
				// Release Key
				if ((modifier & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				}
				if ((modifier & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_ALT);
				}
				if ((modifier & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
				}
				if ((modifier & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK) {
					robot.keyRelease(java.awt.event.KeyEvent.VK_WINDOWS);
				}
				robot.keyRelease(keyCode);
			} catch (Exception e) {
				
			}
			
			System.exit(0); // Just termination.
		}
	}
	
	private Stage consoleStage;
	private void initConsole() throws Exception {
		if (consoleStage == null) {
			consoleStage = new Stage();
			consoleStage.setTitle("Console");
			consoleStage.initStyle(StageStyle.TRANSPARENT);
			{
				FXMLLoader fxmlLoader = new FXMLLoader(Console.class.getResource(Console.class.getSimpleName().concat(CommonConst.EXTENSION_FXML)));
				Parent app = fxmlLoader.load();
				Console console = fxmlLoader.getController();
				console.initialize(consoleStage);
				
				System.setOut(console.getPrintStream());
				System.setErr(console.getPrintStream());
				
				BorderPane root = new BorderPane();
				root.setStyle("-fx-background-color: transparent;");
				root.setTop(createMenuBar());
				root.setCenter(app);
				
				consoleStage.setScene(new Scene(root, Color.TRANSPARENT));
			}
			consoleStage.setWidth(400.0);
			consoleStage.setHeight(500.0);
			consoleStage.getIcons().addAll(getIcons());
		}
	}
	
	private void displayStageIcon() {
		// taskbar
		stage.getIcons().addAll(getIcons());
	}
	
	private void registerTrayIcon(){
		// system tray
		if (SystemUtil.isMac()) {
			TrayIconHandler.registerTrayIcon(new ImageIcon(getClass().getResource("/images/icon/actlist_64.png")).getImage(), BizConst.APPLICATION_NAME, actionEvent -> {
				showOrHide();
			});
		} else {
			TrayIconHandler.registerTrayIcon(new ImageIcon(getClass().getResource("/images/icon/actlist_16.png")).getImage(), BizConst.APPLICATION_NAME, actionEvent -> {
				showOrHide();
			});
		}
		
		TrayIconHandler.addItem(String.join("", "Show/Hide ", "(", ConfigUtil.getShowHideActlistHotKeyText().replaceAll(" ", ""), ")"), actionEvent -> {
			showOrHide();
		});
		
		TrayIconHandler.addItem("Console", actionEvent -> {
			showConsole();
		});
		
		TrayIconHandler.addItem("Configuration", actionEvent -> {
			showConfiguration();
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Open plugins directory", actionEvent -> {
			try {
				File pluginsDirectory = Paths.get(System.getProperty("user.dir"), "plugins").toFile();
				if (pluginsDirectory.exists() == false) {
					pluginsDirectory.mkdirs();
				}
				Desktop.getDesktop().open(pluginsDirectory);
			} catch (Exception e) {
				
			}
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Browse silentsoft.org", actionEvent -> {
			try {
				Desktop.getDesktop().browse(new URI("http://silentsoft.org"));
			} catch (Exception e) {
				/**
				 * if open the box this time, then user may think 'WTF? I have a browser!'
				 * don't open the error message box. sometimes need to silent.
				 */
			}
		});
		TrayIconHandler.addItem("About", actionEvent -> {
			Platform.runLater(() -> {
				getAboutAlert().showAndWait();
			});
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Exit", actionEvent -> {
			exit();
		});
	}
	
	private void registerHotkey() throws Exception {
		getProvider().register(KeyStroke.getKeyStroke(ConfigUtil.getShowHideActlistHotKeyCode(), ConfigUtil.getShowHideActlistHotKeyModifier()), new HotKeyListener() {
			@Override
			public void onHotKey(HotKey arg0) {
				showOrHide();
			}
		});
		
		HotkeyHandler.getInstance().registerHotkey(KeyCode.ESCAPE, false, false, false, () -> {
			showOrHide();
		});
		
		stage.addEventHandler(KeyEvent.KEY_RELEASED, HotkeyHandler.getInstance());
	}
	
	private void registerMenu() {
		if (SystemUtil.isMac()) {
			String appName = "Actlist";
			MenuToolkit menuToolkit = MenuToolkit.toolkit();
			
			MenuItem aboutMenuItem = new MenuItem("About " + appName);
			aboutMenuItem.setOnAction(actionEvent -> {
				getAboutAlert().showAndWait();
			});
			
			MenuItem preferencesMenuItem = new MenuItem("Preferences");
			preferencesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN));
			preferencesMenuItem.setOnAction(actionEvent -> {
				showConfiguration();
			});
			
			Menu menu = new Menu(appName, null, 
						aboutMenuItem,
						new SeparatorMenuItem(),
						preferencesMenuItem,
						new SeparatorMenuItem(),
						menuToolkit.createHideMenuItem(appName),
						menuToolkit.createHideOthersMenuItem(),
						menuToolkit.createUnhideAllMenuItem(),
						new SeparatorMenuItem(),
						menuToolkit.createQuitMenuItem(appName));
			menuToolkit.setApplicationMenu(menu);
		}
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
	
	private void showConsole() {
		Platform.runLater(() -> {
			consoleStage.show();
		});
	}
	
	private Stage configurationStage;
	private void showConfiguration() {
		Platform.runLater(() -> {
			if (configurationStage == null) {
				configurationStage = new Stage();
				configurationStage.setTitle("Actlist Configuration");
				{
					BorderPane scene = new BorderPane();
					scene.setTop(createMenuBar());
					scene.setCenter(new Configuration().getViewer());
					
					configurationStage.setScene(new Scene(scene));
				}
				configurationStage.setResizable(false);
				configurationStage.getIcons().addAll(getIcons());
			}
			configurationStage.show();
		});
	}
	
	private void exit() {
		getProvider().reset();
		getProvider().stop();
		
		System.exit(0);
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
