package cql4s.dsl

import cql4s.compiler.QueryCompiler
import cql4s.utils.{FindAll, NonEmptyListOfColumns, NonEmptyListOfFields, OptionalInput}

class Select[Keyspace, TableName, TableColumns, Fields, Where <: LogicalExpr, GroupBy, OrderBy: OrderByClasue, Limit: [X] =>> OptionalInput[bigint, X], PerPartitionLimit: [X] =>> OptionalInput[bigint, X]](
  val table: Table[Keyspace, TableName, TableColumns],
  val fields: Fields,
  val where: Where,
  val groupBy: GroupBy,
  val orderBy: OrderBy,
  val limit: Limit,
  val perPartitionLimit: PerPartitionLimit,
  val allowFiltering: Boolean
):

  def compile[Input, Output](using compiler: QueryCompiler[this.type, Input, Output]): Query[Input, Output] =
    compiler.build(this)

  def take[NewFields](f: Selectable[TableColumns] => NewFields)(using NonEmptyListOfFields[NewFields]): Select[Keyspace, TableName, TableColumns, NewFields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit] =
    Select(table, f(table), where, groupBy, orderBy, limit, perPartitionLimit, allowFiltering)

  def where[NewWhere <: LogicalExpr](f: Selectable[TableColumns] => NewWhere): Select[Keyspace, TableName, TableColumns, Fields, NewWhere, GroupBy, OrderBy, Limit, PerPartitionLimit] =
    Select(table, fields, f(table), groupBy, orderBy, limit, perPartitionLimit, allowFiltering)

  def andWhere[NewWhere <: LogicalExpr](f: Selectable[TableColumns] => NewWhere): Select[Keyspace, TableName, TableColumns, Fields, And[LogicalEpxrWrap[Where], LogicalEpxrWrap[NewWhere]], GroupBy, OrderBy, Limit, PerPartitionLimit] =
    Select(table, fields, <<(where) and <<(f(table)), groupBy, orderBy, limit, perPartitionLimit, allowFiltering)

  def groupBy[NewGroupBy](f: Selectable[TableColumns] => NewGroupBy)(using NonEmptyListOfColumns[NewGroupBy]): Select[Keyspace, TableName, TableColumns, Fields, Where, NewGroupBy, OrderBy, Limit, PerPartitionLimit] =
    Select(table, fields, where, f(table), orderBy, limit, perPartitionLimit, allowFiltering)

  def orderBy[NewOrderBy](f: Selectable[TableColumns] => NewOrderBy)(using OrderByClasue[NewOrderBy]): Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, NewOrderBy, Limit, PerPartitionLimit] =
    Select(table, fields, where, groupBy, f(table), limit, perPartitionLimit, allowFiltering)

  def limit[NewLimit](newLimit: NewLimit)(using OptionalInput[bigint, NewLimit]): Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, NewLimit, PerPartitionLimit] =
    Select(table, fields, where, groupBy, orderBy, newLimit, perPartitionLimit, allowFiltering)

  def perPartitionLimit[NewPerPartitionLimit](newPerPartitionLimit: NewPerPartitionLimit)(using OptionalInput[bigint, NewPerPartitionLimit]): Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, NewPerPartitionLimit] =
    Select(table, fields, where, groupBy, orderBy, limit, newPerPartitionLimit, allowFiltering)

  def allowFiltering(allowed: Boolean): Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit] =
    Select(table, fields, where, groupBy, orderBy, limit, perPartitionLimit, allowed)


object Select:
  def from[Keyspace, TableName, TableColumns](table: Table[Keyspace, TableName, TableColumns]): Select[Keyspace, TableName, TableColumns, EmptyTuple, AlwaysTrue, EmptyTuple, EmptyTuple, None.type, None.type] =
    Select(table, EmptyTuple, AlwaysTrue, EmptyTuple, EmptyTuple, None, None, allowFiltering = false)
