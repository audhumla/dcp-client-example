# Dcp client example

This project has been created in order to reproduce the java-dcp-client issue described [here](https://issues.couchbase.com/projects/JDCP/issues/JDCP-97?filter=allopenissues).

## Compilation

You can compile the project `dcp-client-example` - if you have maven installed - just typing:

    mvn clean install package

If you would like to import it in eclipse:

    mvn install eclipse:eclipse

## How to run it

### How to test it using Docker-compose

* Once the compilation has been done, modify the environment variables for the service 'dcp-client-example' in the docker-compose.yml according to your setup.
* `docker-compose build`
* In the home directory of the project, type: `docker-compose up -d`
* If it is the first time you're running it:
  * setup couchbase via [web ui](http://localhost:8091)
  * import the 'travel-sample' bucket
  * restart the dcp-client-example service: `docker-compose up -d`
* Perform an update on couchbase via web-ui and observe the sysout of the update in the dcp-client-example
* Restart the couchbase-server: `docker stop couchbase-server; sleep 10s; docker start couchbase-server`
* Wait for the dcp-client-example to reconnect
* Perform an update on couchbase via web-ui and observe the sysout of the update in the dcp-client-example
