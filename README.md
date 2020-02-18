# Actlist

[![release](https://actlist.silentsoft.org/api/shields/release)](http://actlist.silentsoft.org/archives/)
![platform](https://actlist.silentsoft.org/api/shields/platform)
![downloads-total](https://actlist.silentsoft.org/api/shields/downloads-total)
![usage](https://actlist.silentsoft.org/api/shields/usage)
[![Build Status](https://travis-ci.org/silentsoft/actlist.svg?branch=dev)](https://travis-ci.org/silentsoft/actlist)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=silentsoft_actlist&metric=alert_status)](https://sonarcloud.io/dashboard?id=silentsoft_actlist)
![statistics-since](https://actlist.silentsoft.org/api/shields/statistics-since)
![created](https://actlist.silentsoft.org/api/shields/created)
[![actlist-license](https://actlist.silentsoft.org/api/shields/actlist-license)](https://github.com/silentsoft/actlist/blob/master/LICENSE.txt)
[![actlist-plugin-license](https://actlist.silentsoft.org/api/shields/actlist-plugin-license)](https://github.com/silentsoft/actlist-plugin/blob/master/LICENSE.txt)
[![HitCount](http://hits.dwyl.io/silentsoft/actlist.svg)](http://hits.dwyl.io/silentsoft/actlist)
[![Gitter](https://badges.gitter.im/silentsoft/actlist.svg)](https://gitter.im/silentsoft/actlist?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> Easy and simply execute your own act list.

Actlist will helps you to execute your desire things.

Just do focus only on coding to create what you want. and you can share with others to makes better world.

![](http://actlist.silentsoft.org/img/preview.png?token=da8b296e)

## Installation
Windows:
> [download exe or zip file](http://actlist.silentsoft.org/archives/) and launch.

Mac OS X:
> [download dmg file](http://actlist.silentsoft.org/archives/) and launch.

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
    1. [Download starter kit](https://github.com/silentsoft/actlist-plugin-starter-kit/archive/master.zip)
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
    * Add `property`, `repository`, `parent` and `dependency` information to `pom.xml`
      ```
      <properties>
          <mainClass>your.pkg.Plugin</mainClass>
      </properties>
      <repositories>
          <repository>
              <id>silentsoft-repository</id>
              <url>http://nexus.silentsoft.org/repository/maven-public</url>
          </repository>
      </repositories>
      <parent>
          <groupId>org.silentsoft</groupId>
          <artifactId>actlist-plugin-sdk</artifactId>
          <version>2.0.0</version>
      </parent>
      <dependencies>
          <dependency>
              <groupId>org.silentsoft</groupId>
              <artifactId>actlist-plugin</artifactId>
              <version>1.7.1</version>
              <scope>provided</scope>
          </dependency>
      </dependencies>
      ```
    * Generate executable main class called `your.pkg.Plugin.java` that you assigned from `mainClass` property on `pom.xml`
    * Inherit the `ActlistPlugin` class in your `Plugin` class.
    * (Optional) to make a plugin that contains graphic things, you can write the `Plugin.fxml` file where in the same location.
    * (Optional) you can set the plugin's icon image to display on about menu (Right click > About) through `Plugin.png`. if not exists `Plugin.png` then default Actlist logo image will be displayed.
    * Done.
      
      Here is an example source code of `Plugin.java`
      ```
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

For more information on ActlistPlugin development, see [here](http://actlist.silentsoft.org/docs/quick-start/)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please note we have a [CODE_OF_CONDUCT](https://github.com/silentsoft/actlist/blob/master/CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## Change log
Please refer to [CHANGELOG](https://github.com/silentsoft/actlist/blob/master/CHANGELOG.md).

## License
Please refer to [LICENSE](https://github.com/silentsoft/actlist/blob/master/LICENSE.txt) and [NOTICE](https://github.com/silentsoft/actlist/blob/master/NOTICE.md).
