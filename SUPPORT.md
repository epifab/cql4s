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
- [now](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#now) (_not supported_)
- [minTimeuuid, maxTimeuuid](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#mintimeuuid-and-maxtimeuuid) (_not supported_)
- [asBlob](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#blob-conversion-functions) (_not supported_)
- [user defined functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#user-defined-scalar-functions) (_not supported_)

### [Arithmetic operators](https://cassandra.apache.org/doc/latest/cassandra/cql/operators.html)

- `-`
- `+`
- `_`
- `/`
- `%` (_not supported_)

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
- toDate (_not supported_)
- toTimestamp (_not supported_)
- toUnixTimestamp (_not supported_)
- dateOf (_not supported_)
- unixTimestampOf (_not supported_)

### [Aggregate functions](https://cassandra.apache.org/doc/4.0/cassandra/cql/functions.html#aggregate-functions)
- count (_not supported_)
- sum (_not supported_)
- max (_not supported_)
- min (_not supported_)
- avg (_not supported_)
- [user defined aggregation](https://cassandra.apache.org/doc/4.0/cassandra/cql/functions.html#user-defined-aggregates-functions) (_not supported_)


## [Data manipulation](https://cassandra.apache.org/doc/latest/cassandra/cql/dml.html)

- Select
- Insert (_missing support for `IF NOT EXISTS`_)
- Update (_missing support for `IF EXISTS`_)
- Delete (_missing support for `IF EXISTS`_)
- Truncate
