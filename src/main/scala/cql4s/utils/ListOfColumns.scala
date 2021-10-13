package cql4s
package utils

/**
 * Ensure Columns is either a single field or a tuple made of fields
 * @tparam Columns
 */
trait ListOfColumns[-Columns]

object ListOfColumns:
  given [F <: Column[_, _]]: ListOfColumns[F] with { }
  given ListOfColumns[EmptyTuple] with { }
  given [H, T <: Tuple](using ListOfColumns[H], ListOfColumns[T]): ListOfColumns[H *: T] with { }

/**
 * Ensure Columns is either a single field or a non empty tuple of fields
 * @tparam Columns
 */
trait NonEmptyListOfColumns[-Columns]

object NonEmptyListOfColumns:
  given [F <: Column[_, _]]: NonEmptyListOfColumns[F] with { }
  given twoOrMore[H, T <: NonEmptyTuple](using NonEmptyListOfColumns[H], NonEmptyListOfColumns[T]): NonEmptyListOfColumns[H *: T] with { }
  given one[H](using ListOfColumns[H]): NonEmptyListOfColumns[H *: EmptyTuple] with { }
