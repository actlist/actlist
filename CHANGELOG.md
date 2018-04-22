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