package cql4s

trait Field[T]:
  given dbType: DbType[T]
