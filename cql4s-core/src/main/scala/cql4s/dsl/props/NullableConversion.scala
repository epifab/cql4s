package cql4s.dsl
package props

trait NullableConversion[T, U: IsNullable]

object NullableConversion:
  given n[T: DataType: IsNullable]: NullableConversion[T, T] with { }
  given nn[T: DataType: IsNotNullable](using IsNullable[nullable[T]]): NullableConversion[T, nullable[T]] with { }
