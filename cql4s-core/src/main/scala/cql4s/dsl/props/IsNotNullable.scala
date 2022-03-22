package cql4s.dsl
package props

import scala.util.NotGiven

trait IsNotNullable[-T]

object IsNotNullable:
  given dataType[T: DataType] (using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }
  given field[T: Field](using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }
