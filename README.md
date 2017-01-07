# Actlist
> Easy and simply execute your own act list.

Actlist will helps you to execute your desire things.
Just do focus only on coding to create what you want then share with others to make the world better.

![](http://silentsoft.org/actlist/preview.png)

## Installation

Windows:
> [download setup file](http://silentsoft.org/actlist/archives/) and launch.

OS X & Linux:
> not support yet.


## Development setup

To create an Actlist plugin, you need to do some of the following:
* Clone projects as following command or clone via GUI.
    ```
    git clone https://github.com/silentsoft/actlist-plugin.git
    git clone https://github.com/silentsoft/silentsoft-core.git
    git clone https://github.com/silentsoft/silentsoft-io.git
    ```
* Create a new Java project and configure to Maven project.
* Generate executable main class called `Plugin` where in default package (please do not assign package).
* Inherit the `ActlistPlugin` class in your `Plugin` class.
* (Option) to make a plugin that contains graphic things, you can write the `Plugin.fxml` file where in the same location.
* (Option) you can set the plugin's icon image that display where in about section(Right click-About) through `Plugin.png`
* Export your project to runnable jar file and put into `/plugins/` directory that under the Actlist installed path.

Here is an example source code of `Plugin.java`
```
import org.silentsoft.actlist.plugin.ActlistPlugin;

public class Plugin extends ActlistPlugin {
    
    public static void main(String args[]) throws Exception {}
    
    public Plugin() {
        super("Example Plugin");
        
        setPluginDescription("You can set the description of your plugin");
        setPluginVersion("1.0.0");
        setPluginAuthor("Silentsoft (http://silentsoft.org)");
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


## Release History

* 1.2.0
    * configure plugin's priority via drag and drop
    * save plugin's priority
    * provide About menu to each plugin
    * scroll bar design
    * ESC key to hide the Actlist
    * save application window size
    * windows key can be configured by hotkey
    * improved a minor feature
    * fixed a minor bug
* 1.1.0
    * load plugins by parallel and improved speed
    * provide Configuration dialog
    * hotkey change feature
    * opacity change feature
    * always on top feature
    * enable/disable to animation effect
    * fixed bug
      * terminate Actlist when some plugin raised exception
      * not showing up when after first minimize to Actlist
      * application single instance
      * change application window size
* 1.0.0
    * The first proper release
