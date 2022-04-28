package cql4s.dsl

import cql4s.dsl.props.{IsNumerical, NullableConversion}

sealed trait AggregationFunction[+F <: Column[_, _], T](override val dbName: String) extends DbFunction1[F, T]

class count[T: DataType, F <: Column[_, T]](val param: F)(using val dataType: DataType[bigint])
  extends AggregationFunction[F, bigint]("count")

object *

object count:
  def apply[T: DataType, F <: Column[_, T]](f: F): count[T, F] = new count(f)
  def apply(s: *.type): UnsafeField[bigint] = UnsafeField("count(*)")

class max[T: DataType, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction[F, U]("max")

class min[T: DataType, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction[F, U]("min")

class avg[T: DataType: IsNumerical, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction[F, U]("avg")

class sum[T: DataType: IsNumerical, F <: Column[_, T], U](val param: F)(using NullableConversion[T, U])(using val dataType: DataType[U])
  extends AggregationFunction[F, U]("sum")
