#!/bin/bash
set -e

mkdir -p lib

# Собираем все maven-id из .idea/libraries/*.xml
grep -hPo '(?<=maven-id=")[^"]*' .idea/libraries/*.xml | sort -u > /tmp/maven-deps.txt

echo "Зависимости для скачивания:"
cat /tmp/maven-deps.txt

while IFS= read -r dep; do
  if [[ -z "$dep" ]]; then continue; fi

  IFS=':' read -r group artifact version <<< "$dep"
  # Заменяем '.' и ':' в group на '/'
  group_path="${group//.//}"

  # URL на .jar в Maven Central
  url="https://repo1.maven.org/maven2/$group_path/$artifact/$version/${artifact}-${version}.jar"

  target="lib/${artifact}-${version}.jar"
  echo "Скачиваю: $url -> $target"
  curl -s -L "$url" -o "$target"
done < /tmp/maven-deps.txt

echo "Все зависимости загружены в lib/"