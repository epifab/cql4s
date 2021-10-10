package cql4s
package compiler

import Tuple.Concat

trait ListFragment[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b], -T, I <: Tuple]:
  def build(x: T, separation: String): CompiledFragment[I]

object ListFragment:
  given emptyTuple[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b]]: ListFragment[BaseCompiler, EmptyTuple, EmptyTuple] with
    override def build(x: EmptyTuple, separation: String): CompiledFragment[EmptyTuple] = CompiledFragment.empty

  given nonEmptyTuple[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b], HeadInput, HeadOutput <: Tuple, TailInput <: Tuple, TailOutput <: Tuple] (
    using
    headCompiler: BaseCompiler[HeadInput, HeadOutput],
    tailCompier: ListFragment[BaseCompiler, TailInput, TailOutput]
  ): ListFragment[BaseCompiler, HeadInput *: TailInput, HeadOutput Concat TailOutput] with
    def build(tuple: HeadInput *: TailInput, separation: String): CompiledFragment[HeadOutput Concat TailOutput] =
      headCompiler.build(tuple.head).concatenateOptional(tailCompier.build(tuple.tail, separation), separation)

  given single[X, XO <: Tuple, BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b]] (
    using
    compiler: BaseCompiler[X, XO]
  ): ListFragment[BaseCompiler, X, XO] with
    def build(x: X, separation: String): CompiledFragment[XO] =
      compiler.build(x)
