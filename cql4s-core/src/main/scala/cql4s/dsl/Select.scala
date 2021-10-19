package cql4s.dsl

import cql4s.compiler.QueryCompiler
import cql4s.utils.{FindAll, NonEmptyListOfFields}

class Select[Keyspace, TableName, TableColumns, Fields, Where <: LogicalExpr, GroupBy](
  val table: Table[Keyspace, TableName, TableColumns],
  val fields: Fields,
  val where: Where,
  val groupBy: GroupBy,
  val allowFiltering: Boolean
):

  def compile[Input, Output](using compiler: QueryCompiler[this.type, Input, Output]): Query[Input, Output] =
    compiler.build(this)

  def take[NewFields](f: Selectable[TableColumns] => NewFields)(using NonEmptyListOfFields[NewFields]): Select[Keyspace, TableName, TableColumns, NewFields, Where, GroupBy] =
    Select(table, f(table), where, groupBy, allowFiltering)

  def groupBy[NewGroupBy](f: Selectable[TableColumns] => NewGroupBy)(using NonEmptyListOfFields[NewGroupBy]): Select[Keyspace, TableName, TableColumns, Fields, Where, NewGroupBy] =
    Select(table, fields, where, f(table), allowFiltering)

  def allowFiltering(allowed: Boolean): Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy] =
    Select(table, fields, where, groupBy, allowed)

  def where[NewWhere <: LogicalExpr](f: Selectable[TableColumns] => NewWhere): Select[Keyspace, TableName, TableColumns, Fields, NewWhere, GroupBy] =
    Select(table, fields, f(table), groupBy, allowFiltering)


object Select:
  def from[Keyspace, TableName, TableColumns](table: Table[Keyspace, TableName, TableColumns]): Select[Keyspace, TableName, TableColumns, EmptyTuple, AlwaysTrue, EmptyTuple] =
    Select(table, EmptyTuple, AlwaysTrue, EmptyTuple, allowFiltering = false)
