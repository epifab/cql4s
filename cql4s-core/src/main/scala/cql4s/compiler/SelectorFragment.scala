package cql4s.compiler

import cql4s.dsl.*

trait SelectorFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object SelectorFragment:
  given column[C, I <: Tuple](using builder: ColumnFragment[C, I]): SelectorFragment[C, I] with
    def build(column: C): CompiledFragment[I] = builder.build(column)

  given input[P <: InputField[_]]: SelectorFragment[P, P *: EmptyTuple] with
    def build(field: P): CompiledFragment[P *: EmptyTuple] =
      CompiledFragment(List(s"(${field.dataType.dbName})?"), field *: EmptyTuple)

  given cast[F <: Field[_], I <: Tuple, U](using inner: SelectorFragment[F, I]): SelectorFragment[Cast[F, U], I] with
    def build(cast: Cast[F, U]): CompiledFragment[I] = inner.build(cast.field).wrap("CAST(", s" AS ${cast.dataType.dbName})")

  given wrap[T, F <: Field[T], I <: Tuple](using inner: SelectorFragment[F, I]): SelectorFragment[FieldWrap[T, F], I] with
    def build(field: FieldWrap[T, F]): CompiledFragment[I] = inner.build(field.expr).wrap("(", ")")

  given dbFunction[FS <: Tuple, T, Output <: Tuple](
    using
    inner: ListFragment[SelectorFragment, FS, Output]
  ): SelectorFragment[DbFunction[FS, T], Output] with
    def build(func: DbFunction[FS, T]): CompiledFragment[Output] =
      func match
        case f: DbFunction2[_, _, _] if f.infixNotation => inner.build(func.params, s" ${func.dbName} ")
        case _ => inner.build(func.params, ", ").wrap(s"${func.dbName}(", ")")
