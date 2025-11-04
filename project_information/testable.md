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
java -jar lib/junit-platform-console-standalone.jar   --class-path app.jar   --select-class org.example.AllValidatorsTest
```

## Результаты тестов (прям из консоли)
```jupyter
+-- JUnit Platform Suite [OK]
+-- JUnit Jupiter [OK]
| '-- AllValidatorsTest [OK]
|   +-- AuthValidator - valid credentials should succeed() [OK]
|   +-- PermissionValidator - no permission should fail() [OK]
|   +-- ResourceFormatValidator - invalid char in path should fail() [OK]
|   +-- AccessControlService - no permission should fail() [X] expected: <StatusCode(5)> but was: <StatusCode(6)>
|   +-- ResourceFormatValidator - empty segment should fail() [OK]
|   +-- ResourceFormatValidator - valid path A B C should succeed() [OK]
|   +-- AccessControlService - full valid request should succeed() [X] expected: <StatusCode(0)> but was: <StatusCode(6)>
|   +-- AuthValidator - invalid password should fail() [OK]
|   +-- ActionValidator - invalid action should fail() [OK]
|   +-- PermissionValidator - direct permission should succeed() [OK]
|   +-- AuthValidator - unknown user should fail() [OK]
|   +-- VolumeValidator - negative volume should fail() [OK]
|   +-- PermissionValidator - parent permission should succeed() [OK]
|   +-- VolumeValidator - valid volume should succeed() [OK]
|   +-- VolumeValidator - volume exceeding limit should fail() [OK]
|   +-- ResourceExistenceValidator - existing resource should succeed() [OK]
|   +-- ResourceExistenceValidator - non-existing resource should fail() [OK]
|   +-- AccessControlService - invalid login should fail() [X] expected: <StatusCode(3)> but was: <StatusCode(6)>
|   '-- ActionValidator - valid READ should succeed() [OK]
'-- JUnit Vintage [OK]
```