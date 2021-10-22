package cql4s.dsl

import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.CassandraRuntime

import scala.deriving.Mirror

class Command[Input](val cql: String, val encoder: Encoder[Input]):

  def contramap[I](f: I => Input): Command[I] = Command(cql, encoder.contramap(f))

  def pcontramap[P <: Product](using m: Mirror.ProductOf[P], i: m.MirroredElemTypes =:= Input): Command[P] =
    contramap(p => i(Tuple.fromProductTyped(p)))

  def execute[F[_], S[_]](using cassandra: CassandraRuntime[F, S]): Input => F[Unit] =
    cassandra.execute(this)

  def executeBatch[F[_], S[_]](batchType: BatchType)(using cassandra: CassandraRuntime[F, S]): Iterable[Input] => F[Unit] =
    cassandra.executeBatch(this, batchType)
