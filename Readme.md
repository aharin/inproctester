# In-process web application tester

Inproctester provides HtmlUnit and WebDriver extensions to enable testing of a web application in-processs.
It simulates a j2ee servlet container to enable a standard j2ee web application to run within a test process.
Inproctester uses jetty behind the scenes, however it allows to dispatch requests to the web application under test directly, bypassing any socket and http layer.

# Note on jersey
Inproctester provides a jersey-client implementation that allows requests to be dispatched directly to a jersey-based web service packaged as a j2ee web application.
However jersey could also be used independently from a servlet container. E.g. it could be deployed with simpleweb [http://www.simpleframework.org].
Jersey-based web services are testable in memory out of the box, using jersey-test framework without the need to simulate a j2ee web environment [http://jersey.java.net/nonav/documentation/latest/test-framework.html].

The standard jersey-test framework does not however provide a WebDriver interface that would allow to test html if jersey is used for html generation.
The sibling jerseytester project [https://github.com/aharin/jerseytester] provides such WebDriver extensions for jersey-test framework.

## Contributors

* [http://github.com/aharin](Alex Harin)

## Binaries

The latest release of inproctester is 1.0.14, released on 29-Aug-2012. The compiled binaries are available via central maven repository.

    <dependency>
        <groupId>com.thoughtworks.inproctester</groupId>
        <artifactId>inproctester-webdriver</artifactId>
        <version>1.0.14</version>
    </dependency>

## Licence

Inprocess web application tester is distributed under the terms of Apache License, Version 2.0: [http://www.apache.org/licenses/LICENSE-2.0.html]