package casa
package compiler

import scala.deriving.Mirror

import casa.Select

trait QueryCompiler[-Q, Input, Output]:
  def build(query: Q): Query[Input, Output]

object QueryCompiler:
  given select[TableName, TableColumns, Columns, Output, I1 <: Tuple, Input] (
    using
    fields: ListFragment[FieldFragment, Columns, I1],
    encoder: EncoderAdapter[I1, Input],
    decoder: DecoderAdapter[Columns, Output]
  ): QueryCompiler[Select[TableName, TableColumns, Columns], Input, Output] with
    def build(select: Select[TableName, TableColumns, Columns]): Query[Input, Output] =
      val cql = fields.build(select.fields, ", ")
        .wrap("SELECT ", s" FROM ${select.table.name.escaped}")
        .cql

      Query(cql, encoder, decoder)
