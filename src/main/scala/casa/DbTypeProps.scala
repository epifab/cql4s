package casa

import scala.util.NotGiven

object DbTypeProps

trait IsNullable[-T]
trait IsNumerical[T]
trait IsText[-T]

object IsNullable:
  given[T: DbType]: IsNullable[nullable[T]] with { }
  given[T: DbType: IsNullable]: IsNullable[Field[T]] with { }


trait IsNotNullable[-T]

object IsNotNullable:
  given dbType[T: DbType](using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }
  given field[T: Field](using NotGiven[IsNullable[T]]): IsNotNullable[T] with { }


object IsNumerical:
  given short[T](using DbType.Out[T, Short]): IsNumerical[T] with { }
  given integer[T](using DbType.Out[T, Int]): IsNumerical[T] with { }
  given long[T](using DbType.Out[T, Long]): IsNumerical[T] with { }
  given bigDecimal[T](using DbType.Out[T, BigDecimal]): IsNumerical[T] with { }
  given float[T](using DbType.Out[T, Float]): IsNumerical[T] with { }
  given double[T](using DbType.Out[T, Double]): IsNumerical[T] with { }
  given[T: IsNumerical]: IsNumerical[nullable[T]] with { }
  given[T: IsNumerical]: IsNumerical[Field[T]] with { }


object IsText:
  given string[T](using DbType.Out[T, String]): IsText[T] with { }
  given[T: IsText]: IsText[nullable[T]] with { }
  given[T: IsText]: IsText[Field[T]] with { }


trait AreComparable[-T, -U]

trait SameCategoryComparisons:
  given numerical[T: DbType: IsNumerical, U: DbType: IsNumerical]: AreComparable[T, U] with { }
  given text[T: DbType: IsText, U: DbType: IsText]: AreComparable[T, U] with { }

object AreComparable extends SameCategoryComparisons:
  given identity[T: DbType]: AreComparable[T, T] with { }
  given leftNullable[T: DbType: IsNotNullable, U: DbType: IsNotNullable](using AreComparable[T, U]): AreComparable[nullable[T], U] with { }
  given rightNullable[T: DbType: IsNotNullable, U: DbType: IsNotNullable](using AreComparable[T, U]): AreComparable[T, nullable[U]] with { }
  given field[T: DbType, U: DbType](using AreComparable[T, U]): AreComparable[Field[T], Field[U]] with { }
