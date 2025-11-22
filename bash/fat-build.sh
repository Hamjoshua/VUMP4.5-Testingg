#!/bin/bash

set -e

# --- Найти jar через java.home ---
JAVA_HOME_DIR=$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | cut -d'=' -f2 | sed 's#\\#/#g' |  sed 's/^[[:space:]]*//; s/[[:space:]]*$//' )
if [ -z "$JAVA_HOME_DIR" ]; then
    echo "Error: Cannot determine java.home. Is Java installed?"
    exit 1
fi
echo "installed at '${JAVA_HOME_DIR}'"

if [ "$(uname -o 2>/dev/null)" = "Msys" ] || [[ "$(uname -s 2>/dev/null)" == *NT* ]]; then

    JAR_CMD="${JAVA_HOME_DIR}/bin/jar.exe"
else
    JAR_CMD="${JAVA_HOME_DIR}/bin/jar"
fi

if [ ! -e "$JAR_CMD" ]; then
    echo "Error: jar not found at '${JAR_CMD}'. Install JDK (not just JRE)."
    # exit 1
fi
# --- конец определения jar ---

# Найти kotlinx-cli
KOTLINX_CLI_JVM_JAR=$(
    find lib -type f -name "kotlinx-cli-jvm-*.jar" \
                   ! -name "*-sources.jar" \
                   ! -name "*-javadoc.jar" \
                   2>/dev/null | head -n1
)

if [ ! -f "$KOTLINX_CLI_JVM_JAR" ]; then
    echo "JAR not found: $KOTLINX_CLI_JVM_JAR"
    exit 1
fi

# Компиляция
mkdir -p build/classes
readarray -d '' KT_FILES < <(find src -name "*.kt" -print0)
kotlinc -cp "$KOTLINX_CLI_JVM_JAR" "${KT_FILES[@]}" -d build/classes

# Манифест
echo "Main-Class: org.example.MainKt" > build/manifest.txt

# Подготовка fat-директории
mkdir -p build/fat
cp -r build/classes/* build/fat/
unzip -q -o "$KOTLINX_CLI_JVM_JAR" -d build/fat/

# Сборка JAR
"$JAR_CMD" cfm access-control.jar build/manifest.txt -C build/fat .

echo "✅ Fat JAR created: access-control.jar"