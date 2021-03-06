package cql4s.examples.readme

import com.datastax.oss.driver.api.core.ConsistencyLevel
import cql4s.CassandraRuntimeAlgebra
import cql4s.dsl.*

import java.util.{Currency, UUID}
import scala.util.chaining.*

class EventsRepo[F[_], S[_]](using CassandraRuntimeAlgebra[F, S]):
  val add: Event => F[Unit] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]
      .execute

  val updateTickets: (Map[Currency, BigDecimal], Metadata, UUID) => F[Unit] =
    Update(events)
      .set(e => (e("tickets"), e("metadata")))
      .where(_("id") === :?)
      .compile
      .execute
      .pipe(Function.untupled)

  val findByIds: List[UUID] => S[Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") in :?)
      .compile
      .pmap[Event]
      .stream
