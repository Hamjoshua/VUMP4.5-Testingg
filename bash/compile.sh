#!/bin/sh
kotlinc $(find src -name "*.kt") $(find tests -name "*.kt") -cp "../lib/junit-platform-console-standalone.jar;../lib/kotlinx-cli-jvm-0.3.6.jar" -include-runtime -d ../compiled/app.jar