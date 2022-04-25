package cql4s.dsl

import cql4s.dsl.props.IsType

sealed trait DateTimeFunctions

class now(using val dataType: DataType[timeuuid]) extends DateTimeFunctions, DbFunction0[timeuuid]:
  override val dbName: String = "now"

object currentTimeUUID:
  def apply()(using DataType[timeuuid]): now = now()

class currentTimestamp(using val dataType: DataType[timestamp]) extends DateTimeFunctions, DbFunction0[timestamp]:
  override val dbName: String = "currentTimestamp"

class currentDate(using val dataType: DataType[date]) extends DateTimeFunctions, DbFunction0[date]:
  override val dbName: String = "currentDate"

class currentTime(using val dataType: DataType[time]) extends DateTimeFunctions, DbFunction0[time]:
  override val dbName: String = "currentTime"

class minTimeuuid[F <: Field[_]](val param: F)(using IsType[timestamp, F])(using val dataType: DataType[timeuuid]) extends DateTimeFunctions, DbFunction1[F, timeuuid]:
  override val dbName: String = "minTimeuuid"

class maxTimeuuid[F <: Field[_]](val param: F)(using IsType[timestamp, F])(using val dataType: DataType[timeuuid]) extends DateTimeFunctions, DbFunction1[F, timeuuid]:
  override val dbName: String = "maxTimeuuid"
