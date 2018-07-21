#!/bin/sh

if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

JAVA_OPT="-jar"

if [ -z "$JAR_FILENAME" ]; then
  JAR_FILENAME="dcp-client-example-jar-with-dependencies.jar"
fi

# Start the client
exec ./wait-for.sh $CB_HOSTNAME:8091 -- $JAVA $JAVA_OPT $JAR_FILENAME "$@"

