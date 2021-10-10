package cql4s

final class Column[Name, T](using val name: DbIdentifier[Name], override val dbType: DbType[T]) extends Field[T]:
  override def toString: String = name.value

type :=:[A, B] = Column[A, B]
