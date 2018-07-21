FROM anapsix/alpine-java

RUN mkdir /dcp-client-example
COPY entry.sh /dcp-client-example/entry.sh
COPY wait-for.sh /dcp-client-example/wait-for.sh
COPY target/dcp-client-example-jar-with-dependencies.jar /dcp-client-example/dcp-client-example-jar-with-dependencies.jar

WORKDIR /dcp-client-example
RUN chmod -R u+x /dcp-client-example/
ENTRYPOINT "exec" "./entry.sh"
