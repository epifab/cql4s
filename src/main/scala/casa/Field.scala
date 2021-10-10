package casa

trait Field[T]:
  given dbType: DbType[T]
