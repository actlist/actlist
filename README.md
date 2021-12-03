<h1 align="center">
  <img src="https://github.com/actlist/actlist/blob/dev/src/main/resources/images/icon/actlist_64.png?raw=true"></br>
  Actlist
</h1>
<h2 align="center">
  <sup>
    <h5>Easy and simply execute your own act list.</h5>
  </sup>
  <div>

[![release](https://shields.io/github/v/release/actlist/actlist?display_name=tag)](https://actlist.io/archives/)
[![Build Status](https://travis-ci.com/actlist/actlist.svg?branch=dev)](https://travis-ci.com/actlist/actlist)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fsilentsoft%2Factlist.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fsilentsoft%2Factlist?ref=badge_shield)
![downloads-total](https://actlist.silentsoft.org/api/shields/downloads-total)
![usage](https://actlist.silentsoft.org/api/shields/usage)
[![Hits](https://hits.sh/github.com/silentsoft/actlist.svg)](https://hits.sh)
[![Discord](https://img.shields.io/discord/873205203493593178?logo=discord)](https://discord.silentsoft.org/actlist)

  </div>
</h2>

Actlist will helps you to execute your desire things.

Just do focus only on coding to create what you want. and you can share with others to makes better world.

![](https://actlist.io/img/preview.png?token=90777ed1)

## Installation
Windows:
> [download exe or zip file](https://actlist.io/archives/) and launch.

Mac OS X:
> [download dmg file](https://actlist.io/archives/) and launch.

## Prerequisites For Development
* Oracle JDK 1.8 or Open JDK with JavaFx binary distribution
* JavaFx Scene Builder for graphic design

## Actlist Application Development Setup Guide
1. Fork this repository into your GitHub Account
2. Clone & import as a Maven project into your favorite IDE
3. Launch
    ```
    src/main/java/org/silentsoft/actlist/application/App.java
    ```

## Actlist Plugin Development Setup Guide
* There are two ways to create an Actlist plugin. the first one is using starter-kit(which is highly recommended) and second one is creating java project manually.

  ### 1. using starter-kit
    1. [Download starter kit](https://github.com/actlist/actlist-plugin-starter-kit/archive/master.zip)
    2. Rename `master.zip` to the desired name and unzip it
    3. Enter the directory
    4. Initialize your project metadata
       ```
       $ ./mvnw initialize -DgroupId=com.example -DartifactId=awesome-demo
       ```
       `Tip` - If you are behind a proxy server then you should use one of the following
       <details markdown="1"><summary>Details</summary>

       - Windows
         ```
         $ set MAVEN_OPTS=-Dhttps.proxyHost=10.20.30.40 -Dhttps.proxyPort=8080
         $ mvnw initialize -DgroupId=com.example -DartifactId=awesome-demo
         ```
       - Mac | Linux
         ```
         $ export MAVEN_OPTS=-Dhttps.proxyHost=10.20.30.40 -Dhttps.proxyPort=8080
         $ ./mvnw initialize -DgroupId=com.example -DartifactId=awesome-demo
         ```
       - `Note` - The proxy host `10.20.30.40` and proxy port `8080` is up to you.

       </details>
    5. Import project into your favorite IDE
  ---
  ### 2. or creating java project manually
    * Create a new Java project and configure to Maven project.
    * Add `parent` and `property` information to `pom.xml`
      ```xml
      <parent>
          <groupId>org.silentsoft</groupId>
          <artifactId>actlist-plugin-sdk</artifactId>
          <version>2.2.0</version>
      </parent>
      <properties>
          <mainClass>your.pkg.Plugin</mainClass>
      </properties>
      ```
    * Generate executable main class called `your.pkg.Plugin.java` that you assigned from `mainClass` property on `pom.xml`
    * Inherit the `ActlistPlugin` class in your `Plugin` class.
    * (Optional) to make a plugin that contains graphic things, you can write the `Plugin.fxml` file where in the same location.
    * (Optional) you can set the plugin's icon image to display on about menu (Right click > About) through `Plugin.png`. if not exists `Plugin.png` then default Actlist logo image will be displayed.
    * Done.
      
      Here is an example source code of `Plugin.java`
      ```java
      package your.pkg;
      
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
               * setPluginUpdateCheckURI(URI.create("http://your-server.name"));
               */
      
              setPluginAuthor("John Doe");
              /**
               * or you could use hyper-link via
               * setPluginAuthor("John Doe", URI.create("https://github.com/your-github-account/"));
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
              System.out.println("#initialize");
          }
      
          @Override
          public void pluginActivated() throws Exception {
              System.out.println("#pluginActivated");
          }
      
          @Override
          public void pluginDeactivated() throws Exception {
              System.out.println("#pluginDeactivated");
          }
      
      }
      ```

For more information on ActlistPlugin development, see [here](https://actlist.io/docs/quick-start/)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please note we have a [CODE_OF_CONDUCT](https://github.com/actlist/actlist/blob/master/CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## Change log
Please refer to [CHANGELOG](https://github.com/actlist/actlist/blob/master/CHANGELOG.md).

## License
Please refer to [LICENSE](https://github.com/actlist/actlist/blob/master/LICENSE.txt) and [NOTICE](https://github.com/actlist/actlist/blob/master/NOTICE.md).

## Thanks to
<a href="https://www.jetbrains.com/?from=Actlist"><img src="https://actlist.io/img/jetbrains.png" width="200"></a>
