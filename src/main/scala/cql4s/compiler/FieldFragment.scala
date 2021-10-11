package cql4s
package compiler

trait FieldFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object FieldFragment:

  given column[Name, T]: FieldFragment[Column[Name, T], EmptyTuple] with
    def build(field: Column[Name, T]): CompiledFragment[EmptyTuple] =
      CompiledFragment(field.name.escaped)
      
  given placeholder[P <: Placeholder[_]]: FieldFragment[P, P *: EmptyTuple] with
    def build(placeholder: P): CompiledFragment[P *: EmptyTuple] = 
      CompiledFragment(List("?"), placeholder *: EmptyTuple)
