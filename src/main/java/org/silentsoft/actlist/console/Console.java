package org.silentsoft.actlist.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.util.ConfigUtil;
import org.silentsoft.actlist.util.ConfigUtil.Theme;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.model.Delta;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class Console implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Console.class);
	
	@FXML
	private AnchorPane root;
	
	@FXML
	private BorderPane head;
	
	@FXML
	private AnchorPane body;
	
	@FXML
	private HBox leftBox;
	
	@FXML
	private Label leftMinimizeButton, leftMaximizeButton, leftCloseButton;
	
	@FXML
	private Label icon, title;
	
	@FXML
	private HBox rightBox;
	
	@FXML
	private Label rightMinimizeButton, rightMaximizeButton, rightCloseButton;
	
	@FXML
	private TextArea console;
	
	static PrintStream consoleStream;
	static PrintStream advancedStream;
	private PrintStream printStream;
	
	private MaximizeProperty maximizeProperty;

	public void initialize(Stage stage) {
		EventHandler.addListener(this);
		
		setContextMenu();
		
		consoleStream = new PrintStream(new ByteArrayOutputStream() {
			@Override
			public synchronized void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				
				try {
					String log = toString("UTF-8");
					reset();
					
					Platform.runLater(() -> {
						Object appConsole = SharedMemory.getDataMap().get(BizConst.KEY_CONSOLE_TEXT_AREA);
						if (appConsole instanceof TextArea) {
							((TextArea) appConsole).appendText(log);
						}
					});
				} catch (Exception e) {
					;
				}
			}
		}, true);
		advancedStream = new PrintStream(new ByteArrayOutputStream() {
			@Override
			public synchronized void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				
				try {
					String log = toString("UTF-8");
					reset();
					
					Platform.runLater(() -> {
						console.appendText(log);
					});
				} catch (Exception e) {
					;
				}
			}
		}, true);
		printStream = new PrintStream(new ByteArrayOutputStream() {
			@Override
			public synchronized void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				
				try {
					String log = toString("UTF-8");
					reset();
					
					if (log.trim().isEmpty() == false) {
						if (log.equals(String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_INFO_TEXT)))) {
							consoleStream.println(log);
							advancedStream.println(log);
						} else {
							LOGGER.info(log);
						}
					}
				} catch (Exception e) {
					;
				}
			}
		}, true);
		
		maximizeProperty = new MaximizeProperty(stage);
		
		makeDraggable(stage, head);
		makeNormalizable(stage, head);
		{
			makeMinimizable(stage, leftMinimizeButton);
			makeMaximizable(stage, leftMaximizeButton);
			makeClosable(stage, leftCloseButton);
			
			makeMinimizable(stage, rightMinimizeButton);
			makeMaximizable(stage, rightMaximizeButton);
			makeClosable(stage, rightCloseButton);
		}
		applyTheme();
		
		makeResizable(stage, root);
	}
	
	public void notifyFocusState(boolean isFocused) {
		if (ConfigUtil.isMacTheme()) {
			if (isFocused) {
				leftCloseButton.setStyle("-fx-background-color: red; -fx-background-radius: 5em;");
				leftMinimizeButton.setStyle("-fx-background-color: orange; -fx-background-radius: 5em;");
				leftMaximizeButton.setStyle("-fx-background-color: #59bf53; -fx-background-radius: 5em;");
			} else {
				leftCloseButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
				leftMinimizeButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
				leftMaximizeButton.setStyle("-fx-background-color: #808080; -fx-background-radius: 5em;");
			}
		}
	}
	
	private void setContextMenu() {
		MenuItem clearMenuItem = new MenuItem("Clear");
		clearMenuItem.setOnAction(event -> clearConsoleLog());
		console.setContextMenu(new ContextMenu(clearMenuItem));
	}
	
	/**
	 * makes a stage draggable using a given node.
	 * @param stage
	 * @param byNode
	 */
    private void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        
        byNode.setOnMousePressed(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		dragDelta.setX(stage.getX() - mouseEvent.getScreenX());
                dragDelta.setY(stage.getY() - mouseEvent.getScreenY());
                
                byNode.setOpacity(0.8);
        	}
        });
        
        byNode.setOnMouseDragged(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		if (maximizeProperty.isMaximized()) {
        			double x = (mouseEvent.getScreenX() - stage.getX());
        			double y = (mouseEvent.getScreenY() - stage.getY());

        			changeMaximizeProperty(stage);
        			
                    dragDelta.setX(-1 * (stage.getWidth() / 2));
                    dragDelta.setY(-1 * (mouseEvent.getSceneY()));
        		} else {
        			stage.setX(mouseEvent.getScreenX() + dragDelta.getX());
                    stage.setY(mouseEvent.getScreenY() + dragDelta.getY());
        		}
    		}
        });
        
        byNode.setOnMouseReleased(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		byNode.setOpacity(1.0);
        	}			
		});
    }
    
    /**
     * makes a stage normalizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeNormalizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getClickCount() >= 2) {
    			changeMaximizeProperty(stage);
    		}
    	});
    }
    
    /**
     * makes a stage minimizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMinimizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			/**
    			 * EventHandler.callEvent(getClass(), BizConst.EVENT_APPLICATION_SHOW_HIDE);
    			 * 
    			 * Do not call the above event. make sure to stay on taskbar.
    			 */
    			stage.setIconified(true);
    		}
    	});
    }
    
    /**
     * makes a stage maximizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMaximizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			changeMaximizeProperty(stage);
    		}
    	});
    }
    
    /**
     * makes a stage closable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeClosable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			stage.close();
    		}
    	});
    }
    
    private void makeResizable(final Stage stage, final Region region) {
    	StageDragResizer.makeResizable(stage, region, 7, 10);
    }
    
    private void changeMaximizeProperty(Stage stage) {
    	maximizeProperty.setMaximized(stage, !maximizeProperty.isMaximized());
		if (maximizeProperty.isMaximized()) {
			// This option is recommended when maximized.
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			
			AnchorPane.setLeftAnchor(head, 0.0);
			AnchorPane.setRightAnchor(head, 0.0);
			AnchorPane.setTopAnchor(head, 0.0);
			
			AnchorPane.setLeftAnchor(body, 0.0);
			AnchorPane.setRightAnchor(body, 0.0);
			AnchorPane.setTopAnchor(body, 25.0);
			AnchorPane.setBottomAnchor(body, 0.0);
		} else {
			// Showing shadow when normalized.
			AnchorPane.setLeftAnchor(root, 5.0);
			AnchorPane.setRightAnchor(root, 5.0);
			AnchorPane.setTopAnchor(root, 5.0);
			AnchorPane.setBottomAnchor(root, 5.0);
			
			// Make offset for change the size of application via mouse.
			AnchorPane.setLeftAnchor(head, 2.0);
			AnchorPane.setRightAnchor(head, 2.0);
			AnchorPane.setTopAnchor(head, 2.0);
			AnchorPane.setLeftAnchor(body, 2.0);
			AnchorPane.setRightAnchor(body, 2.0);
			AnchorPane.setTopAnchor(body, 27.0);
			AnchorPane.setBottomAnchor(body, 2.0);
		}
		stage.getScene().getRoot().requestLayout();
    }
    
    private void applyTheme() {
    	String theme = ConfigUtil.getTheme();
    	switch (theme) {
    	case Theme.WIN:
    		head.setLeft(null);
    		
    		icon.setVisible(true);
    		title.setVisible(true);
    		
    		head.setRight(rightBox);
    		
    		break;
    	case Theme.MAC:
    		head.setRight(null);
    		
    		icon.setVisible(false);
    		title.setVisible(false);
    		
    		head.setLeft(leftBox);
    		
    		break;
    	}
    }
    
    private void clearConsoleLog() {
    	console.clear();
		
		Object appConsole = SharedMemory.getDataMap().get(BizConst.KEY_CONSOLE_TEXT_AREA);
		if (appConsole instanceof TextArea) {
			((TextArea) appConsole).clear();
		}
		
		System.out.println(String.valueOf(SharedMemory.getDataMap().get(BizConst.KEY_INFO_TEXT)));
    }
    
    @FXML
    private void showControls() {
    	((SVGPath) leftCloseButton.getGraphic()).setFill(Paint.valueOf("rgb(30, 30, 30)"));
    	((SVGPath) leftMinimizeButton.getGraphic()).setFill(Paint.valueOf("rgb(30, 30, 30)"));
    	((SVGPath) leftMaximizeButton.getGraphic()).setFill(Paint.valueOf("rgb(30, 30, 30)"));
    }
    
    @FXML
    private void hideControls() {
    	((SVGPath) leftCloseButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    	((SVGPath) leftMinimizeButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    	((SVGPath) leftMaximizeButton.getGraphic()).setFill(Paint.valueOf("transparent"));
    }
    
    static PrintStream getConsoleStream() {
		return consoleStream;
	}
    static PrintStream getAdvancedStream() {
    	return advancedStream;
    }
	public PrintStream getPrintStream() {
		return printStream;
	}

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_APPLY_THEME:
			applyTheme();
			break;
		case BizConst.EVENT_CLEAR_CONSOLE_LOG:
			clearConsoleLog();
			break;
		}
	}
	
}
