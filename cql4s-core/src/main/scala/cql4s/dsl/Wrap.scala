package cql4s.dsl

case class LogicalEpxrWrap[+E <: LogicalExpr](expr: E) extends LogicalExpr

case class FieldWrap[T, +F <: Field[T]](expr: F) extends Field[T]:
  override val dataType: DataType[T] = expr.dataType

sealed trait Wrap:
  def apply[T, F <: Field[T]](f: F): FieldWrap[T, F] = FieldWrap(f)
  def apply[E <: LogicalExpr](e: E): LogicalEpxrWrap[E] = LogicalEpxrWrap(e)

object << extends Wrap
