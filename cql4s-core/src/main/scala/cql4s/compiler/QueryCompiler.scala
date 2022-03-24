package cql4s.compiler

import cql4s.dsl.*

import scala.Tuple.Concat

trait QueryCompiler[-Q, Input, Output]:
  def build(query: Q): Query[Input, Output]

trait QueryFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object QueryFragment:
  given select[Keyspace, TableName, TableColumns, Fields, Output, Where <: LogicalExpr, GroupBy, OrderBy, Limit, PerPartitionLimit, I1 <: Tuple, I2 <: Tuple, I3 <: Tuple, I4 <: Tuple, I5 <: Tuple, I6 <: Tuple] (
    using
    fields: ListFragment[SelectorFragment, Fields, I1],
    where: LogicalExprFragment[Where, I2],
    groupBy: ListFragment[ColumnFragment, GroupBy, I3],
    orderBy: ListFragment[OrderByFragment, OrderBy, I4],
    limit: OptionalInputFragment[Limit, I5],
    perPartitionLimit: OptionalInputFragment[PerPartitionLimit, I6]
  ): QueryFragment[Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit], I1 Concat I2 Concat I3 Concat I4 Concat I5 Concat I6] with
    def build(select: Select[Keyspace, TableName, TableColumns, Fields, Where, GroupBy, OrderBy, Limit, PerPartitionLimit]): CompiledFragment[I1 Concat I2 Concat I3 Concat I4 Concat I5 Concat I6] =
      fields
        .build(select.fields, ", ")
        .orElse("(int)1")
        .wrap("SELECT ", s" FROM ${select.table.keyspace.escaped}.${select.table.name.escaped}") ++
        where.build(select.where).prependOpt("WHERE ") ++
        groupBy.build(select.groupBy, ", ").prependOpt("GROUP BY ") ++
        orderBy.build(select.orderBy, ", ").prependOpt("ORDER BY ") ++
        limit.build(select.limit).prependOpt("LIMIT ") ++
        perPartitionLimit.build(select.perPartitionLimit).prependOpt("PER PARTITION LIMIT ") ++
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
      Query(fragment.cql, encoder(fragment.input), decoder, None)
