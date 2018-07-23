FROM anapsix/alpine-java

RUN mkdir /dcp-client-example
COPY entry.sh /dcp-client-example/entry.sh
COPY wait-for.sh /dcp-client-example/wait-for.sh
COPY target/dcp-client-example-jar-with-dependencies.jar /dcp-client-example/dcp-client-example-jar-with-dependencies.jar

WORKDIR /dcp-client-example
RUN chmod -R u+x /dcp-client-example/
ENV CB_HOSTNAME=couchbase-server
ENV BUCKET=travel-sample
ENV CB_USER=Admnistrator
ENV CB_PASSWORD=password
ENV MINUTES_WAITING=30
ENV IS_NOOP="false"
ENTRYPOINT "exec" "./entry.sh"
