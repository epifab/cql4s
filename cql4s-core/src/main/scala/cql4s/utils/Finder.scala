package cql4s.utils

import cql4s.dsl.Column

trait Finder[-Haystack, +Needle, Tag]:
  def find(haystack: Haystack): Needle

trait TailFinder:
  given tail[Needle, Tag, Head, Tail <: Tuple](using finder: Finder[Tail, Needle, Tag]): Finder[Head *: Tail, Needle, Tag] with
    def find(haystack: Head *: Tail): Needle = finder.find(haystack.tail)

object Finder extends TailFinder:
  given head[Needle, Tag, Head, Tail <: Tuple](using finder: Finder[Head, Needle, Tag]): Finder[Head *: Tail, Needle, Tag] with
    def find(haystack: Head *: Tail): Needle = finder.find(haystack.head)

  given column[Name, Type]: Finder[Column[Name, Type], Column[Name, Type], Name] with
    def find(haystack: Column[Name, Type]): Column[Name, Type] = haystack
