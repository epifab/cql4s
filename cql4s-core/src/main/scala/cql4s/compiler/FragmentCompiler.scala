package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait FragmentCompiler[-Target, Input <: Tuple]:
  def build(x: Target): CompiledFragment[Input]

case class CompiledFragment[Input <: Tuple](parts: List[String], input: Input):

  lazy val cql: String = parts.mkString

  def `++`(other: String): CompiledFragment[Input] = appendOpt(" " + other)

  def `++`(other: Option[String]): CompiledFragment[Input] = appendOpt(other.map(" " + _))

  def `++`[I2 <: Tuple](other: CompiledFragment[I2]): CompiledFragment[Input Concat I2] = concatenateOpt(other, " ")

  def wrapOpt(before: String, after: String): CompiledFragment[Input] = CompiledFragment(if (parts.isEmpty) Nil else (before :: parts) :+ after, input)

  def appendOpt(after: String): CompiledFragment[Input] = CompiledFragment(if (parts.isEmpty) Nil else parts :+ after, input)

  def appendOpt(after: Option[String]): CompiledFragment[Input] = CompiledFragment(if (parts.isEmpty) Nil else parts ++ after.toList, input)

  def prependOpt(before: String): CompiledFragment[Input] = CompiledFragment(if (parts.isEmpty) Nil else before :: parts, input)

  def concatenateOpt[I2 <: Tuple](other: CompiledFragment[I2], separator: String): CompiledFragment[Input Concat I2] =
    CompiledFragment(
      (parts.isEmpty, other.parts.isEmpty) match
        case (false, false) => parts ++ (separator :: other.parts)
        case _ => parts ++ other.parts,
      input ++ other.input
    )

  def `++!`(other: String): CompiledFragment[Input] = append(" " + other)

  def `++!`(other: Option[String]): CompiledFragment[Input] = append(other.map(" " + _))

  def `++!`[I2 <: Tuple](other: CompiledFragment[I2]): CompiledFragment[Input Concat I2] = concatenate(other, " ")

  def wrap(before: String, after: String): CompiledFragment[Input] = CompiledFragment((before :: parts) :+ after, input)

  def append(after: String): CompiledFragment[Input] = CompiledFragment(parts :+ after, input)

  def append(after: Option[String]): CompiledFragment[Input] = CompiledFragment(parts ++ after.toList, input)

  def prepend(before: String): CompiledFragment[Input] = CompiledFragment(before :: parts, input)

  def concatenate[I2 <: Tuple](other: CompiledFragment[I2], separator: String): CompiledFragment[Input Concat I2] =
    CompiledFragment(
      (parts.isEmpty, other.parts.isEmpty) match
        case (false, false) => parts ++ (separator :: other.parts)
        case _ => Nil,
      input ++ other.input
    )

  def orElse(s: String): CompiledFragment[Input] =
    if (parts.isEmpty) CompiledFragment(s :: Nil, input) else this

object CompiledFragment:
  def empty: CompiledFragment[EmptyTuple] = CompiledFragment(Nil, EmptyTuple)
  def apply(const: String): CompiledFragment[EmptyTuple] = CompiledFragment(const :: Nil, EmptyTuple)
