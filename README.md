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
    <version>1.8</version>
</dependency>
```

## Java

### log4j.properties

```properties
log4j.rootLogger=INFO, kiln, stdout

log4j.appender.kiln=com.nevermindsoft.kiln.log4j.RemoteServiceAppender
log4j.appender.kiln.moduleName=Internal Testing
log4j.appender.kiln.apiKey=TEST-API-KEY
log4j.appender.kiln.environmentName=Test
log4j.appender.kiln.serverUrl=http://localhost:4444/api/events/publish
log4j.appender.kiln.maxRequestItems=200
log4j.appender.kiln.sleepTime=2000
log4j.appender.kiln.platform=JUnit
log4j.appender.kiln.maxQueueSize=1000
log4j.appender.kiln.maxStackTraceSize=2000

# Direct log messages to stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

## Grails

### BuildConfig.groovy

```groovy
dependencies {
    runtime 'com.nevermindsoft:kiln-adaptor-java:1.10'
    ....
}
```

### Config.groovy
```groovy
log4j = {
    appenders {
        ....
        appender    new com.nevermindsoft.kiln.log4j.RemoteServiceAppender(
                name:              "remote",
                moduleName:        "My Module Name",
                apiKey:            "get-key-from-kiln",
                environmentName:   grails.util.Environment.current.getName(),
                serverUrl:         "http://my.url/api/events/publish",
                maxRequestItems:   200,
                sleepTime:         5000,
                platform:          "grails",
                maxQueueSize:      1000,
                maxStackTraceSize: 2000
        )
    }
 
    ...
 
    root {
        info  'remote'
        ...
    }
}
```

## Configuration Options

* **name** - specifies the name for this appender. You will use this name as a reference in your log configuration file to specify which messages are to be logged.
* **moduleName** - specifies the name of the module we are using to produce messages. This may be removed on a future version.
* **apiKey** - the api key configured on your Kiln service. The application name will be automatically deducted from this api key.
* **environmentName** - you can specify your environment for more finely grained Kiln filtering. Typical values are production, staging, test, environment, but the value of this property can be any string.
* **serverUrl** - the url for your publish operation on your kiln service. This will typically be on the format http://[your domain or ip]/api/events/publish unless modified on the server source code.
* **maxRequestItems** - the maximum number of events to send on a single request. Used for throttling and to avoid server overload and failure due to very large requests.
* **sleepTime** - the time between requests in milliseconds. The appender will keep an internal queue of events and send them out to the server at the given interval.
* **internalLogger** - the instance of a KilnInternalLogger to be used for internal logging. The default is to use a com.nevermindsoft.kiln.internal.log.KilnConsoleLogger( Level.WARN ). See documentation for more options.
* **internalLoggerClassName** - the name of the class to be used for internal logging. This is an alternate way to specify the internal logger. Using the *internalLogger* property should be used instead whenever possible.
* **platform** - the platform name to report to the kiln server. This is used for reporting purposes. If not specified, this will default to 'Java'.
* **maxQueueSize** - this is the size of the appender's internal queue. The appender will start rejecting events after a queue reaches this size. The default value for this is 1000.
* **maxStackTraceSize** - this is the max size of stack traces to avoid sending very large requests to the server. The default value for this is unlimited.

## Changelog

* 1.11
    * Adding support for SLF4J
    * Adding unit tests
    * Improving memory utilization
        * Adding support to configure queue size
        * Adding support to configure maximum size for stack trace
        * Making more efficient use of internal memory utilization
* 1.10
    * Refactoring appender configuration into its own class
    * Adding support for specifying a platform (Java,Grails,...) with a default value of Java
    * Adding support for environment metadata and automatically adding following properties:
        * Java Version
* 1.9
    * Adding an option to specify the internal logger to use
    * Adding option to indicate how the source reference should be represented on the generated json to allow using the appender across frameworks
    * Using Apache HttpClient instead of java.net.URL to publish requests
    * Adding support for SSL connections
* 1.8 *(released)*
    * Fixed a bug with the json generated by the appender when stack traces or source were present
* 1.7 *(released)*
    * Fixed bug causing stack traces to be populated with the string "null"
