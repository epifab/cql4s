package cql4s.dsl

import cql4s.compiler.CommandCompiler

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

class Delete[Keyspace, TableName, TableColumns, Where <: LogicalExpr](
  val table: Table[Keyspace, TableName, TableColumns],
  val where: Where,
  val updateParameters: UpdateParameters
):

  def where[NewWhere <: LogicalExpr](f: Selectable[TableColumns] => NewWhere): Delete[Keyspace, TableName, TableColumns, NewWhere] =
    new Delete(table, f(table), updateParameters)

  def usingTtl(ttl: FiniteDuration): Delete[Keyspace, TableName, TableColumns, Where] =
    new Delete(table, where, updateParameters.copy(ttl = Some(ttl)))

  def usingTimestamp(timestamp: Instant): Delete[Keyspace, TableName, TableColumns, Where] =
    new Delete(table, where, updateParameters.copy(timestamp = Some(timestamp)))

  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)


object Delete:
  def from[Keyspace, TableName, TableColumn](table: Table[Keyspace, TableName, TableColumn]) =
    new Delete(table, AlwaysTrue, UpdateParameters(None, None))
