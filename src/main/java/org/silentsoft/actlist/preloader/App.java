package org.silentsoft.actlist.preloader;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.nio.file.Paths;

import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.ProxyMode;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.JSONUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.memory.SharedMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Preloader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private Stage stage;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private ProgressBar statusProgressBar;
	
//	public static void main(String[] args) {
//		launch(args);
//	}
	
	@Override
	public void start(Stage stage) throws Exception {
		generateUserAgentAndInfoText();
		loadConfiguration();
		checkSingleInstance();
		
		this.stage = stage;
		
		stage.initStyle(StageStyle.TRANSPARENT);
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().concat(CommonConst.EXTENSION_FXML)));
			fxmlLoader.setController(this);
			stage.setScene(new Scene(fxmlLoader.load(), Color.TRANSPARENT));
		}
        stage.setWidth(400);
        stage.setHeight(360);
        stage.show();
	}
	
	public static void generateUserAgentAndInfoText() {
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
	
	private static void welcome() {
		String text = String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_INFO_TEXT));
		for (String message : text.split("\r\n")) {
			LOGGER.info(message);
		}
	}
	
	public static void loadConfiguration() {
		try {
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
//				actlistConfig.put("animationEffect", true);
				actlistConfig.put("alwaysOnTop", false);
				actlistConfig.put("darkMode", false);
				actlistConfig.put("proxyMode", ProxyMode.AUTOMATIC);
				actlistConfig.put("proxyHost", "");
			}
			
			SharedMemory.getDataMap().put(BizConst.KEY_ACTLIST_CONFIG, actlistConfig);
		} catch (Exception e) {
			
		}
	}
	
	private static void checkSingleInstance() {
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
	
    @Override
    public void handleApplicationNotification(PreloaderNotification preloaderNotification) {
    	if (preloaderNotification instanceof ProgressNotification) {
            double progress = ((ProgressNotification) preloaderNotification).getProgress();
            statusProgressBar.setProgress(progress);
            
            if (Double.compare(progress, 0.3) == 0) {
            	statusLabel.setText("Preparing engine");
            } else if (Double.compare(progress, 0.5) == 0) {
            	statusLabel.setText("Preparing plugins");
            } else if (Double.compare(progress, 1.0) >= 0) {
            	statusLabel.setText("Enjoy your own Actlist !");
            }
         } else if (preloaderNotification instanceof StateChangeNotification) {
        	 if (((StateChangeNotification) preloaderNotification).getType() == StateChangeNotification.Type.BEFORE_START) {
	             stage.hide();
        	 }
         }
    }

}
