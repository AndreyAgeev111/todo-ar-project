-- Users credentials
CREATE TABLE IF NOT EXISTS users_credentials (
                           login text NOT NULL,
                           PRIMARY KEY (login),
                           pass text NOT NULL
);

-- Users
CREATE TABLE IF NOT EXISTS users (
                        user_login text NOT NULL,
                        PRIMARY KEY (user_login),
                        first_name text NULL,
                        second_name text NULL,
                        email text NULL
);

ALTER TABLE IF EXISTS users
    ADD FOREIGN KEY (user_login) REFERENCES users_credentials (login) ;

-- User's tasks
CREATE TYPE task_status AS ENUM ('ToDo', 'InProgress', 'Done');

CREATE TABLE IF NOT EXISTS user_tasks (
                            id text NOT NULL,
                            PRIMARY KEY (id),
                            user_login text NOT NULL,
                            name text NOT NULL ,
                            description text NULL,
                            created_at timestamp with time zone NOT NULL,
                            deadline timestamp with time zone NOT NULL,
                            status task_status NOT NULL
);

ALTER TABLE IF EXISTS user_tasks
    ADD FOREIGN KEY (user_login) REFERENCES users_credentials (login);