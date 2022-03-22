package cql4s.dsl
package props

trait CanContain[-T, -U]

object CanContain:
  given notNullableList[T, U](using AreComparable[T, U]): CanContain[list[T], U] with { }
  given leftNullableList[T, U](using AreComparable[T, U]): CanContain[nullable[list[T]], U] with { }
  given rightNullableList[T, U](using AreComparable[T, U]): CanContain[list[T], nullable[U]] with { }
  given bothNullableList[T, U](using AreComparable[T, U]): CanContain[nullable[list[T]], nullable[U]] with { }
  given field[T, U](using CanContain[U, T]): CanContain[Field[U], Field[T]] with { }
