# Java Spring MVC Web App with Spring Cloud Config Client and NO SPRINGBOOT

We all know how difficult is to change the configuration of our apps and services in the middle of Production panic attacks :) Configuration management has become much simpler with Spring Cloud Config ecosystem, which allows dynamic configuration management for any service and clients, from backend to frontend. However, being mostly a Java shop, it has been really hard to onbonard customers due to the fact that they are required to migrate to SpringBoot. Therefore, it was really important to us to find a way around and easy customers adoption.

This is based on the discussions of https://github.com/spring-cloud/spring-cloud-config/issues/299.

# Why this Sample

The reason why I decided to invest some time to make this reference example a reality was, by no means, to go against the SpringBoot's adoption. As a big fan, I'd like to help those who doesn't want to do a full sprint from the beginning, but would like it to be a "crawl-walk-run" one. That is:

* You need dynamic configuration management from reading the magic from https://cloud.spring.io/spring-cloud-config/ and https://spring.io/guides/gs/centralized-configuration/.
* You LOVE SpringBoot, but some of your services are STILL using JavaEE, or even in SpringFramework, but you don't have the time, money, budget, etc to make a full migration.
* You work in a Platform team but you find resistence from your customers to adopt Spring Cloud Config based on the requirements of SpringBoot migration.
* You want others, less fortunate souls, to come and embrace configuration management before embracing the full SpringBoot framework.

> NOTE: 
> * This example uses the original SpringCloud Config client without any changes.
> * It does require you to add a couple of changes to your application, as described at https://github.com/spring-cloud/spring-cloud-config/issues/299


# How to use Spring Cloud Config Client in Non-SpringBoot Web Applications?

> NOTE: 
> * The solution to make this possible is described at https://github.com/spring-cloud/spring-cloud-config/issues/299#issuecomment-293990546. 
> * The steps below are from the PR https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1 to show the integration into an existing application.

* Using a compatible Spring Framework to the Spring Cloud Config Client you want to use requires verifying their compatibility.
* This solution involves changing the file `web.xml` and creating at least 3 classes to be added to your application.

1. Add the dependency to the library to your application using the `pom.xml` as shown at https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/1bf65f128f58f59111449dc13aa6b5e5f0eccb54
 * Optionally, add the dependency to `Run Jetty Run` to run the application in Eclipse, just like SpringBoot apps. https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/bb395ae9b0b990a83a085db4398d247dd8419547
 
2. Create an ApplicationContext that uses the Spring Cloud Config Client as a resource and update the `web.xml` with it.
 * https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/3b5f5e8604c7335e4c2f5eda98a4d2563b281abf

3. Create your POJO classes that represents a couple or multiple properties using `@Value` or `@ConfigurationProperties` annotations. 
 * Use the @ConfigurationProperties to better plan your properties and organize them following a common business logic. I usually create `*Properties` classes where `Properties` help me identify that it's a POJO for configuration. 
 * Only use @Value when you need a reference of a property alone.
 * Very useful when taking advantage of the `@RefreshScope` capabilities of Spring Cloud Config http://cloud.spring.io/spring-cloud-static/docs/1.0.x/spring-cloud.html#_refresh_scope
 
4. Create the `@EnableConfigurationProperties` class instantiates the bean that's responsible for resolving classes with `@Value` and `@ConfigurationProperties`. 
 * Note that this class MUST list all the Beans with @ConfigurationProperties
 * https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/7d64e413b2ceb019e5ef38485f5acd45a5cdc699

5. Autowire the new Properties Beans into an existing `@Controller` or `@Service`. 
 * https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/c15f54504dff4f629f8201f8514c78e885fd3d2b

6. Add the Log level for the Spring Cloud Config Client to at least `INFO` and see what it is up to! 
 * https://github.com/marcellodesales/spring-mvc-helloworld-spring-cloud-config-client/pull/1/commits/454d8350d42377b2ed8f1efdfa2daf6e37e7496f
 
7. Enjoy! 

# Setup and Tools

Here's what I use to get very close to SpringBoot's amazing set of commands.

## Run Jetty Run

* For very quick verification, I recommend installing the Jetty Eclipse Runner to quickly run and debug this application just like using SpringBoot's DevTools!
* Follow the instructions from https://github.com/xzer/run-jetty-run#install.
 * You will be running your application as quickly as SpringBoot apps from the comfort of Eclipse :)
 * No need to deploy the app in Tomcat or other App Server

