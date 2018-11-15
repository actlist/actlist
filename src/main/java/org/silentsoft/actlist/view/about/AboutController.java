package org.silentsoft.actlist.view.about;

import java.awt.Desktop;
import java.net.URI;

import org.silentsoft.actlist.BizConst;
import org.silentsoft.actlist.version.BuildVersion;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.viewer.AbstractViewerController;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AboutController extends AbstractViewerController {

	@FXML
	private Label version, runtime;
	
	@FXML
	private HBox newVersionBox;
	
	@FXML
	private Hyperlink archivesLink, sourceCodeLink, licenseLink, changeLogLink, homepageLink;
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		version.setText(BuildVersion.VERSION);
		runtime.setText(String.format("%s, %s", System.getProperty("java.vm.name"), System.getProperty("java.runtime.version")));
		
		boolean isAvailableNewActlist = (boolean) SharedMemory.getDataMap().getOrDefault(BizConst.KEY_IS_AVAILABLE_NEW_ACTLIST, false);
		if (isAvailableNewActlist) {
			newVersionBox.setVisible(true);
		} else {
			newVersionBox.setVisible(false);
		}
	}
	
	@FXML
	private void browseHomepage() {
		browse(homepageLink, "http://actlist.silentsoft.org");
	}
	
	@FXML
	private void browseArchives() {
		browse(archivesLink, "http://actlist.silentsoft.org/archives/");
	}
	
	@FXML
	private void browseSourceCode() {
		browse(sourceCodeLink, "https://github.com/silentsoft/actlist");
	}
	
	@FXML
	private void browseLicense() {
		browse(licenseLink, "https://github.com/silentsoft/actlist/blob/master/NOTICE.md");
	}
	
	@FXML
	private void browseChangeLog() {
		browse(changeLogLink, "https://github.com/silentsoft/actlist/blob/master/CHANGELOG.md");
	}
	
	private void browse(Hyperlink hyperlink, String uri) {
		hyperlink.setVisited(false);
		
		try {
			Desktop.getDesktop().browse(URI.create(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
