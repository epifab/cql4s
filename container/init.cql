-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS music WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

-- Create a table
CREATE TABLE IF NOT EXISTS music.events (
    id uuid,
    artists list<varchar>,
    venue varchar,
    start_time timestamp,
    primary key (id, venue)
);
