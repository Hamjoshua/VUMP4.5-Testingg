DELETE FROM permissions;
DELETE FROM resources;
DELETE FROM users;

-- Сбрасываем автоинкрементные счетчики для H2
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE resources ALTER COLUMN id RESTART WITH 1;
ALTER TABLE permissions ALTER COLUMN id RESTART WITH 1;

-- Вставляем пользователей
INSERT INTO users (id, login, password_hash, salt, created_at) VALUES
(1, 'alice', 'PpSQFJ34GNn0FvGaxr1N+5MsrVQH29NF/kFTJ0s1EiI=', 'salt_alice', CURRENT_TIMESTAMP),
(2, 'bob', 'fRG9eJQ0l9jXLv1Z6a/dPsbfzB+U52+6QoMv6eAYi6I=', 'salt_bob', CURRENT_TIMESTAMP),
(3, 'charlie', 'CHJ8axFdX0BwBHN6gAP8TTR7QbM2ZxQ0BTV9QbN3V8E=', 'salt_charlie', CURRENT_TIMESTAMP);

-- Вставляем ресурсы (сначала родительские, потом дочерние)
-- Ресурс A (корневой, parent_id = NULL)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(1, 'A', 100, NULL, CURRENT_TIMESTAMP);

-- Ресурс X (корневой, parent_id = NULL)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(5, 'X', 200, NULL, CURRENT_TIMESTAMP);

-- Ресурс A.B (дочерний для A)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(2, 'A.B', 50, 1, CURRENT_TIMESTAMP);

-- Ресурс A.B.C (дочерний для A.B)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(3, 'A.B.C', 20, 2, CURRENT_TIMESTAMP);

-- Ресурс A.B.D (дочерний для A.B)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(4, 'A.B.D', 30, 2, CURRENT_TIMESTAMP);

-- Ресурс X.Y (дочерний для X)
INSERT INTO resources (id, path, max_volume, parent_id, created_at) VALUES
(6, 'X.Y', 75, 5, CURRENT_TIMESTAMP);

-- Вставляем разрешения (убедившись, что пользователи и ресурсы уже существуют)
INSERT INTO permissions (id, user_id, resource_id, can_read, can_write, can_execute, created_at) VALUES
-- Разрешения для alice
(1, (SELECT id FROM users WHERE login = 'alice'),
    (SELECT id FROM resources WHERE path = 'A.B'),
    1, 1, 1, CURRENT_TIMESTAMP),
(2, (SELECT id FROM users WHERE login = 'alice'),
    (SELECT id FROM resources WHERE path = 'X'),
    1, 0, 0, CURRENT_TIMESTAMP),

-- Разрешения для bob
(3, (SELECT id FROM users WHERE login = 'bob'),
    (SELECT id FROM resources WHERE path = 'A.B.C'),
    1, 0, 0, CURRENT_TIMESTAMP),
(4, (SELECT id FROM users WHERE login = 'bob'),
    (SELECT id FROM resources WHERE path = 'X.Y'),
    0, 1, 0, CURRENT_TIMESTAMP),

-- Разрешения для charlie
(5, (SELECT id FROM users WHERE login = 'charlie'),
    (SELECT id FROM resources WHERE path = 'X'),
    0, 0, 1, CURRENT_TIMESTAMP),
(6, (SELECT id FROM users WHERE login = 'charlie'),
    (SELECT id FROM resources WHERE path = 'A.B.D'),
    1, 1, 1, CURRENT_TIMESTAMP);