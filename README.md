kiln-java-adaptor
=================

You can find more information on Kiln, the open source platform for log aggregation, visualization and analysis, on its [GitHub page](https://github.com/rcracel/Kiln).

# Log4J Appender

If you are using Maven with Java or another JVM based languange, you can add a dependency to the Kiln adapter to your application, and usually through a simple configuration line push log events to your Kiln instance.

The project information and source code for the Log4J adaptor is currently available the [Project Page](https://github.com/rcracel/kiln-java-adaptor) on GitHub. Feel free to check it out, contribute or report bugs.

## Maven

### pom.xml

```
<dependency>
    <groupId>com.nevermindsoft</groupId>
    <artifactId>kiln-adaptor-java</artifactId>
    <version>1.4</version>
</dependency>
```

## Grails

### BuildConfig.groovy

```java
dependencies {
    runtime 'com.nevermindsoft:kiln-adaptor-java:1.4'
    ....
}
```

### Config.groovy
```java
log4j = {
    appenders {
        ....
        appender    new com.nevermindsoft.kiln.RemoteServiceAppender(
                name:            "remote",
                moduleName:      "My Module Name",
                apiKey:          "get-key-from-kiln",
                environmentName: grails.util.Environment.current.getName(),
                serverUrl:       "http://my.url/api/events/publish",
                maxRequestItems: 200,
                sleepTime:       5000
        )
    }
 
    ...
 
    root {
        info  'remote'
        ...
    }
}
```
