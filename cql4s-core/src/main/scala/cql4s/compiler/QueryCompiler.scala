package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait QueryCompiler[-Q, Input, Output]:
  def build(query: Q): Query[Input, Output]

trait QueryFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object QueryFragment:
  given select[Keyspace, TableName, TableColumns, Fields, Output, Where <: LogicalExpr, GroupBy, OrderBy, Limit, PerPartitionLimit, I1 <: Tuple, I2 <: Tuple, I3 <: Tuple, I4 <: Tuple, I5 <: Tuple, I6 <: Tuple] (
    using
    fields: ListFragment[FieldFragment, Fields, I1],
    where: LogicalExprFragment[Where, I2],
    groupBy: ListFragment[FieldFragment, GroupBy, I3],
    orderBy: ListFragment[OrderByFragment, OrderBy, I4],
    limit: OptionalInputFragment[Limit, I5],
    perPartitionLimit: OptionalInputFragment[PerPartitionLimit, I6]
  ): QueryFragment[Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit], I1 Concat I2 Concat I3 Concat I4 Concat I5 Concat I6] with
    def build(select: Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit]): CompiledFragment[I1 Concat I2 Concat I3 Concat I4 Concat I5 Concat I6] =
      fields
        .build(select.fields, ", ")
        .orElse("1")
        .wrap("SELECT ", s" FROM ${select.table.keyspace.escaped}.${select.table.name.escaped}") ++
        where.build(select.where).prepend("WHERE ") ++
        groupBy.build(select.groupBy, ", ").prepend("GROUP BY ") ++
        orderBy.build(select.orderBy, ", ").prepend("ORDER BY ") ++
        limit.build(select.limit).prepend("LIMIT ") ++
        perPartitionLimit.build(select.perPartitionLimit).prepend("PER PARTITION LIMIT ") ++
        Option.when(select.allowFiltering)("ALLOW FILTERING")


object QueryCompiler:
  given [Fields, S <: Select[_, _, _, Fields, _, _, _, _, _], RawInput <: Tuple, Input, Output] (
    using
    queryFragment: QueryFragment[S, RawInput],
    encoder: EncoderFactory[RawInput, Input],
    decoder: DecoderAdapter[Fields, Output]
  ): QueryCompiler[S, Input, Output] with
    def build(select: S): Query[Input, Output] =
      val fragment = queryFragment.build(select)
      Query(fragment.cql, encoder(fragment.input), decoder)
