package org.silentsoft.actlist.application;

import java.awt.Desktop;
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
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.ImageIcon;

import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.util.SystemUtil;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.tray.TrayIconHandler;
import org.silentsoft.ui.util.StageUtil;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class App extends Application implements HotkeyListener {

	private static final int CTRL_ALT_A = 1;
	
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

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		initialize();
		
		stage.setTitle(BizConst.APPLICATION_NAME);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(app, Color.TRANSPARENT));
		stage.setOnCloseRequest(event -> {
			stage.hide();
		});
		
		AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
		stage.show();
	}
	
	private void initialize() throws Exception {
		Platform.setImplicitExit(false);
		
		StageUtil.registerStage(stage);
		
		// WARNING : DO NOT MODIFY FUNCTION CALL PRIORITY
		initIntellitype();
		checkSingleInstance();
		displayIcon();
		registerHotkey();
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().concat(CommonConst.EXTENSION_FXML)));
		app = fxmlLoader.load();
		appController = fxmlLoader.getController();
		Platform.runLater(() -> {
			appController.initialize();
		});
	}
	
	private void initIntellitype() {
		JIntellitype.setLibraryLocation(Paths.get(System.getProperty("user.dir"), "libs", String.join("", "JIntellitype-", SystemUtil.getOSArchitecture(), CommonConst.EXTENSION_DLL)).toString());
		
		if (JIntellitype.isJIntellitypeSupported() == false) {
			System.exit(1); // Abnormal termination.
		}
	}
	
	private void checkSingleInstance() {
		if (SystemUtil.findProcessByImageName(String.join("", BizConst.APPLICATION_NAME, CommonConst.EXTENSION_EXE))) {
			System.exit(0); // Just termination.
		}
	}
	
	private void displayIcon() {
		// taskbar
		stage.getIcons().addAll(new Function<int[], List<Image>>() {
			@Override
			public List<Image> apply(int[] values) {
				ArrayList<Image> images = new ArrayList<Image>();
				for (int size : values) {
					images.add(new Image(String.join("", "/images/icon/actlist_", String.valueOf(size), CommonConst.EXTENSION_PNG)));
				}
				return images;
			}
		}.apply(new int[]{24, 32, 48, 64, 128, 256}));
		
		// system tray
		TrayIconHandler.registerTrayIcon(new ImageIcon(getClass().getResource("/images/icon/actlist_16.png")).getImage(), BizConst.APPLICATION_NAME, actionEvent -> {
			showOrHide();
		});
		
		// TODO The user may want to change this shortcut.
		TrayIconHandler.addItem("Show/Hide (Ctrl+Alt+A)", actionEvent -> {
			showOrHide();
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
				// TODO need to change to use BuildVersion class file instead hard-coding. but, I dont have time.
				message.append("Version  : 1.0.0\r\n");
				message.append("\r\n");
				message.append("Homepage : silentsoft.org\r\n");
				message.append("\r\n");
				message.append("3rd party library\r\n");
				message.append(" JIntellitype\r\n");
				message.append(" Jidefx-common\r\n");
				message.append(" JFoenix\r\n");
				message.append(" ControlsFx\r\n");
				message.append("\r\n");
				message.append("Open Source License\r\n");
				// TODO specify open source licenses here.
				message.append(" Apache License 2.0\r\n");
				
				MessageBox.showAbout("Actlist", message.toString());
			});
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Exit", actionEvent -> {
			exit();
		});
	}
	
	private void registerHotkey() {
		
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().registerHotKey(CTRL_ALT_A, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, 'A');
	}
	
	private void showOrHide() {
		Platform.runLater(() -> {
			if (stage.isShowing()) {
				Transition animation = AnimationUtils.createTransition(app, AnimationType.BOUNCE_OUT_DOWN);
    			animation.setOnFinished(actionEvent -> {
    				stage.hide();
    			});
    			animation.play();
			} else {
				AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
				stage.show();
			}
		});
	}
	
	private void exit() {
		JIntellitype.getInstance().cleanUp();
		System.exit(0);
	}

	@Override
	public void onHotKey(int identifier) {
		switch (identifier) {
		case CTRL_ALT_A:
			showOrHide();
			break;
		}
	}
		
}
