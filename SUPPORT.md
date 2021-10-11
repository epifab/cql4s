# Supported features

## Data types

- ascii
- bigint
- blob
- boolean
- counter
- date
- decimal
- double
- duration
- float
- inet
- int
- smallint
- text
- time
- timestamp
- timeuuid
- tinyint
- uuid
- varchar
- varint

### Collections

- map[K, V]
- list[T]
- set[T]

### Extra

- nullable[T] - optional fields
- json[T] - json encoded text


## Select

- Select a specific list of fields from a table
- Group by and aggregations are not supported yet
- Where clause is not supported yet
- Limit clause is not supported yet
- Order by clause is not supported yet

## Commands

- Insert (missing support for `IF NOT EXISTS` and `WITH TIMESTAMP`)
- Update not supported yet
- Delete not supported yet
- Truncate not supported yet
