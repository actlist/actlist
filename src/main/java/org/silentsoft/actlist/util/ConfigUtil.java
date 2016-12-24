package org.silentsoft.actlist.util;

import org.silentsoft.actlist.ActlistConfig;
import org.silentsoft.actlist.BizConst;
import org.silentsoft.io.memory.SharedMemory;

import com.melloware.jintellitype.JIntellitype;

public class ConfigUtil {

	public static ActlistConfig getActlistConfig() {
		return (ActlistConfig) SharedMemory.getDataMap().get(BizConst.KEY_ACTLIST_CONFIG);
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
		if ((modifier & JIntellitype.MOD_CONTROL) == JIntellitype.MOD_CONTROL) {
			hotKeyText = hotKeyText.concat("Ctrl + ");
		}
		if ((modifier & JIntellitype.MOD_ALT) == JIntellitype.MOD_ALT) {
			hotKeyText = hotKeyText.concat("Alt + ");
		}
		if ((modifier & JIntellitype.MOD_SHIFT) == JIntellitype.MOD_SHIFT) {
			hotKeyText = hotKeyText.concat("Shift + ");
		}
		
		return hotKeyText.concat(String.valueOf((char) getShowHideActlistHotKeyCode()));
	}
	
	public static boolean isAnimationEffect() {
		return (boolean) getActlistConfig().get("animationEffect");
	}
	
	public static void setAnimationEffect(boolean value) throws Exception {
		getActlistConfig().put("animationEffect", value);
	}
	
	public static boolean isAlwaysOnTop() {
		return (boolean) getActlistConfig().get("alwaysOnTop");
	}
	
	public static void setAlwaysOnTop(boolean value) throws Exception {
		getActlistConfig().put("alwaysOnTop", value);
	}
	
}
