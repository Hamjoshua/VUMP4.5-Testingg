#!/bin/bash

set -e

# Найдём JAR (ты уже прописал путь вручную — отлично!)
KOTLINX_CLI_JAR=$(
    find ~/.gradle -type f -name "kotlinx-cli-jvm-*.jar" \
                   ! -name "*-sources.jar" \
                   ! -name "*-javadoc.jar" \
                   2>/dev/null | head -n1
)
# Проверим, что JAR существует
if [ ! -f "$KOTLINX_CLI_JAR" ]; then
    echo "JAR not found: $KOTLINX_CLI_JAR"
    exit 1
fi

echo "Compiling with kotlinx-cli from: $KOTLINX_CLI_JAR"

# Собираем все .kt файлы в массив (без tr!)
readarray -d '' KT_FILES < <(find ../src -name "*.kt" -print0)

# Компилируем — передаём массив напрямую
kotlinc -cp "$KOTLINX_CLI_JAR" "${KT_FILES[@]}" -include-runtime -d access-control.jar

echo "Success! access-control.jar created."