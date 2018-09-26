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
	<version>1.0.1</version>
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

## License

Please refer to [LICENSE](https://github.com/silentsoft/actlist/blob/master/LICENSE.txt) and [NOTICE](https://github.com/silentsoft/actlist/blob/master/NOTICE.md).
