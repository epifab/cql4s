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
- duration (not supported)
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

- udt[Keyspace, Name, Components] - user defined type
- nullable[T] - optional fields
- json[T] - json encoded text


## DML

- Select
- Insert (missing support for `IF NOT EXISTS`)
- Update (missing support for `IF EXISTS`)
- Delete not supported yet
- Truncate
