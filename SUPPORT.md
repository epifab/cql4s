# Supported features

## Data types

### [Native types](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#native-types)

- ascii
- bigint
- blob
- boolean
- counter
- date
- decimal
- double
- duration (_not supported_)
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

### [Collections](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#collections)

- map[K, V]
- list[T]
- set[T]

### Extra

- [user defined types](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#udts) - `udt[Keyspace, Name, Components]`
- nullable[T] - optional fields
- json[T] - json encoded text (supported by `cql4s-circe`)
- [tuples](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#tuples)


## Functions

- [cast](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#cast)
- [token](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#token) (_not supported_)
- [uuid](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#uuid) (_not supported_)
- [asBlob](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#blob-conversion-functions) (_not supported_)
- [user defined functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#user-defined-scalar-functions)

### [Arithmetic operators](https://cassandra.apache.org/doc/latest/cassandra/cql/operators.html)

- `-`
- `+`
- `*`
- `/`
- `%`

### [Datetime arithmetic](https://cassandra.apache.org/doc/latest/cassandra/cql/operators.html#datetime--arithmetic)

- `+` (_not supported_)
- `-` (_not supported_)

### [Datetime functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#datetime-functions)
- currentTimestamp
- currentDate
- currentTime
- currentTimeUUID, now
- minTimeuuid, maxTimeuuid

### [Time conversion functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#time-conversion-functions)
- toDate
- toTimestamp
- toUnixTimestamp

### [Aggregate functions](https://cassandra.apache.org/doc/4.0/cassandra/cql/functions.html#aggregate-functions)
- count
- sum
- max
- min
- avg
- [user defined aggregation](https://cassandra.apache.org/doc/4.0/cassandra/cql/functions.html#user-defined-aggregates-functions) (_not supported_)


## [Data manipulation](https://cassandra.apache.org/doc/latest/cassandra/cql/dml.html)

- Select
- Insert
- Update (_missing support for `IF EXISTS`_)
- Delete (_missing support for `IF EXISTS`_)
- Truncate
