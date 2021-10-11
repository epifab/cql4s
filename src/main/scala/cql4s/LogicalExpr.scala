package cql4s

sealed trait LogicalExpr:
  def and[F2 <: LogicalExpr](otherExpression: F2): And[this.type, F2] = And(this, otherExpression)
  def or[F2 <: LogicalExpr](otherExpression: F2): Or[this.type, F2] = Or(this, otherExpression)

sealed trait AlwaysTrue extends LogicalExpr

case object AlwaysTrue extends AlwaysTrue

sealed trait LogicalExpr1[+F] extends LogicalExpr:
  def expr: F

sealed trait LogicalExpr2[+F1, +F2] extends LogicalExpr:
  def left: F1
  def right: F2

case class And[+F1 <: LogicalExpr, +F2 <: LogicalExpr](left: F1, right: F2)
  extends LogicalExpr2[F1, F2]

case class Or[+F1 <: LogicalExpr, +F2 <: LogicalExpr](left: F1, right: F2)
  extends LogicalExpr2[F1, F2]

sealed trait Comparison[+F1 <: Field[_], +F2 <: Field[_]] extends LogicalExpr2[F1, F2]

case class Equals[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]

case class NotEquals[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]

case class GreaterThan[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]

case class LessThan[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]

case class GreaterThanOrEqual[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]

case class LessThanOrEqual[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using AreComparable[F1, F2])
  extends Comparison[F1, F2]
