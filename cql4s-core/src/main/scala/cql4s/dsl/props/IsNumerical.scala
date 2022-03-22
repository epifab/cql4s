package cql4s.dsl
package props

trait IsNumerical[T]

object IsNumerical:
  given byte[T](using DataType.Aux[T, Byte]): IsNumerical[T] with { }
  given short[T](using DataType.Aux[T, Short]): IsNumerical[T] with { }
  given integer[T](using DataType.Aux[T, Int]): IsNumerical[T] with { }
  given long[T](using DataType.Aux[T, Long]): IsNumerical[T] with { }
  given bigInt[T](using DataType.Aux[T, BigInt]): IsNumerical[T] with { }
  given bigDecimal[T](using DataType.Aux[T, BigDecimal]): IsNumerical[T] with { }
  given float[T](using DataType.Aux[T, Float]): IsNumerical[T] with { }
  given double[T](using DataType.Aux[T, Double]): IsNumerical[T] with { }
  given[T: IsNumerical]: IsNumerical[nullable[T]] with { }
  given[T: IsNumerical]: IsNumerical[Field[T]] with { }
