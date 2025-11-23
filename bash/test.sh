#!/bin/sh

JUNIT_JAR=$(find lib -name "junit-platform-console-standalone.jar" | head -n1)

java -jar "$JUNIT_JAR" \
    --class-path access-control.jar \
    --select-class org.example.AllValidatorsTest