package cql4s.compiler

import cql4s.dsl.*

trait OptionalInputFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object OptionalInputFragment:
  given empty: OptionalInputFragment[None.type, EmptyTuple] with
    override def build(x: None.type) = CompiledFragment.empty

  given placeholder[T, Input <: Tuple](using fragment: FieldFragment[Placeholder[T], Input]): OptionalInputFragment[Placeholder[T], Input] with
    override def build(x: Placeholder[T]) = fragment.build(x)

  given const[T, Input <: Tuple](using fragment: FieldFragment[Const[T], Input]): OptionalInputFragment[Const[T], Input] with
    override def build(x: Const[T]) = fragment.build(x)
