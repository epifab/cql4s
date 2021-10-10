package casa

import casa.utils.ColumnsFactory

trait Table[Name, Columns](using val name: DbIdentifier[Name], val columns: ColumnsFactory[Columns]):

  val fields: Columns = columns.value

  override def toString: String = name.value
