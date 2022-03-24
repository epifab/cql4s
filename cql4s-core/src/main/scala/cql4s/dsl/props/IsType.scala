package cql4s.dsl
package props

trait IsType[X: DataType, -T]

trait IsTypeId:
  given[T: DataType]: IsType[T, T] with { }

object IsType extends IsTypeId:
  given[T: DataType, U: DataType](using IsType[T, U]): IsType[T, nullable[U]] with { }
  given[T: DataType, U: DataType](using IsType[T, U]): IsType[T, Field[U]] with { }

trait IsAnyType[Xs <: Tuple, -T]

trait IsAnyTypeTail:
  given foundInTail[X: DataType, Tail <: Tuple, T](using IsAnyType[Tail, T]): IsAnyType[X *: Tail, T] with { }

object IsAnyType extends IsAnyTypeTail:
  given foundInHead[X: DataType, Tail <: Tuple, T](using IsType[X, T]): IsAnyType[X *: Tail, T] with { }
