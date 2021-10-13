package cql4s

import cql4s.utils.{ColumnsFactory, Finder}


trait Selectable[Columns]:
  def `*`: Columns

  def apply[Tag <: Singleton, Needle](tag: Tag)(
    using
    finder: Finder[Columns, Needle, Tag]
  ): Needle = finder.find(*)


trait Table[Keyspace, Name, Columns](using val keyspace: DbIdentifier[Keyspace], val name: DbIdentifier[Name], val columnsFactory: ColumnsFactory[Columns]) extends Selectable[Columns]:

  override val `*`: Columns = columnsFactory.value

  override val toString: String = name.value
