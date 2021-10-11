package cql4s
package compiler

import scala.Tuple.Concat
import scala.deriving.Mirror

trait QueryCompiler[-Q, Input, Output]:
  def build(query: Q): Query[Input, Output]

object QueryCompiler:
  given select[TableName, TableColumns, Columns, Output, Where <: LogicalExpr, I1 <: Tuple, I2 <: Tuple, Input] (
    using
    fields: ListFragment[FieldFragment, Columns, I1],
    where: LogicalExprFragment[Where, I2],
    encoder: EncoderAdapter[I1 Concat I2, Input],
    decoder: DecoderAdapter[Columns, Output]
  ): QueryCompiler[Select[TableName, TableColumns, Columns, Where], Input, Output] with
    def build(select: Select[TableName, TableColumns, Columns, Where]): Query[Input, Output] =
      val fragment =
        fields
          .build(select.fields, ", ")
          .wrap("SELECT ", s" FROM ${select.table.name.escaped}") ++
        where.build(select.where).prepend("WHERE ")

      Query(fragment.cql, encoder, decoder)
