package org.silentsoft.actlist.util;

import java.awt.event.InputEvent;

import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.io.memory.SharedMemory;

public class ConfigUtil {

	public static ActlistConfig getActlistConfig() {
		return (ActlistConfig) SharedMemory.getDataMap().get(BizConst.KEY_ACTLIST_CONFIG);
	}
	
	public static double getRootWidth() {
		Object rootWidth = getActlistConfig().get("rootWidth");
		if (rootWidth instanceof Integer) {
			return ((int) rootWidth) + 0.0;
		}
		return ((double) rootWidth);
	}
	
	public static void setRootWidth(double width) throws Exception {
		getActlistConfig().put("rootWidth", width);
	}
	
	public static double getRootHeight() {
		Object rootHeight = getActlistConfig().get("rootHeight");
		if (rootHeight instanceof Integer) {
			return ((int) rootHeight) + 0.0;
		}
		return ((double) rootHeight);
	}
	
	public static void setRootHeight(double height) throws Exception {
		getActlistConfig().put("rootHeight", height);
	}
	
	public static double getStageWidth() {
		Object stageWidth = getActlistConfig().get("stageWidth");
		if (stageWidth instanceof Integer) {
			return ((int) stageWidth) + 0.0;
		}
		return ((double) stageWidth);
	}
	
	public static void setStageWidth(double width) throws Exception {
		getActlistConfig().put("stageWidth", width);
	}
	
	public static double getStageHeight() {
		Object stageHeight = getActlistConfig().get("stageHeight");
		if (stageHeight instanceof Integer) {
			return ((int) stageHeight) + 0.0;
		}
		return ((double) stageHeight);
	}
	
	public static void setStageHeight(double height) throws Exception {
		getActlistConfig().put("stageHeight", height);
	}
	
	public static double getStageOpacity() {
		Object stageOpacity = getActlistConfig().get("stageOpacity");
		if (stageOpacity instanceof Integer) {
			return ((int) stageOpacity) + 0.0;
		}
		return ((double) stageOpacity);
	}
	
	public static void setStageOpacity(double opacity) throws Exception {
		getActlistConfig().put("stageOpacity", opacity);
	}
	
	public static boolean isAlwaysOnTop() {
		return (boolean) getActlistConfig().get("alwaysOnTop");
	}
	
	public static void setAlwaysOnTop(boolean value) throws Exception {
		getActlistConfig().put("alwaysOnTop", value);
	}
	
	public static String getLoggingLevel() {
		return getActlistConfig().get("loggingLevel");
	}
	
	public static void setLoggingLevel(String value) throws Exception {
		getActlistConfig().put("loggingLevel", value);
	}
	
	/*
	public static boolean isAnimationEffect() {
		return (boolean) getActlistConfig().get("animationEffect");
	}
	
	public static void setAnimationEffect(boolean value) throws Exception {
		getActlistConfig().put("animationEffect", value);
	}
	*/
	
	public static int getShowHideActlistHotKeyModifier() {
		return (int) getActlistConfig().get("showHideActlistHotKeyModifier");
	}
	
	public static void setShowHideActlistHotKeyModifier(int modifier) throws Exception {
		getActlistConfig().put("showHideActlistHotKeyModifier", modifier);
	}
	
	public static int getShowHideActlistHotKeyCode() {
		return (int) getActlistConfig().get("showHideActlistHotKeyCode");
	}
	
	public static void setShowHideActlistHotKeyCode(int code) throws Exception {
		getActlistConfig().put("showHideActlistHotKeyCode", code);
	}
	
	public static String getShowHideActlistHotKeyText() {
		String hotKeyText = "";
		
		int modifier = getShowHideActlistHotKeyModifier();
		if ((modifier & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			hotKeyText = hotKeyText.concat("Ctrl + ");
		}
		if ((modifier & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK) {
			hotKeyText = hotKeyText.concat("Alt + ");
		}
		if ((modifier & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
			hotKeyText = hotKeyText.concat("Shift + ");
		}
		if ((modifier & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK) {
			if (SystemUtil.isWindows()) {
				hotKeyText = hotKeyText.concat("Win + ");
			} else if (SystemUtil.isMac()) {
				hotKeyText = hotKeyText.concat("Cmd + ");
			} else {
				hotKeyText = hotKeyText.concat("Meta + ");
			}
		}
		
		return hotKeyText.concat(String.valueOf((char) getShowHideActlistHotKeyCode()));
	}

	public class Theme {
		// Do not make as enum type. It must be a string for simple json serialize/deserialize.
		public static final String MAC = "MAC";
		public static final String WIN = "WIN";
	}
	
	public static String getTheme() {
		Object theme = getActlistConfig().get("theme");
		if (theme == null) { // platform dependent property is not saved on initial config.jar
			try {
				if (SystemUtil.isMac()) {
					theme = Theme.MAC;
				} else {
					theme = Theme.WIN;
				}
				setTheme((String) theme);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return (String) theme;
	}
	
	public static void setTheme(String theme) throws Exception {
		getActlistConfig().put("theme", theme);
	}
	
	public static boolean isMacTheme() {
		return Theme.MAC.equalsIgnoreCase(getTheme());
	}
	
	public static boolean isWinTheme() {
		return Theme.WIN.equalsIgnoreCase(getTheme());
	}
	
	public static boolean isDarkMode() {
		return (boolean) getActlistConfig().get("darkMode");
	}
	
	public static void setDarkMode(boolean value) throws Exception {
		getActlistConfig().put("darkMode", value);
	}
	
	public class ProxyMode {
		// Do not make as enum type. It must be a string for simple json serialize/deserialize.
		public static final String NONE = "NONE";
		public static final String AUTOMATIC = "AUTOMATIC";
		public static final String MANUAL = "MANUAL";
	}
	
	public static String getProxyMode() {
		return (String) getActlistConfig().get("proxyMode");
	}
	
	public static void setProxyMode(String proxyMode) throws Exception {
		getActlistConfig().put("proxyMode", proxyMode);
	}
	
	public static String getProxyHost() {
		return (String) getActlistConfig().get("proxyHost");
	}
	
	public static void setProxyHost(String proxyHost) throws Exception {
		getActlistConfig().put("proxyHost", proxyHost);
	}

}