[![Run Jetty Run](https://img.youtube.com/vi/Dtj1YBy9LKw/0.jpg)](https://www.youtube.com/watch?v=Dtj1YBy9LKw)

* If you prefer to mimic SpringBoot in the command-line level, you can use http://www.eclipse.org/jetty/documentation/9.4.x/jetty-maven-plugin.html.

Before you run, make sure you execute the following:

```java
$ mvn eclipse:eclipse
```

## Docker

* The repo originally comes with a Dockerfile. You can quickly build the application and run it inside a container.

```java
$ mvn package
$ docker build -t reference .
$ docker run -ti -p 8080:8080 reference
```

However, it is easier to just run it using Jetty.

# Running this app

> This method requires `Spring Framework 4.2.5.RELEASE` at minimum.
> You must provide the environment variables to your application instead of setting the values.

You will run the following:

* The Spring Cloud Config Server loading files from your local directory for the quick check.
* Run the application to load the properties from it.

## Running the Config Server

You can use the Docker image https://hub.docker.com/r/hyness/spring-cloud-config-server/ to quickly run the config server loading property files from the local directory. I'm providing the `docker-compose` with it, loading from the local directory indicated (my personal machine) or yours provided with the environment variable `SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS`.

```yml
$ cat config-server-local-directory.yml 
version: '2.1'

services:
  configServer:
    image: hyness/spring-cloud-config-server
    container_name: config-server
    environment:
      SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS: file:/local-development
      SPRING_PROFILES_ACTIVE: native 
    volumes:
      - ${SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS:-/home/mdesales/dev/github/spring-cloud-config-publisher-config}:/local-development
    ports:
      - 8888:8888

volumes:
  "config-server":
```

Starting the config server can be done with the following:

```
$ docker-compose up
```

You can verify it by calling the following:

* `curl localhost:8888/publisher/qal/develop | jq` will show the current configuration from your running server, where:
 * `SPRING_APPLICATION_NAME` = publisher
 * `SPRING_PROFILES_ACTIVE` = qal 
 * `SPRING_CLOUD_CONFIG_LABEL = develop

Here's the example calling it and inspecting the output of the Metadata API. This application requires at least `github.cloneBaseDir` and `github.cloneBaseDir`. 

> ATTENTION: Note that `develop` is not applicable in this example since it's NOT a github repo.

```json
{
  "name": "publisher",
  "profiles": [
    "qal"
  ],
  "label": "develop",
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "file:/local-development/publisher-qal.yml",
      "source": {
        "github.cloneBaseDir": "/app/spring-cloud-config-publisher/git_basedir",
        "publisher.s3.cacheControl": "private; max-age: 61; s-max-age: 61"
      }
    },
    {
      "name": "file:/local-development/publisher.properties",
      "source": {
        "github.cloneBaseDir": "/tmp",
        "github.token": "f4d24eed3909ad8766e29fd2500e80cb33d153a8"
     }
  ]
}
```

## Running the Application

Make sure that there's a configuration service running at `SPRING_CLOUD_CONFIG_URI`, or it will try fetching configuration at http://localhost:8888. Converting the same properties defined above to your application, use the convention described by Spring Cloud Config.

```sh
$ SPRING_APPLICATION_NAME=publisher SPRING_PROFILES_ACTIVE=qal SPRING_CLOUD_CONFIG_LABEL=develop mvn jetty:run
```

The following output is for the current version. Everything runs just like SpringBoot! If the application prints `Github Properties are` and `My Properties are`, with values associated with them, then it's a success! They are using Spring Cloud Config and Spring Framework to load properties using `@Value("${property.name}")` and `@ConfigurationProperty(prefix = "property.prefix")` classes with magical powers.

```java
$ SPRING_APPLICATION_NAME=publisher SPRING_PROFILES_ACTIVE=qal SPRING_CLOUD_CONFIG_LABEL=develop mvn jetty:run         
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building HelloWorld Maven Webapp 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] >>> jetty-maven-plugin:9.4.3.v20170317:run (default-cli) > test-compile @ HelloWorld >>>
[INFO] 
[INFO] --- maven-resources-plugin:2.3:resources (default-resources) @ HelloWorld ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 2 resources
[INFO] 
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ HelloWorld ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:2.3:testResources (default-testResources) @ HelloWorld ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ HelloWorld ---
[INFO] No sources to compile
[INFO] 
[INFO] <<< jetty-maven-plugin:9.4.3.v20170317:run (default-cli) < test-compile @ HelloWorld <<<
[INFO] 
[INFO] --- jetty-maven-plugin:9.4.3.v20170317:run (default-cli) @ HelloWorld ---
[INFO] Configuring Jetty for project: HelloWorld Maven Webapp
[INFO] webAppSourceDirectory not set. Trying src/main/webapp
[INFO] Reload Mechanic: automatic
[INFO] Classes = /home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/target/classes
[INFO] Logging initialized @2059ms to org.eclipse.jetty.util.log.Slf4jLog
[INFO] Context path = /
[INFO] Tmp directory = /home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/target/tmp
[INFO] Web defaults = org/eclipse/jetty/webapp/webdefault.xml
[INFO] Web overrides =  none
[INFO] web.xml file = file:///home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/src/main/webapp/WEB-INF/web.xml
[INFO] Webapp directory = /home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/src/main/webapp
[INFO] jetty-9.4.3.v20170317
[INFO] Scanning elapsed time=1200ms
[INFO] 1 Spring WebApplicationInitializers detected on classpath
[INFO] DefaultSessionIdManager workerName=node0
[INFO] No SessionScavenger set, using defaults
[INFO] Scavenging every 660000ms
23:46:13,390 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
23:46:13,390 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
23:46:13,390 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/target/classes/logback.xml]
23:46:13,439 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - debug attribute not set
23:46:13,442 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
23:46:13,451 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [STDOUT]
23:46:13,505 |-WARN in ch.qos.logback.core.ConsoleAppender[STDOUT] - This appender no longer admits a layout as a sub-component, set an encoder instead.
23:46:13,505 |-WARN in ch.qos.logback.core.ConsoleAppender[STDOUT] - To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.
23:46:13,505 |-WARN in ch.qos.logback.core.ConsoleAppender[STDOUT] - See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details
23:46:13,506 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [com.zenika.controller] to DEBUG
23:46:13,506 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [com.zenika.controller] to false
23:46:13,506 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [STDOUT] to Logger[com.zenika.controller]
23:46:13,507 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [org.springframework.cloud.config.client] to DEBUG
23:46:13,507 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [org.springframework.cloud.config.client] to false
23:46:13,507 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [STDOUT] to Logger[org.springframework.cloud.config.client]
23:46:13,507 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to ERROR
23:46:13,507 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [STDOUT] to Logger[ROOT]
23:46:13,507 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
23:46:13,508 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@4ee5b2d9 - Registering current configuration as safe fallback point

