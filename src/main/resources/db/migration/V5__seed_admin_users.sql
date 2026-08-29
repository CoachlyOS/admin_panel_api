INSERT INTO administrators (username, password, role)
VALUES ('admin', '$2a$12$ZCUQ1zZmhTKH1rMQmkCc0.eQNb3LZUptfHWq8fUNZMXQlu.9d1a02', 'admin')
    ON CONFLICT (username) DO NOTHING;
