package cql4s.dsl
package props

trait IsNullable[-T]

object IsNullable:
  given[T: DataType]: IsNullable[nullable[T]] with { }
  given[T: DataType: IsNullable]: IsNullable[Field[T]] with { }
