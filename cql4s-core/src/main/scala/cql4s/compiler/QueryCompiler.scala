package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait QueryCompiler[-Q, Input, Output]:
  def build(query: Q): Query[Input, Output]

trait QueryFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object QueryFragment:
  given select[Keyspace, TableName, TableColumns, Columns, Output, Where <: LogicalExpr, GroupBy, I1 <: Tuple, I2 <: Tuple, I3 <: Tuple] (
    using
    fields: ListFragment[FieldFragment, Columns, I1],
    where: LogicalExprFragment[Where, I2],
    groupBy: ListFragment[FieldFragment, GroupBy, I3]
  ): QueryFragment[Select[Keyspace, TableName, TableColumns, Columns, Where, GroupBy], I1 Concat I2 Concat I3] with
    def build(select: Select[Keyspace, TableName, TableColumns, Columns, Where, GroupBy]): CompiledFragment[I1 Concat I2 Concat I3] =
      fields
        .build(select.fields, ", ")
        .wrap("SELECT ", s" FROM ${select.table.keyspace.escaped}.${select.table.name.escaped}") ++
        where.build(select.where).prepend("WHERE ") ++
        groupBy.build(select.groupBy, ", ").prepend("GROUP BY ") ++
        Option.when(select.allowFiltering)("ALLOW FILTERING")


object QueryCompiler:
  given [Fields, S <: Select[_, _, Fields, _, _, _], RawInput <: Tuple, Input, Output] (
    using
    fragment: QueryFragment[S, RawInput],
    encoder: EncoderAdapter[RawInput, Input],
    decoder: DecoderAdapter[Fields, Output]
  ): QueryCompiler[S, Input, Output] with
    def build(select: S): Query[Input, Output] =
      Query(fragment.build(select).cql, encoder, decoder)
