package cql4s.dsl

import com.datastax.oss.driver.api.core.ConsistencyLevel
import cql4s.CassandraRuntimeAlgebra

import scala.deriving.Mirror

class Query[Input, Output](val cql: String, val encoder: Encoder[Input], val decoder: Decoder[Output], val consistencyLevel: Option[ConsistencyLevel]):
  override val toString: String = cql

  def withConsistencyLevel(consistencyLevel: ConsistencyLevel): Query[Input, Output] =
    Query(cql, encoder, decoder, Some(consistencyLevel))

  def map[O](f: Output => O): Query[Input, O] = Query(cql, encoder, decoder.map(f), consistencyLevel)

  def pmap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Output, toProduct: Output <:< Product): Query[Input, P] =
    map[P]((x: Output) => m.fromProduct(toProduct(x)))

  def contramap[I](f: I => Input): Query[I, Output] = Query(cql, encoder.contramap(f), decoder, consistencyLevel)

  def pcontramap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Input): Query[P, Output] =
    contramap(p => i(Tuple.fromProductTyped(p)))

  def stream[F[_], S[_]](using cassandra: CassandraRuntimeAlgebra[F, S]): Input => S[Output] =
    cassandra.stream(this)

  def option[F[_], S[_]](using cassandra: CassandraRuntimeAlgebra[F, S]): Input => F[Option[Output]] =
    cassandra.option(this)

  def one[F[_], S[_]](using cassandra: CassandraRuntimeAlgebra[F, S]): Input => F[Output] =
    cassandra.one(this)
