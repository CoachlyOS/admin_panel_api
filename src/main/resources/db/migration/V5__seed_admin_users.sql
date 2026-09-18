INSERT INTO administrators (username, password, role)
VALUES ('admin', '$2a$12$0KPSo/.NLcbZqNN1PWsYcuaWSZFjteHI7WQR1bAW78l9Gq0rKo03G', 'ADMIN')
    ON CONFLICT (username) DO NOTHING;
