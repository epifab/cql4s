package cql4s.dsl

import cql4s.compiler.CommandCompiler
import cql4s.utils.NonEmptyListOfFields

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

case class UpdateParameters(ttl: Option[FiniteDuration], timestamp: Option[Instant])

class Insert[Keyspace, TableName, TableColumns, KeyValues](
  val table: Table[Keyspace, TableName, TableColumns],
  val keyValues: KeyValues,
  val updateParameters: UpdateParameters,
  val ifNotExistsCond: Boolean
):

  def fields[A, NewKeyValues](f: Selectable[TableColumns] => A)(
    using
    assignments: Assignments[A, NewKeyValues]
  ): Insert[Keyspace, TableName, TableColumns, NewKeyValues] =
    Insert[Keyspace, TableName, TableColumns, NewKeyValues](table, assignments(f(table)), updateParameters, ifNotExistsCond)

  def usingTtl(ttl: FiniteDuration): Insert[Keyspace, TableName, TableColumns, KeyValues] =
    Insert(table, keyValues, updateParameters.copy(ttl = Some(ttl)), ifNotExistsCond)

  def usingTimestamp(timestamp: Instant): Insert[Keyspace, TableName, TableColumns, KeyValues] =
    Insert(table, keyValues, updateParameters.copy(timestamp = Some(timestamp)), ifNotExistsCond)

  def ifNotExists: Insert[Keyspace, TableName, TableColumns, KeyValues] =
    Insert(table, keyValues, updateParameters, true)

  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)


object Insert:
  def into[Keyspace, TableName, TableColumns, KeyValues](table: Table[Keyspace, TableName, TableColumns])(
    using
    assignments: Assignments[TableColumns, KeyValues]
  ): Insert[Keyspace, TableName, TableColumns, KeyValues] = Insert(table, assignments(table.*), updateParameters = UpdateParameters(None, None), false)
