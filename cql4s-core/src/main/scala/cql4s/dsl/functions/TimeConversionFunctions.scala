package cql4s.dsl
package functions

import cql4s.dsl.props.IsAnyType

sealed trait TimeConversionFunctions

class toDate[F <: Field[_]](val param: F)(using IsAnyType[(timeuuid, timestamp), F])(using val dataType: DataType[date])
  extends TimeConversionFunctions, DbFunction1[F, date]:
    override val dbName: String = "toDate"

class toTimestamp[F <: Field[_]](val param: F)(using IsAnyType[(timeuuid, date), F])(using val dataType: DataType[timestamp])
  extends TimeConversionFunctions, DbFunction1[F, timestamp]:
    override val dbName: String = "toTimestamp"

class toUnixTimestamp[F <: Field[_]](val param: F)(using IsAnyType[(timeuuid, timestamp, date), F])(using val dataType: DataType[bigint])
  extends TimeConversionFunctions, DbFunction1[F, bigint]:
    override val dbName: String = "toUnixTimestamp"


object dateOf:
  @deprecated("Use toDate", "Cassandra 4.x")
  def apply[F <: Field[_]](param: F)(using IsAnyType[(timeuuid, timestamp), F], DataType[date]): toDate[F] =
    toDate(param)

object unixTimestampOf:
  @deprecated("Use toUnixTimestamp", "Cassandra 4.x")
  def apply[F <: Field[_]](param: F)(using IsAnyType[(timeuuid, timestamp, date), F], DataType[bigint]): toUnixTimestamp[F] =
    toUnixTimestamp(param)
