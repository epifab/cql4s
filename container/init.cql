-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS music WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

CREATE TYPE IF NOT EXISTS music.user (
    name text,
    email text
);

CREATE TYPE IF NOT EXISTS music.metadata (
    created_at timestamp,
    updated_at timestamp,
    author frozen<user>
);

-- Create a table
CREATE TABLE IF NOT EXISTS music.events (
    id uuid primary key,
    start_time timestamp,
    artists list<varchar>,
    venue varchar,
    tickets map<varchar, decimal>,
    tags set<varchar>,
    metadata frozen<metadata>
);
