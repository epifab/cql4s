package cql4s.compiler

import cql4s.dsl.*

trait OrderByFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object OrderByFragment:
  given ascending[F <: Field[_], I <: Tuple](using fragment: FieldFragment[F, I]): OrderByFragment[Asc[F], I] with
    def build(asc: Asc[F]): CompiledFragment[I] = fragment.build(asc.field).append(" ASC")

  given descending[F <: Field[_], I <: Tuple](using fragment: FieldFragment[F, I]): OrderByFragment[Desc[F], I] with
    def build(asc: Desc[F]): CompiledFragment[I] = fragment.build(asc.field).append(" DESC")

  given default[F <: Field[_], I <: Tuple](using fragment: FieldFragment[F, I]): OrderByFragment[F, I] with
    def build(field: F): CompiledFragment[I] = fragment.build(field)