[INFO] Initializing Spring root WebApplicationContext
##################### loaded my comfigurable context
2017-04-13 23:46:13 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [servletConfigInitParams] PropertySource with lowest search precedence
2017-04-13 23:46:13 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [servletContextInitParams] PropertySource with lowest search precedence
2017-04-13 23:46:13 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [jndiProperties] PropertySource with lowest search precedence
2017-04-13 23:46:13 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [systemProperties] PropertySource with lowest search precedence
2017-04-13 23:46:13 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [systemEnvironment] PropertySource with lowest search precedence
##################### will load the client configuration
ConfigClientProperties [enabled=true, profile=qal, name=publisher, label=master, username=null, password=null, uri=http://localhost:8888, authorization=null, discovery.enabled=false, failFast=false, token=null]
2017-04-13 23:46:14 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at: http://localhost:8888
2017-04-13 23:46:15 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=publisher, profiles=[qal], label=develop, version=null, state=null
2017-04-13 23:46:15 [main] DEBUG c.z.c.config.CloudEnvironment - Adding [configService] PropertySource with lowest search precedence
2017-04-13 23:46:15 [main] DEBUG c.z.c.config.CloudEnvironment - Initialized CloudEnvironment with PropertySources [servletConfigInitParams,servletContextInitParams,jndiProperties,systemProperties,systemEnvironment,configService]
2017-04-13 23:46:15 [main] DEBUG c.z.c.config.CloudEnvironment - Replacing [servletContextInitParams] PropertySource with [servletContextInitParams]
2017-04-13 23:46:15 [main] INFO  c.z.c.c.SpringCloudConfigContext - Refreshing Root WebApplicationContext: startup date [Thu Apr 13 23:46:15 PDT 2017]; root of context hierarchy
2017-04-13 23:46:15 [main] DEBUG c.z.c.c.SpringCloudConfigContext - Bean factory for Root WebApplicationContext: org.springframework.beans.factory.support.DefaultListableBeanFactory@163042ea: defining beans [propertiesConfigurer,baseController,myPropeties,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,org.springframework.context.event.internalEventListenerProcessor,org.springframework.context.event.internalEventListenerFactory,org.springframework.web.servlet.view.InternalResourceViewResolver#0]; root of factory hierarchy
2017-04-13 23:46:15 [main] DEBUG c.z.c.c.SpringCloudConfigContext - Unable to locate MessageSource with name 'messageSource': using default [org.springframework.context.support.DelegatingMessageSource@429aeac1]
2017-04-13 23:46:15 [main] DEBUG c.z.c.c.SpringCloudConfigContext - Unable to locate ApplicationEventMulticaster with name 'applicationEventMulticaster': using default [org.springframework.context.event.SimpleApplicationEventMulticaster@e91b4f4]
Github Properties are f4d24eed3909ad8766e29fd2500e80cb33d153a8 with /app/spring-cloud-config-publisher/git_basedir
My Properties are: f4d24eed3909ad8766e29fd2500e80cb33d153a8 with /app/spring-cloud-config-publisher/git_basedir
2017-04-13 23:46:15 [main] DEBUG c.z.c.c.SpringCloudConfigContext - Unable to locate LifecycleProcessor with name 'lifecycleProcessor': using default [org.springframework.context.support.DefaultLifecycleProcessor@5f366587]
[INFO] Initializing Spring FrameworkServlet 'mvc-dispatcher'
Github Properties are f4d24eed3909ad8766e29fd2500e80cb33d153a8 with /app/spring-cloud-config-publisher/git_basedir
My Properties are: f4d24eed3909ad8766e29fd2500e80cb33d153a8 with /app/spring-cloud-config-publisher/git_basedir
[INFO] Started o.e.j.m.p.JettyWebAppContext@6df7988f{/,file:///home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/src/main/webapp/,AVAILABLE}{file:///home/mdesales/dev/github/intuit/servicesplatform-tools/spring-cloud-config-reference-springframework-app/src/main/webapp/}
[INFO] Started ServerConnector@2efd2f21{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
[INFO] Started @7077ms
[INFO] Started Jetty Server
```
