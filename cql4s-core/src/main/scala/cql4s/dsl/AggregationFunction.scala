package cql4s.dsl

import cql4s.dsl.props.NullableConversion

sealed trait AggregationFunction

class count[T: DataType, F <: Column[_, T]](val param: F)(using val dataType: DataType[bigint])
  extends AggregationFunction, DbFunction1[F, bigint]:
    override val dbName: String = "count"

object *

object count:
  def apply[T: DataType, F <: Column[_, T]](f: F): count[T, F] = new count(f)
  def apply(s: *.type): UnsafeField[bigint] = UnsafeField("count(*)")

class max[T: DataType, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction with DbFunction1[F, U]:
  override val dbName: String = "max"

class min[T: DataType, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction with DbFunction1[F, U]:
    override val dbName: String = "min"
