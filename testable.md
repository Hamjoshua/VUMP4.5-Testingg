# 4.5 - Тестирование
Снизу будут представлены классы, а под ними - фукнции для тестирования

## Тестируемые объекты
*Валидаторы*
- ActionValidator 
- -
- AuthValidator
- -
- PermissionValidator
- -
- ResourceExistenceValidator
- -
- ResourceFormatValidator
- -
- VolumeValidator
- -

*Цепочка ответственности*
- AccessControlService
- -

## Нетестируемые объекты
- main(). Зависит от ввода аргументов
- MockData. Используется для данных
- Сущности. Используется для данных
- Парсинг аргументов в main(). Зависит от сторонней библиотеки, что нежелательно

## Инструкция по запуску
Сначала нужно скомпилировать `app.jar`

```bash
 kotlinc $(find src -name "*.kt") $(find tests -name "*.kt") -cp "lib/junit-platform-console-standalone.jar;lib/kotlinx-cli-jvm-0.3.6.jar" -include-runtime -d app.jar
```

Потом запустить `app.jar`

```bash
java -jar lib/junit-platform-console-standalone.jar --class-path app.jar --select-class AllValidatorsClass
```

## Результаты тестов