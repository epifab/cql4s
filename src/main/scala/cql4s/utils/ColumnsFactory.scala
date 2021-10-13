package cql4s
package utils

/**
 * Singleton value for a list of columns
 * @tparam Columns
 */
trait ColumnsFactory[Columns]:
  def value: Columns

object ColumnsFactory:
  given empty: ColumnsFactory[EmptyTuple] with
    def value: EmptyTuple = EmptyTuple

  given head[Name, T, Tail <: Tuple](using dbi: DbIdentifier[Name], dt: DataType[T], tail: ColumnsFactory[Tail]): ColumnsFactory[Column[Name, T] *: Tail] with
    def value: Column[Name, T] *: Tail = new Column[Name, T] *: tail.value

  given singleColumn[Name, T](using dbi: DbIdentifier[Name], dt: DataType[T]): ColumnsFactory[Column[Name, T]] with
    def value: Column[Name, T] = new Column[Name, T]
