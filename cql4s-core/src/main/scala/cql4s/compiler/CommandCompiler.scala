package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat
import scala.deriving.Mirror

trait CommandCompiler[-C, Input]:
  def build(command: C): Command[Input]

trait CommandFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object CommandFragment:

  def updateParameters(up: UpdateParameters): Option[String] =
    (up.ttl, up.timestamp) match
      case (None, None) => None
      case (a, b) => Some("USING " + (a.map("TTL " + _.toSeconds).toList ++ b.map(ts => "TIMESTAMP " + (ts.toEpochMilli * 1000))).mkString(" AND "))

  given insert[Keyspace, TableName, TableColumns, KeyValues, A <: Tuple, B <: Tuple](
    using
    // todo: should be NonEmptyListFragment
    fields: ListFragment[KeyFragment, KeyValues, A],
    values: ListFragment[ValueFragment, KeyValues, B]
  ): CommandFragment[Insert[Keyspace, TableName, TableColumns, KeyValues], A Concat B] with
    def build(command: Insert[Keyspace, TableName, TableColumns, KeyValues]): CompiledFragment[A Concat B] = {
      CompiledFragment(s"INSERT INTO ${command.table.keyspace.escaped}.${command.table.name.escaped}") ++
        fields.build(command.keyValues, ", ").wrap("(", ")") ++
        values.build(command.keyValues, ", ").wrap("VALUES (", ")") ++
        updateParameters(command.updateParameters)
    }

  given update[Keyspace, TableName, TableColumns, KeyValues, Where <: LogicalExpr, A <: Tuple, B <: Tuple](
    using
    // todo: should be NonEmptyListFragment
    keyValues: ListFragment[KeyValueFragment, KeyValues, A],
    where: LogicalExprFragment[Where, B]
  ): CommandFragment[Update[Keyspace, TableName, TableColumns, KeyValues, Where], A Concat B] with
    def build(command: Update[Keyspace, TableName, TableColumns, KeyValues, Where]): CompiledFragment[A Concat B] =
      CompiledFragment(s"UPDATE ${command.table.keyspace.escaped}.${command.table.name.escaped}") ++
        updateParameters(command.updateParameters) ++
        keyValues.build(command.keyValues, ", ").prepend("SET ") ++
        where.build(command.where).prependOpt("WHERE ")

  given delete[Keysapce, TableName, TableColumns, Where <: LogicalExpr, A <: Tuple](
    using
    where: LogicalExprFragment[Where, A]
  ): CommandFragment[Delete[Keysapce, TableName, TableColumns, Where], A] with
    def build(command: Delete[Keysapce, TableName, TableColumns, Where]): CompiledFragment[A] =
      CompiledFragment(s"DELETE FROM ${command.table.keyspace.escaped}.${command.table.name.escaped}") ++
        updateParameters(command.updateParameters) ++
        where.build(command.where).prependOpt("WHERE ")

  given truncate[Keyspace, TableName, TableColumns]: CommandFragment[Truncate[Keyspace, TableName, TableColumns], EmptyTuple] with
    def build(command: Truncate[Keyspace, TableName, TableColumns]): CompiledFragment[EmptyTuple] =
      CompiledFragment(s"TRUNCATE ${command.table.keyspace.escaped}.${command.table.name.escaped}")


object CommandCompiler:
  given [C, I <: Tuple, Input](
    using
    commandFragment: CommandFragment[C, I],
    encoder: EncoderFactory[I, Input]
  ): CommandCompiler[C, Input] with
    def build(command: C): Command[Input] =
      val fragment = commandFragment.build(command)
      Command(fragment.cql, encoder(fragment.input), None)
