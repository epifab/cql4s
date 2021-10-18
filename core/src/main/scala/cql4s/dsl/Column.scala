package cql4s.dsl

final class Column[Name, T](using val name: DbIdentifier[Name], override val dataType: DataType[T]) extends Field[T]:
  override def toString: String = name.value

type :=:[A, B] = Column[A, B]
