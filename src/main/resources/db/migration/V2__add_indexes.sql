CREATE INDEX idx_users_login ON users(login);
CREATE INDEX idx_resources_path ON resources(path);
CREATE INDEX idx_resources_parent ON resources(parent_id);
CREATE INDEX idx_permissions_user ON permissions(user_id);
CREATE INDEX idx_permissions_resource ON permissions(resource_id);