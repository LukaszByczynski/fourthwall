CREATE DATABASE movieslistingdb;
CREATE DATABASE cinemamanagerdb;
CREATE DATABASE eventbusdb;

CREATE USER cinemamanager;
CREATE USER movieslisting;
CREATE USER eventbus;

GRANT ALL PRIVILEGES ON DATABASE cinemamanagerdb TO cinemamanager;
GRANT ALL PRIVILEGES ON DATABASE movieslistingdb TO movieslisting;
GRANT ALL PRIVILEGES ON DATABASE eventbusdb TO eventbus;

-- For movieslisting user
\c movieslistingdb;
GRANT USAGE ON SCHEMA public TO movieslisting;
GRANT CREATE ON SCHEMA public TO movieslisting;

-- For cinemamanager user
\c cinemamanagerdb;
GRANT USAGE ON SCHEMA public TO cinemamanager;
GRANT CREATE ON SCHEMA public TO cinemamanager;

-- For eventbus user
\c eventbusdb;
GRANT USAGE ON SCHEMA public TO eventbus;
GRANT CREATE ON SCHEMA public TO eventbus;