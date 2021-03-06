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

  given head[Head, Tail <: Tuple](using head: ColumnsFactory[Head], tail: ColumnsFactory[Tail]): ColumnsFactory[Head *: Tail] with
    def value: Head *: Tail = head.value *: tail.value
    def toList: List[Column[_, _]] = head.toList ++ tail.toList

  given singleColumn[Name, T](using dbi: DbIdentifier[Name], dt: DataType[T]): ColumnsFactory[Column[Name, T]] with
    def value: Column[Name, T] = new Column[Name, T]
    def toList: List[Column[_, _]] = new Column[Name, T] :: Nil
