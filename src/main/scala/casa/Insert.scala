package casa

import casa.compiler.{CommandCompiler}
import casa.utils.NonEmptyListOfFields


class Insert[TableName, TableColumns, KeyValues](
  val table: Table[TableName, TableColumns],
  val keyValues: KeyValues
):

  def fields[A, NewKeyValues](f: Selectable[TableColumns] => A)(
    using
    assignments: Assignments[A, NewKeyValues]
  ): Insert[TableName, TableColumns, NewKeyValues] =
    Insert[TableName, TableColumns, NewKeyValues](table, assignments(f(table)))
    
  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)


object Insert:
  def into[TableName, TableColumns, KeyValues](table: Table[TableName, TableColumns])(
    using
    assignments: Assignments[TableColumns, KeyValues]
  ): Insert[TableName, TableColumns, KeyValues] = Insert(table, assignments(table.*))
