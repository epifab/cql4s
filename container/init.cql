-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS music WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

-- Create a table
CREATE TABLE IF NOT EXISTS music.events (
    id uuid primary key,
    start_time timestamp,
    artists list<varchar>,
    venue varchar,
    prices map<varchar, decimal>,
    tags set<varchar>,
    metadata text
);

CREATE KEYSPACE IF NOT EXISTS people WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

CREATE TYPE IF NOT EXISTS people.phone (
    country_code int,
    number text,
);

CREATE TYPE IF NOT EXISTS people.address (
    street text,
    city text,
    zip text,
    phones list<frozen<phone>>
);

CREATE TABLE IF NOT EXISTS people.users (
  name text PRIMARY KEY,
  address frozen<address>
);
