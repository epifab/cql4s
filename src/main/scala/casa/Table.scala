package casa

import casa.utils.ColumnsFactory

trait Table[Name, Columns](using val name: DbIdentifier[Name], val columnsFactory: ColumnsFactory[Columns]):

  val columns: Columns = columnsFactory.value

  override def toString: String = name.value
