package cql4s.dsl

sealed trait AggregationFunction

class count[T: DataType, F <: Field[T]](val param: F)(using val dataType: DataType[bigint])
  extends AggregationFunction, DbFunction1[F, bigint]:
    override val dbName: String = "count"

object *

object count:
  def apply[T: DataType, F <: Field[T]](f: F): count[T, F] = new count(f)
  def apply(s: *.type): UnsafeField[bigint] = UnsafeField("count(*)")
