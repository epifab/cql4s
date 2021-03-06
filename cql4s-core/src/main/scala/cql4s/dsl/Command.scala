package cql4s.dsl

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.CassandraRuntimeAlgebra

import scala.deriving.Mirror

class Command[Input](val cql: String, val encoder: Encoder[Input], val consistencyLevel: Option[ConsistencyLevel]):
  override val toString: String = cql

  def withConsistencyLevel(consistencyLevel: ConsistencyLevel): Command[Input] =
    Command(cql, encoder, Some(consistencyLevel))

  def contramap[I](f: I => Input): Command[I] = Command(cql, encoder.contramap(f), consistencyLevel)

  def pcontramap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Input): Command[P] =
    contramap(p => i(Tuple.fromProductTyped(p)))

  def execute[F[_], S[_]](using cassandra: CassandraRuntimeAlgebra[F, S]): Input => F[Unit] =
    cassandra.execute(this)

  def executeBatch[F[_], S[_]](batchType: BatchType)(using cassandra: CassandraRuntimeAlgebra[F, S]): Iterable[Input] => F[Unit] =
    cassandra.executeBatch(this, batchType)
