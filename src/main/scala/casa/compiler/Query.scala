package casa.compiler

import scala.deriving.Mirror

import casa.Select

class Query[Input, Output](val cql: String, val encoder: Encoder[Input], val decoder: Decoder[Output]):
  def map[O](f: Output => O): Query[Input, O] = Query(cql, encoder, decoder.map(f))

  def pmap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Output, toProduct: Output <:< Product): Query[Input, P] =
    map[P]((x: Output) => m.fromProduct(toProduct(x)))

  def contramap[I](f: I => Input): Query[I, Output] = Query(cql, encoder.contramap(f), decoder)

  def pcontramap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Input): Query[P, Output] =
    contramap(p => i(Tuple.fromProductTyped(p)))


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
