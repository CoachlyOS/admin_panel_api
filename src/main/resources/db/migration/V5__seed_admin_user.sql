INSERT INTO users (username, password, email)
VALUES ('admin', '$2a$12$cLCPGK3A68Bgj44fFt0ri.BDK/tIDXJw1no2OOEK49w.5s8UsgStS', 'admin@example.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;
