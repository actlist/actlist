package org.silentsoft.actlist.preloader;

import java.awt.Robot;
import java.awt.event.InputEvent;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.CommonConst;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.core.util.SystemUtil;

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
