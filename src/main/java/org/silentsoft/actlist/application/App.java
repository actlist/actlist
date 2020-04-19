package org.silentsoft.actlist.application;

import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.apache.http.HttpHost;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.console.Console;
import org.silentsoft.actlist.rest.RESTfulAPI;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.ProxyMode;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.JSONUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.hotkey.HotkeyHandler;
import org.silentsoft.ui.tray.TrayIconHandler;
import org.silentsoft.ui.util.StageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import de.codecentric.centerdevice.MenuToolkit;
import de.codecentric.centerdevice.glass.AdapterContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private static Stage stage;
	
	private static Parent app;
	
	private AppController appController;
	
	public static void main(String[] args) throws Exception {
		loadConfiguration();
		
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
	
	private static void loadConfiguration() throws Exception {
		ActlistConfig actlistConfig = null;
		
		File configFile = Paths.get(System.getProperty("user.dir"), "actlist.jar.config").toFile();
		if (configFile.exists()) {
			String configContent = FileUtil.readFile(configFile);
			actlistConfig = JSONUtil.JSONToObject(configContent, ActlistConfig.class);
		} else {
			actlistConfig = new ActlistConfig();
			actlistConfig.put("rootWidth", 506.0);
			actlistConfig.put("rootHeight", 443.0);
			actlistConfig.put("stageWidth", 516.0);  // left shadow(5) + root(506) + right shadow(5)
			actlistConfig.put("stageHeight", 453.0); // top shadow(5) + root(443) + bottom shadow(5)
			actlistConfig.put("stageOpacity", 1.0);
			actlistConfig.put("loggingLevel", "Info");
			actlistConfig.put("showHideActlistHotKeyModifier", InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK);
			actlistConfig.put("showHideActlistHotKeyCode", (int)'A');
//			actlistConfig.put("animationEffect", true);
			actlistConfig.put("alwaysOnTop", false);
			actlistConfig.put("darkMode", false);
			actlistConfig.put("proxyMode", ProxyMode.AUTOMATIC);
			actlistConfig.put("proxyHost", "");
		}
		
		SharedMemory.getDataMap().put(BizConst.KEY_ACTLIST_CONFIG, actlistConfig);
		
		updateLoggingLevel();
	}
	
	private static void updateLoggingLevel() {
		LogManager.getRootLogger().setLevel(Level.toLevel(ConfigUtil.getLoggingLevel()));
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		new Thread(() -> {
			try {
				notifyPreloader(new Preloader.ProgressNotification(0.2));
				heavyLifting();
				
				notifyPreloader(new Preloader.ProgressNotification(0.3));
				initializeWithoutFxThread();
				
				notifyPreloader(new Preloader.ProgressNotification(0.4));
				Platform.runLater(() -> {
					try {
						initializeWithFxThread();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private void heavyLifting() {
		generateUserAgentAndInfoText();
		updateProxyHost();
	}
	
	private void generateUserAgentAndInfoText() {
		String osArchitecture = SystemUtil.getOSArchitecture();
		String platformArchitecture = SystemUtil.getPlatformArchitecture();
		
		StringBuffer userAgent = new StringBuffer();
		{
			userAgent.append("Actlist-");
			
			userAgent.append(BuildVersion.VERSION);
			
			if (SystemUtil.isWindows()) {
				userAgent.append(" windows-");
			} else if (SystemUtil.isMac()) {
				userAgent.append(" macosx-");
			} else if (SystemUtil.isLinux()) {
				userAgent.append(" linux-");
			} else {
				userAgent.append(" unknown-");
			}
			userAgent.append(osArchitecture);
			
			userAgent.append(" platform-");
			userAgent.append(platformArchitecture);
		}
		SharedMemory.getDataMap().put(BizConst.KEY_USER_AGENT, userAgent.toString());
		
		StringBuffer infoText = new StringBuffer();
		{
			infoText.append(String.format("Actlist %s (%s %s, platform %s)", BuildVersion.VERSION, SystemUtil.getOSName(), osArchitecture, platformArchitecture));
			infoText.append("\r\n");
			infoText.append(String.format("%s, %s", System.getProperty("java.vm.name"), System.getProperty("java.runtime.version")));
		}
		SharedMemory.getDataMap().put(BizConst.KEY_INFO_TEXT, infoText.toString());
		
		welcome();
	}
	
	private void welcome() {
		String text = String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_INFO_TEXT));
		for (String message : text.split("\r\n")) {
			LOGGER.info(message);
		}
	}
	
	private void updateProxyHost() {
		String proxyHost = "";
		String proxyPort = "";
		
		HttpHost proxy = RESTfulAPI.getProxyHost();
		if (proxy != null) {
			proxyHost = proxy.getHostName();
			proxyPort = String.valueOf(proxy.getPort());
		}
		
		System.setProperty("http.proxyHost", proxyHost);
		System.setProperty("http.proxyPort", proxyPort);
		
		System.setProperty("https.proxyHost", proxyHost);
		System.setProperty("https.proxyPort", proxyPort);
	}
	
	private void initializeWithoutFxThread() throws Exception {
		Platform.setImplicitExit(false);
		
		StageUtil.registerStage(stage);
		
		EventHandler.addListener(this);
		
		
		displayStageIcon();
		registerTrayIcon();
		registerHotkey();
	}
	
	private void initializeWithFxThread() throws Exception {
		initConsole();
		registerMenu();
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().concat(CommonConst.EXTENSION_FXML)));
		app = fxmlLoader.load();
		appController = fxmlLoader.getController();
		appController.initialize();
		
		stage.setTitle(BizConst.APPLICATION_NAME);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(createScene());
		stage.setWidth(ConfigUtil.getStageWidth());
		stage.setHeight(ConfigUtil.getStageHeight());
		stage.setOpacity(ConfigUtil.getStageOpacity());
		stage.setAlwaysOnTop(ConfigUtil.isAlwaysOnTop());
		stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
			appController.notifyFocusState(newValue);
		});
		
//		if (ConfigUtil.isAnimationEffect()) {
//			AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
//		}
		
//		stage.show();
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
	
	private Stage consoleStage;
	private void initConsole() throws Exception {
		if (consoleStage == null) {
			consoleStage = new Stage();
			consoleStage.setTitle("Console");
			consoleStage.initOwner(App.getStage());
			consoleStage.initStyle(StageStyle.TRANSPARENT);
			{
				FXMLLoader fxmlLoader = new FXMLLoader(Console.class.getResource(Console.class.getSimpleName().concat(CommonConst.EXTENSION_FXML)));
				Parent app = fxmlLoader.load();
				Console console = fxmlLoader.getController();
				console.initialize(consoleStage);
				
				consoleStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
					console.notifyFocusState(newValue);
				});
				
				System.setOut(console.getPrintStream());
				System.setErr(console.getPrintStream());
				
				System.out.println(String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_INFO_TEXT)));
				
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
		
		TrayIconHandler.addItem("Explore Other Plugins", actionEvent -> {
			showExplore();
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Open Plugins Directory", actionEvent -> {
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
		
		TrayIconHandler.addItem("Browse Homepage", actionEvent -> {
			try {
				Desktop.getDesktop().browse(new URI("http://actlist.silentsoft.org"));
			} catch (Exception e) {
				
			}
		});
		TrayIconHandler.addItem("Feedback", actionEvent -> {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/silentsoft/actlist/issues"));
			} catch (Exception e) {
				
			}
		});
		TrayIconHandler.addItem("About", actionEvent -> {
			showAbout();
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
				showAbout();
			});
			
			MenuItem preferencesMenuItem = new MenuItem("Preferences");
			preferencesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN));
			preferencesMenuItem.setOnAction(actionEvent -> {
				showConfiguration();
			});
			
			Menu appMenu = new Menu(appName, null, 
							   aboutMenuItem,
							   new SeparatorMenuItem(),
							   preferencesMenuItem,
							   new SeparatorMenuItem(),
							   menuToolkit.createHideMenuItem(appName),
							   menuToolkit.createHideOthersMenuItem(),
							   menuToolkit.createUnhideAllMenuItem(),
							   new SeparatorMenuItem(),
							   menuToolkit.createQuitMenuItem(appName));
			menuToolkit.setApplicationMenu(appMenu);
			
			// File Menu
			Menu fileMenu = new Menu("File");
			fileMenu.getItems().addAll(menuToolkit.createCloseWindowMenuItem());

			// Edit
			Menu editMenu = new Menu("Edit");
			// View Menu
			Menu viewMenu = new Menu("View");

			// Window Menu
			Menu windowMenu = new Menu("Window", null,
								  menuToolkit.createMinimizeMenuItem(),
								  menuToolkit.createZoomMenuItem(),
								  menuToolkit.createCycleWindowsItem(),
								  new SeparatorMenuItem(),
								  menuToolkit.createBringAllToFrontItem());
			menuToolkit.autoAddWindowMenuItems(windowMenu);
			
			// Help Menu
			Menu helpMenu = new Menu("Help");
			
			menuToolkit.setGlobalMenuBar(new MenuBar(appMenu, fileMenu, editMenu, viewMenu, windowMenu, helpMenu));
		}
	}
	
	private void showOrHide() {
		if (stage.isIconified()) {
			Platform.runLater(() -> { stage.setIconified(false); }); // just bring it up to front from taskbar.
		} else {
			if (stage.isShowing()) {
				if (stage.isFocused()) {
//					if (ConfigUtil.isAnimationEffect()) {
//						Transition animation = AnimationUtils.createTransition(app, AnimationType.BOUNCE_OUT_DOWN);
//		    			animation.setOnFinished(actionEvent -> {
//		    				stage.hide();
//		    			});
//		    			animation.play();
//					} else {
					if (SystemUtil.isMac()) {
						AdapterContext.getContext().getApplicationAdapter().hide();
					} else {
						Platform.runLater(() -> { stage.hide(); });
					}
//					}
				} else {
					if (SystemUtil.isMac()) {
						Platform.runLater(() -> {
							stage.toFront();

							/* WHAT THE HECK .. ! BUT THIS IS NECESSARY BECAUSE OF stage.requestFocus() DOES NOT WORKS PROPERLY ON MAC */
							{
								Point previousMouseLocation = MouseInfo.getPointerInfo().getLocation();
								new Thread(() -> {
									Platform.runLater(() -> {
										try {
											Robot robot = new Robot();
											robot.mouseMove((int) stage.getX()+10, (int) stage.getY()+10);
											robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
											robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
											
											robot.mouseMove(previousMouseLocation.x, previousMouseLocation.y);
										} catch (Exception e) {
											
										}
									});
								}).start();
							}
						});
					} else {
						Platform.runLater(() -> { stage.requestFocus(); }); // do not hide. just bring it up to front.
					}
				}
			} else {
//				if (ConfigUtil.isAnimationEffect()) {
//					AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
//				}
				if (SystemUtil.isMac()) {
					AdapterContext.getContext().getApplicationAdapter().unhideAllApplications();
				} else {
					Platform.runLater(() -> { stage.show(); });
				}
			}
		}
	}
	
	private void bringToFront() {
		if (isHidden()) {
			showOrHide(); // in this case the Actlist will definitely showing up.
		}
	}
	
	public static boolean isShown() {
		return !isHidden();
	}
	
	public static boolean isHidden() {
		if (stage.isIconified() ||
			stage.isShowing() == false ||
		    (stage.isShowing() == true && stage.isFocused() == false)) {
			return true;
		}
		
		return false;
	}
	
	private void showExplore() {
		Platform.runLater(() -> {
			bringToFront();
			EventHandler.callEvent(getClass(), BizConst.EVENT_SHOW_EXPLORE_VIEW, false);
		});
	}
	
	private void showAbout() {
		Platform.runLater(() -> {
			bringToFront();
			EventHandler.callEvent(getClass(), BizConst.EVENT_SHOW_ABOUT_VIEW, false);
		});
	}
	
	private void showConsole() {
		Platform.runLater(() -> {
			consoleStage.show();
		});
	}
	
	private void showConfiguration() {
		Platform.runLater(() -> {
			bringToFront();
			EventHandler.callEvent(getClass(), BizConst.EVENT_SHOW_CONFIGURATION_VIEW, false);
		});
	}
	
	private void exit() {
		getProvider().reset();
		getProvider().stop();
		
		EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_CLOSE_REQUESTED, false);
		System.exit(0);
	}

	private CountDownLatch pluginCountDownLatch;
	private void notifyPreloaderPreparingPlugins() {
		pluginCountDownLatch = new CountDownLatch((int) SharedMemory.getDataMap().get(BizConst.KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS));
		notifyPreloader(new Preloader.ProgressNotification(0.5));
		new Thread(() -> {
			try {
				pluginCountDownLatch.await();
				
				Thread.sleep(300);
				notifyPreloader(new Preloader.ProgressNotification(1.0));
				Thread.sleep(1200);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				notifyPreloader(new Preloader.StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
				Platform.runLater(() -> {
					stage.show();
				});
			}
		}).start();
	}
	
	private void notifyPreloaderCountDownPlugin() {
		int numberOfPlugins = (int) SharedMemory.getDataMap().get(BizConst.KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS);
		notifyPreloader(new Preloader.ProgressNotification(0.5 + (((numberOfPlugins - pluginCountDownLatch.getCount() + 1) / ((double)numberOfPlugins)) * 0.5)));
		pluginCountDownLatch.countDown();
	}
	
	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_NOTIFY_PRELOADER_PREPARING_PLUGINS:
			notifyPreloaderPreparingPlugins();
			break;
		case BizConst.EVENT_NOTIFY_PRELOADER_COUNT_DOWN_PLUGIN:
			notifyPreloaderCountDownPlugin();
			break;
		case BizConst.EVENT_REGISTER_TRAY_ICON:
			registerTrayIcon();
			break;
		case BizConst.EVENT_APPLICATION_SHOW_HIDE:
			showOrHide();
			break;
		case BizConst.EVENT_APPLICATION_BRING_TO_FRONT:
			bringToFront();
			break;
		case BizConst.EVENT_APPLICATION_EXIT:
			exit();
			break;
		case BizConst.EVENT_UPDATE_PROXY_HOST:
			updateProxyHost();
			break;
		}
	}
	
}
