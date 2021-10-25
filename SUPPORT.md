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

### [Collections](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#collections)

- map[K, V]
- list[T]
- set[T]

### Extra

- [user defined types](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#udts) - `udt[Keyspace, Name, Components]`
- nullable[T] - optional fields
- json[T] - json encoded text (supported by `cql4s-circe`)
- [tuples](https://cassandra.apache.org/doc/latest/cassandra/cql/types.html#tuples)

### [Operators](https://cassandra.apache.org/doc/latest/cassandra/cql/operators.html) (none supported)

- `-`
- `+`
- `*`
- `/`
- `%`

### Functions (none supported)

- [cast](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#cast)
- [token](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#token)
- [uuid](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#uuid)
- [now](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#now)
- [minTimeuuid, maxTimeuuid](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#mintimeuuid-and-maxtimeuuid)
- [asBlob](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#blob-conversion-functions)
- [user defined functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#user-defined-scalar-functions)

#### [Datetime functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#datetime-functions)
- currentTimestamp
- currentDate
- currentTime
- currentTimeUUID

#### [Time conversion functions](https://cassandra.apache.org/doc/latest/cassandra/cql/functions.html#time-conversion-functions)
- toDate
- toTimestamp
- toUnixTimestamp
- dateOf
- unixTimestampOf


## [Data manipulation](https://cassandra.apache.org/doc/latest/cassandra/cql/dml.html)

- Select
- Insert (missing support for `IF NOT EXISTS`)
- Update (missing support for `IF EXISTS`)
- Delete (missing support for `IF EXISTS`)
- Truncate
