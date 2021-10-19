package cql4s.dsl

trait DbIdentifier[T]:
  def value: String
  // should check all reserved words too
  def escaped: String = if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) value else s""""$value""""

object DbIdentifier:
  given[A <: String](using singleton: ValueOf[A]): DbIdentifier[A] with
    val value: String = singleton.value
