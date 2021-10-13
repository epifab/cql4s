package cql4s

import cql4s.compiler.CommandCompiler
import cql4s.utils.NonEmptyListOfFields

import java.time.Instant
import scala.concurrent.duration.FiniteDuration


class Update[Keyspace, TableName, TableColumns, KeyValues, Where <: LogicalExpr](
  val table: Table[Keyspace, TableName, TableColumns],
  val keyValues: KeyValues,
  val where: Where,
  val updateParameters: UpdateParameters
):

  def set[A, NewKeyValues](f: Selectable[TableColumns] => A)(
    using
    assignments: Assignments[A, NewKeyValues]
  ): Update[Keyspace, TableName, TableColumns, NewKeyValues, Where] =
    new Update(table, assignments(f(table)), where, updateParameters)

  def where[NewWhere <: LogicalExpr](f: Selectable[TableColumns] => NewWhere): Update[Keyspace, TableName, TableColumns, KeyValues, NewWhere] =
    new Update(table, keyValues, f(table), updateParameters)

  def usingTtl(ttl: FiniteDuration): Update[Keyspace, TableName, TableColumns, KeyValues, Where] =
    new Update(table, keyValues, where, updateParameters.copy(ttl = Some(ttl)))

  def usingTimestamp(timestamp: Instant): Update[Keyspace, TableName, TableColumns, KeyValues, Where] =
    new Update(table, keyValues, where, updateParameters.copy(timestamp = Some(timestamp)))

  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)


object Update:
  def apply[Keyspace, TableName, TableColumns, KeyValues](table: Table[Keyspace, TableName, TableColumns])(
    using
    assignments: Assignments[TableColumns, KeyValues]
  ): Update[Keyspace, TableName, TableColumns, KeyValues, AlwaysTrue] = new Update(table, assignments(table.*), AlwaysTrue, UpdateParameters(None, None))
