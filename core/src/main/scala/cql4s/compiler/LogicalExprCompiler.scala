package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait LogicalExprFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object LogicalExprFragment:
  given alwaysTrue: LogicalExprFragment[AlwaysTrue, EmptyTuple] with
    def build(alwaysTrue: AlwaysTrue): CompiledFragment[EmptyTuple] = CompiledFragment.empty

  given and[E1 <: LogicalExpr, E2 <: LogicalExpr, E <: LogicalExpr2[E1, E2], T1 <: Tuple, T2 <: Tuple](
    using
    left: LogicalExprFragment[E1, T1],
    right: LogicalExprFragment[E2, T2]
  ): LogicalExprFragment[And[E1, E2], T1 Concat T2] with
    def build(e: And[E1, E2]): CompiledFragment[T1 Concat T2] =
      left.build(e.left).concatenateOptional(right.build(e.right), " AND ")

  given or[E1 <: LogicalExpr, E2 <: LogicalExpr, E <: LogicalExpr2[E1, E2], T1 <: Tuple, T2 <: Tuple](
    using
    left: LogicalExprFragment[E1, T1],
    right: LogicalExprFragment[E2, T2]
  ): LogicalExprFragment[Or[E1, E2], T1 Concat T2] with
    def build(e: Or[E1, E2]): CompiledFragment[T1 Concat T2] =
      left.build(e.left).concatenateOptional(right.build(e.right), " OR ")

  given comparison[F1 <: Field[_], F2 <: Field[_], E <: Comparison[F1, F2], P <: Tuple, Q <: Tuple](
    using
    left: FieldFragment[F1, P],
    right: FieldFragment[F2, Q]
  ): LogicalExprFragment[E, P Concat Q] with
    def build(filter: E): CompiledFragment[P Concat Q] =
      val e1 = left.build(filter.left)
      val e2 = right.build(filter.right)
      filter match
        case _: Equals[_, _] => e1.concatenateRequired(e2, " = ")
        case _: NotEquals[_, _] => e1.concatenateRequired(e2, " <> ")
        case _: GreaterThan[_, _] => e1.concatenateRequired(e2, " > ")
        case _: LessThan[_, _] => e1.concatenateRequired(e2, " < ")
        case _: GreaterThanOrEqual[_, _] => e1.concatenateRequired(e2, " >= ")
        case _: LessThanOrEqual[_, _] => e1.concatenateRequired(e2, " <= ")
