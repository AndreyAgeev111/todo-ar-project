CREATE DATABASE ar-project;

-- Users credentials
CREATE TABLE users_credentials (
                           login text NOT NULL,
                           PRIMARY KEY (login),
                           pass text NOT NULL
);

-- Users
CREATE TABLE users (
                        user_login text NOT NULL,
                        PRIMARY KEY (user_login),
                        first_name text NULL,
                        second_name text NULL,
                        email text NULL
);

ALTER TABLE users
    ADD FOREIGN KEY (user_login) REFERENCES users_credentials (login) ;

-- User's tasks
CREATE TABLE user_tasks (
                            id text NOT NULL,
                            PRIMARY KEY (id),
                            user_login text NOT NULL,
                            name text NOT NULL ,
                            description text NULL,
                            created_at timestamp with time zone NOT NULL,
                            deadline timestamp with time zone NOT NULL,
                            status text NOT NULL
);

ALTER TABLE user_tasks
    ADD FOREIGN KEY (user_login) REFERENCES users_credentials (login);