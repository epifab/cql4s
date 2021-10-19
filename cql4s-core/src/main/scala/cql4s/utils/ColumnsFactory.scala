package cql4s.utils

import cql4s.dsl.{Column, DataType, DbIdentifier}

/**
 * Singleton value for a list of columns
 * @tparam Columns
 */
trait ColumnsFactory[Columns]:
  def value: Columns
  def toList: List[Column[_, _]]

object ColumnsFactory:
  given empty: ColumnsFactory[EmptyTuple] with
    def value: EmptyTuple = EmptyTuple
    def toList: List[Column[_, _]] = Nil

  given head[Name, T, Tail <: Tuple](using dbi: DbIdentifier[Name], dt: DataType[T], tail: ColumnsFactory[Tail]): ColumnsFactory[Column[Name, T] *: Tail] with
    def value: Column[Name, T] *: Tail = new Column[Name, T] *: tail.value
    def toList: List[Column[_, _]] = new Column[Name, T] :: tail.toList

  given singleColumn[Name, T](using dbi: DbIdentifier[Name], dt: DataType[T]): ColumnsFactory[Column[Name, T]] with
    def value: Column[Name, T] = new Column[Name, T]
    def toList: List[Column[_, _]] = new Column[Name, T] :: Nil
