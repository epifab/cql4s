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
