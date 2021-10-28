package cql4s.compiler

import cql4s.dsl.*

trait FieldFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object FieldFragment:

  given column[Name, T]: FieldFragment[Column[Name, T], EmptyTuple] with
    def build(field: Column[Name, T]): CompiledFragment[EmptyTuple] =
      CompiledFragment(field.name.escaped)
      
  given placeholder[P <: Placeholder[_]]: FieldFragment[P, P *: EmptyTuple] with
    def build(placeholder: P): CompiledFragment[P *: EmptyTuple] = 
      CompiledFragment(List("?"), placeholder *: EmptyTuple)

  given const[P <: Const[_]]: FieldFragment[P, P *: EmptyTuple] with
    def build(const: P): CompiledFragment[P *: EmptyTuple] =
      CompiledFragment(List("?"), const *: EmptyTuple)

  given cast[F <: Field[_], I <: Tuple, U](using inner: FieldFragment[F, I]): FieldFragment[Cast[F, U], I] with
    def build(cast: Cast[F, U]): CompiledFragment[I] = inner.build(cast.field).wrap("CAST(", s" AS ${cast.dataType.dbName})")

  given dbFunction[FS <: Tuple, T, Output <: Tuple](
    using
    inner: ListFragment[FieldFragment, FS, Output]
  ): FieldFragment[DbFunction[FS, T], Output] with
    def build(func: DbFunction[FS, T]): CompiledFragment[Output] =
      func match
        case f: DbFunction2[_, _, _] if f.infixNotation => inner.build(func.params, s" ${func.dbName} ")
        case _ => inner.build(func.params, ", ").wrap(s"${func.dbName}(", ")")
