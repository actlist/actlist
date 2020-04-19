* 2.0.1
    * new features
      * supports dynamic logging level configuration
      * supports log file under `/log` directory
    * improvements
      * retry with proxy and non-proxy request
      * url fetching logic to prevent non-proxy redirect request failure
      * pretty JSON serialization
    * dependency
      * update commons-codec to 1.11 from 1.6
      * update commons-logging to 1.2 from 1.1.3
      * update httpclient to 4.5.12 from 4.3.6
      * update jackson to 2.10.3 from 2.4.2
* 1.7.1
    * bugfix
      * critical JSON serialization patch
* 1.7.0
    * new features
      * supports markdown text on plugin’s About dialog
      * supports `analyze` method for checking the minimum compatible version
      * supports `ToggleFunction` on context menu
      * supports `getFunction` method for getting context menu's graphic object
    * improvements
      * user interaction when failed to plugin’s automatic update
      * minor css style adjustments
    * dependency
      * new dependency - javassist 3.26.0-GA
      * new dependency - commonmark 0.13.1
* 1.6.0
    * new features
      * supports dark mode UI
      * supports statistics through `setPluginStatisticsUUID()`
      * supports `Clear` context menu on console view
    * bugfix
      * launch fails with non-ISO-8859-1 path on Windows
      * file encoding as a UTF-8 on Windows
      * detect wrong filename from download url
      * certain jar DnD with NPE
      * transparent background bug through `showLoadingBar()`
    * changes
      * `debug(...)` method parameter as a builder pattern
      * some of the `debug(...)` method has been deprecated
      * minor UI changes
    * breaking changes (access modifier)
      * `setSupportedPlatforms()`
      * `setOneTimePlugin()`
      * `setPluginArchivesURI()`
      * `setBeforeRequest()`
      * `setPluginUpdateCheckURI()`
    * dependency
      * update JFoenix dependency to 8.0.8 from 1.0.0
* 1.5.2
    * improvements
      * minor speed improvements
    * bugfix
      * wrong plugin config file interactions
* 1.5.1
    * improvements
      * supports direct install from explore view
      * `debug(isDebugMode)` and `debug(proxy)` method has been added
      * supports `ActlistPlugin.isDebugMode()` method for public static access
      * supports `requiredActlist` parameter from plugin's update check response for restrict automatic updates
* 1.5.0
    * new features
      * supports automatic plugin updates
      * `Check for updates` context menu
      * `Delete` context menu
    * improvements
      * multiple jar DnD experience
      * native drag experience on Mac theme
    * breaking changes
      * pluginArchivesURI will respect server's update check response
* 1.4.3
    * critical dependency patch
* 1.4.2
    * new features
      * supports `mainClass` property on plugin's MANIFEST.MF
    * improvements
      * fully supported debugging experience
      * UTF-8 console logging
    * changes
      * Win theme UI/UX
      * default stage/root size
    * bugfix
      * event bubbling on context menu
* 1.4.1
    * minor bugfix
* 1.4.0
    * new features
      * brand new UI
      * supports platform theme
      * new plugin explore menu
    * changes
      * java version will be displayed on About view
      * update/warning label will be disappeared after interactions
      * minor changes on first meet prompt label
    * fixed bugs
      * http/https proxy host issue
      * scroll bar broken issue on WebView
      * tray notification dismiss issue
* 1.3.0
    * new features
      * supports platform restriction function in plugin
      * supports feedback tray menu
    * improvements
      * interaction on engine initialization
      * installation path can be selected on Windows exe installer
      * desktop icon can be created on Windows exe installer
      * caller plugin name will be shown in the tray notification
    * fixed bugs
      * RESTfulAPI initialization
      * unicode installation path
      * mouse event consumming issue
      * wrong UI interactions on all platforms
* 1.2.10
    * new features
      * supports one-time plugin which deactivates immediately after activated
      * supports plugin's update check through `.js` file
      * Actlist showing/hiding feature
    * minor bugfix
* 1.2.9
    * new features
      * plugin's kill switch
      * plugin's end of service
    * minor bugfix
* 1.2.8
    * update check request will be executed every 24 hours.
    * minor UX improvements 
      * orange dot and red dot will have animation
      * the tabs on plugin's about dialog are displayed dynamically.
    * minor bugfix 
      * getConfig() / putConfig() on debug mode
* 1.2.7
    * supports UTF-8 content on plugin's about dialog
    * plugin can be added through drag and drop
      * also stage right click -> 'Add a new plugin' too
    * minor UI changes
    * minor UX improvements
    * minor bugfix
      * context menu
* 1.2.6
    * finally, `debug();` method can be used in the Plugin's main method
    * supports MessageBox owner
    * supports proxy setting (None / Automatic / Manual)
    * supports Plugin's update check
    * supports warning text on Plugin
    * minor UI changes on About dialog
    * minor bugfix
      * hotkey on Mac
      * stage resize
* 1.2.5
    * JVM tuning options has been modified.
* 1.2.4
    * supports Actlist latest update alarm.
    * minor feature has been added. (minimum compatible version / tray notification / G1GC option)
* 1.2.3
    * supports Mac OS X.
    * supports console viewer for logging.
    * application executing speed has been improved.
    * the menu has been added for open the plugins directory.
* 1.2.2
    * minor feature has been improved. (function priority)
    * minor feature has been modified.
      * applicationActivated() / applicationDeactivated() method will called even if the application is minimized.
    * minor feature has been added. (applicationCloseRequested method)
      * each plugins can defining an action that called when the application is receives a close request.
* 1.2.1
    * exception throwing feature.
    * minor design changes.
    * minor bug has been fixed.
* 1.2.0
    * configure plugin's priority via drag and drop.
    * save plugin's priority.
    * supports About menu to each plugin.
    * scroll bar design.
    * ESC key to hide the Actlist.
    * save application window size.
    * windows key can be configured by hotkey.
    * minor feature has been improved.
    * minor bug has been fixed.
* 1.1.0
    * load plugins by parallel and speed has been improved.
    * supports Configuration dialog.
    * hotkey change feature.
    * opacity change feature.
    * always on top feature.
    * enable/disable to animation effect.
    * fixed bug
      * terminate Actlist when some plugin raised exception.
      * not showing up when after first minimize to Actlist.
      * application single instance.
      * change application window size.
* 1.0.0
    * The first proper release