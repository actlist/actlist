package org.silentsoft.actlist.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import org.silentsoft.ui.model.Delta;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;

public class Console {
	
	@FXML
	private AnchorPane root;
	
	@FXML
	private AnchorPane head;
	
	@FXML
	private AnchorPane body;
	
	@FXML
	private Button appMinimizeBtn;
	
	@FXML
	private Button appMaximizeBtn;
	
	@FXML
	private Button appCloseBtn;
	
	@FXML
	private TextArea console;
	
	private PrintStream printStream;
	
	private MaximizeProperty maximizeProperty;

	public void initialize(Stage stage) {
		console.setContextMenu(new ContextMenu()); // disable context menu.
		
		printStream = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				Platform.runLater(() -> {
					console.appendText(String.valueOf((char) b));
				});
			}
		}, true);
		
		maximizeProperty = new MaximizeProperty(stage);
		
		makeDraggable(stage, head);
		makeNormalizable(stage, head);
		
		makeMinimizable(stage, appMinimizeBtn);
		makeMaximizable(stage, appMaximizeBtn);
		makeClosable(stage, appCloseBtn);
		
		makeResizable(stage, root);
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
    }
	
	public PrintStream getPrintStream() {
		return printStream;
	}
	
}
