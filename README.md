<h1 align="center">
  <img src="https://github.com/actlist/actlist/blob/dev/src/main/resources/images/icon/actlist_64.png?raw=true"></br>
  Actlist
</h1>
<h2 align="center">
  <sup>
    <h5>Execute your own action list easily and simply</h5>
  </sup>
  <div>

[![release](https://shields.io/github/v/release/actlist/actlist?display_name=tag)](https://actlist.io/archives/)
[![Build Status](https://app.travis-ci.com/actlist/actlist.svg?branch=dev)](https://app.travis-ci.com/actlist/actlist)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fsilentsoft%2Factlist.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fsilentsoft%2Factlist?ref=badge_shield)
![downloads-total](https://actlist.silentsoft.org/api/shields/downloads-total)
![usage](https://actlist.silentsoft.org/api/shields/usage)
[![Hits](https://hits.sh/github.com/silentsoft/actlist.svg)](https://hits.sh/github.com/silentsoft/actlist/)
[![Discord](https://img.shields.io/discord/873205203493593178?logo=discord)](https://discord.silentsoft.org/actlist)

  </div>
</h2>

![](https://actlist.io/img/preview.png?token=90777ed1)

## Installation
Windows:
> [download exe or zip file](https://actlist.io/archives/) and launch.

macOS:
> [download dmg file](https://actlist.io/archives/) and launch.

## Documentation
* https://actlist.io/docs/quick-start/

## Prerequisites For Development
* [OpenJDK 1.8 with JavaFx binary distribution](https://www.azul.com/downloads/?version=java-8-lts&package=jdk-fx#download-openjdk)
* [Scene Builder](https://gluonhq.com/products/scene-builder/#download) for graphic design

## Actlist Application Development Setup Guide
1. Fork this repository into your GitHub Account
2. Clone & import as a Maven project into your favorite IDE
3. Launch
   ```
   src/main/java/org/silentsoft/actlist/application/App.java
   ```

## Actlist Plugin Development Setup Guide
There are two ways to create an Actlist plugin. the first one is using starter-kit(which is highly recommended) and the second one is creating Maven project using Maven archetype.

### Getting started with GitHub
1. [Generate repository](https://github.com/actlist/actlist-plugin-starter-kit/generate)
2. Clone & import as a Maven project into your favorite IDE
   - If you are using `IntelliJ IDEA`, you need to enable `Add dependencies with "provided" scope to classpath` in the Run Configuration dialog
4. Launch
   ```
   src/main/java/sample/Plugin.java
   ```

### Getting started with Maven
1. Execute following command:
   ```shell
   $ mvn archetype:generate \
         -DarchetypeGroupId=org.silentsoft \
         -DarchetypeArtifactId=actlist-plugin-archetype \
         -DarchetypeVersion=1.0.0 \
         -Dactlist-plugin-sdk-version=2.2.0 \
         -DgroupId=sample \
         -DartifactId=starter \
         -Dversion=1.0.0
   ```
   `Tip` - If you are behind a proxy server then you should use one of the following
   <details markdown="1"><summary>Details</summary>

   - Windows
     ```shell
     $ set MAVEN_OPTS=-Dhttps.proxyHost=10.20.30.40 -Dhttps.proxyPort=8080
     ```
   - macOS
     ```shell
     $ export MAVEN_OPTS=-Dhttps.proxyHost=10.20.30.40 -Dhttps.proxyPort=8080
     ```
   - `Note` - The proxy host `10.20.30.40` and proxy port `8080` is up to you.

   </details>
2. Import as a Maven project into your favorite IDE
   - If you are using `IntelliJ IDEA`, you need to enable `Add dependencies with "provided" scope to classpath` in the Run Configuration dialog
4. Launch
   ```
   src/main/java/sample/Plugin.java
   ```

## Packaging
```shell
$ mvn clean package
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please note we have a [CODE_OF_CONDUCT](https://github.com/actlist/actlist/blob/master/CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## Change log
Please refer to [CHANGELOG](https://github.com/actlist/actlist/blob/master/CHANGELOG.md).

## License
Please refer to [LICENSE](https://github.com/actlist/actlist/blob/master/LICENSE.txt) and [NOTICE](https://github.com/actlist/actlist/blob/master/NOTICE.md).

## Thanks to
<a href="https://www.jetbrains.com/?from=Actlist"><img src="https://actlist.io/img/jetbrains.png" width="200"></a>
