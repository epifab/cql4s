package casa

import scala.deriving.Mirror


class Query[Input, Output](val cql: String, val encoder: Encoder[Input], val decoder: Decoder[Output]):
  def map[O](f: Output => O): Query[Input, O] = Query(cql, encoder, decoder.map(f))

  def pmap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Output, toProduct: Output <:< Product): Query[Input, P] =
    map[P]((x: Output) => m.fromProduct(toProduct(x)))

  def contramap[I](f: I => Input): Query[I, Output] = Query(cql, encoder.contramap(f), decoder)

  def pcontramap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Input): Query[P, Output] =
    contramap(p => i(Tuple.fromProductTyped(p)))

