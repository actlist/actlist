package org.silentsoft.actlist;

public class BizConst {

	public static final String KEY_ACTLIST_CONFIG = "KEY_ACTLIST_CONFIG";
	public static final String KEY_DEACTIVATED_PLUGINS = "KEY_DEACTIVATED_PLUGINS";
	public static final String KEY_PRIORITY_OF_PLUGINS = "KEY_PRIORITY_OF_PLUGINS";
	public static final String KEY_COMPONENT_BOX = "KEY_COMPONENT_BOX";
	public static final String KEY_PLUGIN_MAP = "KEY_PLUGIN_MAP";
	
	public static final String KEY_IS_AVAILABLE_NEW_ACTLIST = "KEY_IS_AVAILABLE_NEW_ACTLIST";
	
	public static final String KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS = "KEY_NOTIFY_PRELOADER_NUMBER_OF_PLUGINS";
	public static final String EVENT_NOTIFY_PRELOADER_PREPARING_PLUGINS = "EVENT_NOTIFY_PRELOADER_PREPARING_PLUGINS";
	public static final String EVENT_NOTIFY_PRELOADER_COUNT_DOWN_PLUGIN = "EVENT_NOTIFY_PRELOADER_COUNT_DOWN_PLUGIN";
	
	/**
	 * Actlist application will register the tray icon when this event is called.
	 */
	public static final String EVENT_REGISTER_TRAY_ICON          = "EVENT_REGISTER_TRAY_ICON";
	/**
	 * Actlist application will show or hide when this event is called.
	 */
	public static final String EVENT_APPLICATION_SHOW_HIDE       = "EVENT_APPLICATION_SHOW_HIDE";
	/**
	 * Actlist application will bring to front when this event is called.
	 */
	public static final String EVENT_APPLICATION_BRING_TO_FRONT  = "EVENT_APPLICATION_BRING_TO_FRONT";
	/**
	 * Actlist application will exit when this event is called.
	 */
	public static final String EVENT_APPLICATION_EXIT            = "EVENT_APPLICATION_EXIT";
	/**
	 * Actlist application will save deactivated plugins list when this event is called.
	 */
	public static final String EVENT_SAVE_DEACTIVATED_PLUGINS    = "EVENT_SAVE_DEACTIVATED_PLUGINS";
	/**
	 * Actlist application will save priority of plugins list when this event is called.
	 */
	public static final String EVENT_SAVE_PRIORITY_OF_PLUGINS    = "EVENT_SAVE_PRIORITY_OF_PLUGINS";

	/**
	 * Plugin's master proxy host information will be updated when this event is called.
	 */
	public static final String EVENT_UPDATE_PROXY_HOST = "EVENT_UPDATE_PROXY_HOST";
	
	/**
	 * Plugin's applicationActivated() method will be called when this event is called.
	 */
	public static final String EVENT_APPLICATION_ACTIVATED       = "EVENT_APPLICATION_ACTIVATED";
	/**
	 * Plugin's applicationDeactivated() method will be called when this event is called.
	 */
	public static final String EVENT_APPLICATION_DEACTIVATED     = "EVENT_APPLICATION_DEACTIVATED";
	/**
	 * Plugin's applicationCloseRequested() method will be called when this event is called.
	 */
	public static final String EVENT_APPLICATION_CLOSE_REQUESTED = "EVENT_APPLICATION_CLOSE_REQUESTED";
	
	
	public static final String APPLICATION_NAME = "Actlist";
	
	public static final String PLUGIN_CLASS_NAME = "Plugin";
	
}
