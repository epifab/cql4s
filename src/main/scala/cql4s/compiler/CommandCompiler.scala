package cql4s
package compiler

import Tuple.Concat
import scala.deriving.Mirror

trait CommandCompiler[-C, Input]:
  def build(command: C): Command[Input]

trait CommandFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object CommandFragment:

  def updateParameters(up: UpdateParameters): Option[String] =
    (up.ttl, up.timestamp) match
      case (None, None) => None
      case (a, b) => Some("USING " + (a.map("TTL " + _.toSeconds).toList ++ b.map(ts => "TIMESTAMP " + (ts.toEpochMilli * 1000))).mkString(" AND "))

  given insert[Keyspace, TableName, TableColumns, KeyValues, A <: Tuple, B <: Tuple] (
    using
    fields: ListFragment[KeyFragment, KeyValues, A],
    values: ListFragment[ValueFragment, KeyValues, B]
  ): CommandFragment[Insert[Keyspace, TableName, TableColumns, KeyValues], A Concat B] with
    def build(command: Insert[Keyspace, TableName, TableColumns, KeyValues]): CompiledFragment[A Concat B] = {
      CompiledFragment(s"INSERT INTO ${command.table.keyspace.escaped}.${command.table.name.escaped}") ++
        fields.build(command.keyValues, ", ").wrap("(", ")") ++
        values.build(command.keyValues, ", ").wrap("VALUES (", ")") ++
        updateParameters(command.updateParameters)
    }

  given update[Keyspace, TableName, TableColumns, KeyValues, Where <: LogicalExpr, A <: Tuple, B <: Tuple] (
    using
    keyValues: ListFragment[KeyValueFragment, KeyValues, A],
    where: LogicalExprFragment[Where, B]
  ): CommandFragment[Update[Keyspace, TableName, TableColumns, KeyValues, Where], A Concat B] with
    def build(command: Update[Keyspace, TableName, TableColumns, KeyValues, Where]): CompiledFragment[A Concat B] =
      CompiledFragment(s"UPDATE ${command.table.keyspace.escaped}.${command.table.name.escaped}") ++
        updateParameters(command.updateParameters) ++
        keyValues.build(command.keyValues, ", ").prepend("SET ") ++
        where.build(command.where).prepend("WHERE ")

object CommandCompiler:
  given [C, I <: Tuple, Input](
    using
    commandFragment: CommandFragment[C, I],
    encoder: EncoderAdapter[I, Input]
  ): CommandCompiler[C, Input] with
    def build(command: C): Command[Input] =
      Command(commandFragment.build(command).cql, encoder)
