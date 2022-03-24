package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait NonEmptyListFragment[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b], -T, I <: Tuple]:
  def build(x: T, separation: String): CompiledFragment[I]


object NonEmptyListFragment:
  given one[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b], HeadInput, HeadOutput <: Tuple](
    using
    headCompiler: BaseCompiler[HeadInput, HeadOutput]
  ): NonEmptyListFragment[BaseCompiler, HeadInput *: EmptyTuple, HeadOutput] with
    def build(tuple: HeadInput *: EmptyTuple, separation: String): CompiledFragment[HeadOutput] =
      headCompiler.build(tuple.head)

  given twoOrMore[BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b], HeadInput, HeadOutput <: Tuple, TailInput <: Tuple, TailOutput <: Tuple](
    using
    headCompiler: BaseCompiler[HeadInput, HeadOutput],
    tailCompier: ListFragment[BaseCompiler, TailInput, TailOutput]
  ): NonEmptyListFragment[BaseCompiler, HeadInput *: TailInput, HeadOutput Concat TailOutput] with
    def build(tuple: HeadInput *: TailInput, separation: String): CompiledFragment[HeadOutput Concat TailOutput] =
      headCompiler.build(tuple.head).concatenateOpt(tailCompier.build(tuple.tail, separation), separation)

  given single[X, XO <: Tuple, BaseCompiler[a, b <: Tuple] <: FragmentCompiler[a, b]] (
    using
    compiler: BaseCompiler[X, XO]
  ): NonEmptyListFragment[BaseCompiler, X, XO] with
    def build(x: X, separation: String): CompiledFragment[XO] =
      compiler.build(x)
