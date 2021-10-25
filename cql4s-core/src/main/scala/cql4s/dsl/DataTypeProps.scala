package cql4s.dsl

import scala.util.NotGiven

object DataTypeProps

trait IsNullable[-T]
trait IsNumerical[T]
trait IsText[-T]

object IsNullable:
  given[T: DataType]: IsNullable[nullable[T]] with { }
  given[T: DataType: IsNullable]: IsNullable[Field[T]] with { }


trait IsNotNullable[-T]

object IsNotNullable:
  given dataType[T: DataType] (using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }
  given field[T: Field](using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }


object IsNumerical:
  given short[T](using DataType.Aux[T, Short]): IsNumerical[T] with { }
  given integer[T](using DataType.Aux[T, Int]): IsNumerical[T] with { }
  given long[T](using DataType.Aux[T, Long]): IsNumerical[T] with { }
  given bigDecimal[T](using DataType.Aux[T, BigDecimal]): IsNumerical[T] with { }
  given float[T](using DataType.Aux[T, Float]): IsNumerical[T] with { }
  given double[T](using DataType.Aux[T, Double]): IsNumerical[T] with { }
  given[T: IsNumerical]: IsNumerical[nullable[T]] with { }
  given[T: IsNumerical]: IsNumerical[Field[T]] with { }


object IsText:
  given string[T](using DataType.Aux[T, String]): IsText[T] with { }
  given[T: IsText]: IsText[nullable[T]] with { }
  given[T: IsText]: IsText[Field[T]] with { }


trait AreComparable[-T, -U]

trait SameCategoryComparisons:
  given numerical[T: DataType: IsNumerical, U: DataType: IsNumerical]: AreComparable[T, U] with { }
  given text[T: DataType: IsText, U: DataType: IsText]: AreComparable[T, U] with { }

object AreComparable extends SameCategoryComparisons:
  given identity[T: DataType]: AreComparable[T, T] with { }
  given leftNullable[T: DataType: IsNotNullable, U: DataType: IsNotNullable] (using AreComparable[T, U]): AreComparable[nullable[T], U] with { }
  given rightNullable[T: DataType: IsNotNullable, U: DataType: IsNotNullable] (using AreComparable[T, U]): AreComparable[T, nullable[U]] with { }
  given field[T: DataType, U: DataType] (using AreComparable[T, U]): AreComparable[Field[T], Field[U]] with { }


trait CanContain[-T, -U]

object CanContain:
  given notNullableList[T, U](using AreComparable[T, U]): CanContain[list[T], U] with { }
  given leftNullableList[T, U](using AreComparable[T, U]): CanContain[nullable[list[T]], U] with { }
  given rightNullableList[T, U](using AreComparable[T, U]): CanContain[list[T], nullable[U]] with { }
  given bothNullableList[T, U](using AreComparable[T, U]): CanContain[nullable[list[T]], nullable[U]] with { }
  given field[T, U](using CanContain[U, T]): CanContain[Field[U], Field[T]] with { }
