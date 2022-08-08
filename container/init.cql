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
    id uuid primary key,
    "_ascii" ascii,
    "_bigint" bigint,
    "_blob" blob,
    "_boolean" boolean,
    -- "_counter" counter,
    "_date" date,
    "_decimal" decimal,
    "_double" double,
    -- "_duration" duration,
    "_float" float,
    "_inet" inet,
    "_int" int,
    "_smallint" smallint,
    "_text" text,
    "_time" time,
    "_timestamp" timestamp,
    "_timeuuid" timeuuid,
    "_tinyint" tinyint,
    "_uuid" uuid,
    "_varchar" varchar,
    "_varint" varint,
    "_nullableint" int
);

CREATE FUNCTION test.plusone(arg int)
    RETURNS NULL ON NULL INPUT
    RETURNS int
    LANGUAGE java
    AS $$ return arg + 1; $$;
