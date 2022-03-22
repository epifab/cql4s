package cql4s.dsl
package props

trait IsText[-T]

object IsText:
  given string[T](using DataType.Aux[T, String]): IsText[T] with { }
  given[T: IsText]: IsText[nullable[T]] with { }
  given[T: IsText]: IsText[Field[T]] with { }
