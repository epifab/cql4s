package cql4s.dsl
package props

trait IsType[X: DataType, -T]

trait IsTypeId:
  given[T: DataType]: IsType[T, T] with { }

object IsType extends IsTypeId:
  given[T: DataType, U: DataType](using IsType[T, U]): IsType[T, nullable[U]] with { }
  given[T: DataType, U: DataType](using IsType[T, U]): IsType[T, Field[U]] with { }
