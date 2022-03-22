package cql4s.dsl
package props

trait AreComparable[-T, -U]

trait SameCategoryComparisons:
  given numerical[T: DataType: IsNumerical, U: DataType: IsNumerical]: AreComparable[T, U] with { }
  given text[T: DataType: IsText, U: DataType: IsText]: AreComparable[T, U] with { }

object AreComparable extends SameCategoryComparisons:
  given identity[T: DataType]: AreComparable[T, T] with { }
  given leftNullable[T: DataType: IsNotNullable, U: DataType: IsNotNullable] (using AreComparable[T, U]): AreComparable[nullable[T], U] with { }
  given rightNullable[T: DataType: IsNotNullable, U: DataType: IsNotNullable] (using AreComparable[T, U]): AreComparable[T, nullable[U]] with { }
  given field[T: DataType, U: DataType] (using AreComparable[T, U]): AreComparable[Field[T], Field[U]] with { }

