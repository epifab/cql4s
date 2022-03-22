package cql4s.dsl

import cql4s.dsl.props.{AreComparable, CanContain}

trait LogicalExpr:
  def and[E2 <: LogicalExpr](otherExpression: E2): And[this.type, E2] = And(this, otherExpression)
  def or[E2 <: LogicalExpr](otherExpression: E2): Or[this.type, E2] = Or(this, otherExpression)

sealed trait AlwaysTrue extends LogicalExpr
case object AlwaysTrue extends AlwaysTrue

sealed trait LogicalExpr1[+E] extends LogicalExpr:
  def expr: E

sealed trait LogicalExpr2[+E1, +E2] extends LogicalExpr:
  def left: E1
  def right: E2

case class And[+E1 <: LogicalExpr, +E2 <: LogicalExpr](left: E1, right: E2)
  extends LogicalExpr2[E1, E2]

case class Or[+E1 <: LogicalExpr, +E2 <: LogicalExpr](left: E1, right: E2)
  extends LogicalExpr2[E1, E2]

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

case class In[+F1 <: Field[_], +F2 <: Field[_]](left: F1, right: F2)(using CanContain[F2, F1])
  extends Comparison[F1, F2]
