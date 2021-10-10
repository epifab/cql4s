-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS store WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : '1'
};

-- Create a table
CREATE TABLE IF NOT EXISTS store.shopping_cart (
    userid text PRIMARY KEY,
    item_count int,
    last_update_timestamp timestamp
);
