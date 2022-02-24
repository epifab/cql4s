package cql4s.compiler

import cql4s.dsl.Column

trait ColumnFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object ColumnFragment:
  given column[Name, T]: ColumnFragment[Column[Name, T], EmptyTuple] with
    def build(field: Column[Name, T]): CompiledFragment[EmptyTuple] =
      CompiledFragment(field.name.escaped)
