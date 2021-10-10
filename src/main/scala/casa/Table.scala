package casa

import casa.utils.{ColumnsFactory, Finder}


trait Selectable[Columns]:
  def columns: Columns

  def apply[Tag <: Singleton, Needle](tag: Tag)(
    using
    finder: Finder[Columns, Needle, Tag]
  ): Needle = finder.find(columns)


trait Table[Name, Columns](using val name: DbIdentifier[Name], val columnsFactory: ColumnsFactory[Columns]) extends Selectable[Columns]:

  override val columns: Columns = columnsFactory.value

  override val toString: String = name.value
