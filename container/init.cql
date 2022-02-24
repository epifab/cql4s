DROP KEYSPACE IF EXISTS music;
DROP KEYSPACE IF EXISTS test;

CREATE KEYSPACE music WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

CREATE TYPE music.user (
    name text,
    email text,
    phone tuple<smallint, text>
);

CREATE TYPE music.metadata (
    created_at timestamp,
    updated_at timestamp,
    author frozen<user>
);

-- Create a table
CREATE TABLE music.events (
    id uuid primary key,
    start_time timestamp,
    artists list<text>,
    venue text,
    tickets map<text, decimal>,
    tags set<text>,
    metadata frozen<metadata>
);

CREATE KEYSPACE test WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

CREATE TABLE test.dummy (
    id uuid primary key
);
