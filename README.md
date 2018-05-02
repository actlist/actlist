# Actlist
> Easy and simply execute your own act list.


Actlist will helps you to execute your desire things.

Just do focus only on coding to create what you want. and you can share with others to makes better world.

![](http://actlist.silentsoft.org/img/preview.png)

## Installation

Windows:
> [download exe or zip file](http://actlist.silentsoft.org/archives/) and launch.

Mac OS X:
> [download dmg file](http://actlist.silentsoft.org/archives/) and launch.

## Development setup

To create an Actlist plugin, you need to do some of the following:
* Clone projects as following command or clone via GUI.
```
git clone https://github.com/silentsoft/silentsoft-io.git
git clone https://github.com/silentsoft/silentsoft-core.git
git clone https://github.com/silentsoft/actlist-plugin.git
```
* Create a new Java project and configure to Maven project.
* Add dependency as like below:
```
<dependency>
	<groupId>org.silentsoft</groupId>
	<artifactId>actlist-plugin</artifactId>
	<version>1.0.0</version>
</dependency>
```
* Generate executable main class called `Plugin` where in default package (please do not assign package).
* Inherit the `ActlistPlugin` class in your `Plugin` class.
* (Optional) to make a plugin that contains graphic things, you can write the `Plugin.fxml` file where in the same location.
* (Optional) you can set the plugin's icon image to display on about menu (Right click > About) through `Plugin.png`. if not exists `Plugin.png` then default Actlist logo image will be displayed.
* Export your project to runnable jar file and put into `/plugins/` directory that under the Actlist installed path.

Here is an example source code of `Plugin.java`
```
import org.silentsoft.actlist.plugin.ActlistPlugin;

public class Plugin extends ActlistPlugin {
    
    public static void main(String args[]) throws Exception {
        debug();
    }
    
    public Plugin() throws Exception {
        super("Example Plugin");
        
        setPluginVersion("1.0.0");
        /**
         * you can induce to use the latest version of the plugin to your users via
         * setPluginUpdateCheckURI(URI.create("http://your-server.name"), URI.create("http://location-of-archives"));
         */
        
        setPluginAuthor("Silentsoft");
        /**
         * or you could use hyper-link via
         * setPluginAuthor("Silentsoft", URI.create("http://silentsoft.org"));
         */
        
        setPluginDescription("You can set the description of your plugin");
        /**
         * or you could use file via
         * setPluginDescription(getClass().getResource("/Plugin.description").toURI());
         *
         * ! you can set the plugin's ChangeLog and License with same way
         */
    }
    
    @Override
    protected void initialize() throws Exception {
        System.out.println("Hello, World !");
    }
    
    @Override
    public void pluginActivated() throws Exception {
        System.out.println("plugin is activated.");
    }
    
    @Override
    public void pluginDeactivated() throws Exception {
        System.out.println("plugin is deactivated.");
    }

}
```

For more information on ActlistPlugin development, see [here](http://actlist.silentsoft.org/docs/quick-start/)

## Change log

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

## Third party

__material icons__
 * https://material.io/icons/
 * Apache License 2.0

__commons-codec 1.6__
 * https://github.com/apache/commons-codec
 * Apache License 2.0

__commons-daemon 1.0.10__
 * https://github.com/apache/commons-daemon
 * Apache License 2.0

__commons-io 2.4__
 * https://github.com/apache/commons-io
 * Apache License 2.0

__commons-lang 2.6__
 * https://github.com/apache/commons-lang
 * Apache License 2.0

__commons-logging 1.1.3__
 * https://github.com/apache/commons-logging
 * Apache License 2.0

__commons-net 3.3__
 * https://github.com/apache/commons-net
 * Apache License 2.0

__httpclient 4.3.6__
 * https://github.com/apache/httpcomponents-client
 * Apache License 2.0

__httpcore 4.3.3__
 * https://github.com/apache/httpcomponents-core
 * Apache License 2.0

__httpmime 4.3.6__
 * Apache License 2.0

__log4j 1.2.17__
 * https://github.com/apache/log4j
 * Apache License 2.0

__centerdevice-nsmenufx 2.1.5__
 * https://github.com/codecentric/NSMenuFX
 * BSD-3-Clause

__controlsfx 8.40.10__
 * https://bitbucket.org/controlsfx/controlsfx
 * BSD-3-Clause

__json 20141113__
 * https://github.com/douglascrockford/JSON-java
 * JSON License

__jackson-annotations 2.4.2__
 * https://github.com/FasterXML/jackson-annotations
 * Apache License 2.0

__jackson-core 2.4.2__
 * https://github.com/FasterXML/jackson-core
 * Apache License 2.0

__jackson-databind 2.4.2__
 * https://github.com/FasterXML/jackson-databind
 * Apache License 2.0

__jfoenix 1.0.0__
 * https://github.com/jfoenixadmin/Jfoenix
 * Apache License 2.0

__jna 4.5.1__
 * https://github.com/java-native-access/jna
 * Apache License 2.0

__jna-platform 4.5.1__
 * https://github.com/java-native-access/jna
 * Apache License 2.0

__proxy-vole 1.0.3__
 * https://github.com/MarkusBernhardt/proxy-vole
 * Apache License 2.0

__ini4j 0.5.4__
 * https://github.com/michaelPf85/ini4j
 * Apache License 2.0

__PlusHaze-TrayNotification__
 * https://github.com/PlusHaze/TrayNotification
 * MIT License

__slf4j-api 1.7.5__
 * https://github.com/qos-ch/slf4j
 * MIT License

__slf4j-log4j12 1.7.5__
 * https://github.com/qos-ch/slf4j
 * MIT License

__jkeymaster 1.2__
 * https://github.com/tulskiy/jkeymaster
 * LGPL-3.0
