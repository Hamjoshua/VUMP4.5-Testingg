# План имплементации SQL

## Описание сущностей и их атрибутов

### 1. User (Пользователь)
**Атрибуты:**
- `id` (INTEGER) - первичный ключ, автоинкремент
- `login` (TEXT) - уникальный логин пользователя
- `password_hash` (TEXT) - хэш пароля с использованием SHA-256 + соль
- `salt` (TEXT) - соль для хэширования пароля

**Описание:** 
Хранит информацию о пользователях системы. Логин используется для аутентификации, пароль хранится в хэшированном виде с солью для безопасности.

### 2. Resource (Ресурс)
**Атрибуты:**
- `id` (INTEGER) - первичный ключ, автоинкремент
- `path` (TEXT) - уникальный путь ресурса в иерархической структуре
- `max_volume` (INTEGER) - максимальный объем ресурса
- `parent_id` (INTEGER) - внешний ключ на родительский ресурс (может быть NULL)

**Описание:** 
Представляет иерархическую структуру ресурсов. Путь соответствует формату "A.B.C.D", где каждая часть - имя ресурса. Родительские связи определяют наследование прав доступа.

### 3. Permission (Разрешение)
**Атрибуты:**
- `id` (INTEGER) - первичный ключ, автоинкремент
- `user_login` (TEXT) - внешний ключ на логин пользователя
- `resource_path` (TEXT) - внешний ключ на путь ресурса
- `can_read` (INTEGER) - флаг разрешения на чтение (0/1)
- `can_write` (INTEGER) - флаг разрешения на запись (0/1)
- `can_execute` (INTEGER) - флаг разрешения на выполнение (0/1)

**Описание:** 
Определяет права доступа пользователей к ресурсам. Права наследуются от родительских ресурсов к дочерним.

## ER-диаграмма базы данных

```mermaid
erDiagram
    User {
        INTEGER id PK "Первичный ключ"
        TEXT login UK "Уникальный логин"
        TEXT password_hash "Хэш пароля"
        TEXT salt "Соль для хэширования"
    }

    Resource {
        INTEGER id PK "Первичный ключ"
        TEXT path UK "Уникальный путь ресурса"
        INTEGER max_volume "Максимальный объем"
        INTEGER parent_id FK "Ссылка на родительский ресурс"
    }

    Permission {
        INTEGER id PK "Первичный ключ"
        TEXT user_login FK "Логин пользователя"
        TEXT resource_path FK "Путь ресурса"
        INTEGER can_read "Разрешение на чтение"
        INTEGER can_write "Разрешение на запись"
        INTEGER can_execute "Разрешение на выполнение"
    }

     User ||--o{ Permission : "имеет_разрешения"
    Resource ||--o{ Permission : "имеет_доступ"
    Resource ||--o| Resource : "родительская_иерархия"

    User {
        string login "Пример: alice, bob, charlie"
        string password_hash "Пример: 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
        string salt "Пример: salt_alice"
    }

    Resource {
        string path "Пример: A, A.B, A.B.C, X.Y"
        int max_volume "Пример: 100, 50, 20"
        int parent_id "NULL для корневых ресурсов"
    }

    Permission {
        string user_login "Связь с User.login"
        string resource_path "Связь с Resource.path"
        int can_read "1 - разрешено, 0 - запрещено"
        int can_write "1 - разрешено, 0 - запрещено"
        int can_execute "1 - разрешено, 0 - запрещено"
    }
```