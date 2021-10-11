package cql4s

import cql4s.compiler.CommandCompiler
import cql4s.utils.NonEmptyListOfFields

import scala.concurrent.duration.FiniteDuration


class Insert[TableName, TableColumns, KeyValues](
  val table: Table[TableName, TableColumns],
  val keyValues: KeyValues,
  val ttl: Option[FiniteDuration]
):

  def fields[A, NewKeyValues](f: Selectable[TableColumns] => A)(
    using
    assignments: Assignments[A, NewKeyValues]
  ): Insert[TableName, TableColumns, NewKeyValues] =
    Insert[TableName, TableColumns, NewKeyValues](table, assignments(f(table)), ttl)
    
  def usingTtl(ttl: FiniteDuration): Insert[TableName, TableColumns, KeyValues] =
    Insert(table, keyValues, Some(ttl))
    
  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)


object Insert:
  def into[TableName, TableColumns, KeyValues](table: Table[TableName, TableColumns])(
    using
    assignments: Assignments[TableColumns, KeyValues]
  ): Insert[TableName, TableColumns, KeyValues] = Insert(table, assignments(table.*), ttl = None)
