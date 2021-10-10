package casa
package compiler

import Tuple.Concat
import scala.deriving.Mirror

trait CommandCompiler[-C, Input]:
  def build(command: C): Command[Input]

object CommandCompiler:
  given insert[TableName, TableColumns, KeyValues, A <: Tuple, B <: Tuple, Input] (
   using
   fields: ListFragment[KeyFragment, KeyValues, A],
   values: ListFragment[ValueFragment, KeyValues, B],
   encoder: EncoderAdapter[A Concat B, Input]
  ): CommandCompiler[Insert[TableName, TableColumns, KeyValues], Input] with
    def build(command: Insert[TableName, TableColumns, KeyValues]): Command[Input] =
      val fragment = CompiledFragment(s"INSERT INTO ${command.table.name.escaped}") ++
        fields.build(command.keyValues, ", ").wrap("(", ")") ++
        values.build(command.keyValues, ", ").wrap("VALUES (", ")")
      Command(fragment.cql, encoder)
