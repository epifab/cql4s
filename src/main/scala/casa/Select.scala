package casa

import casa.utils.FindAll


class Select[TableName, TableColumns, Columns](table: Table[TableName, TableColumns], columns: Columns):
  def take[ColumnsName, NewColumns](names: ColumnsName)(using finder: FindAll[TableColumns, ColumnsName, NewColumns]): Select[TableName, TableColumns, NewColumns] =
    Select(table, finder.get(table.columns))

object Select:
  def from[TableName, TableColumns](table: Table[TableName, TableColumns]): Select[TableName, TableColumns, EmptyTuple] =
    Select(table, EmptyTuple)
