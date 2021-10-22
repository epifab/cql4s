package cql4s

import com.datastax.oss.driver.api.core.CqlSession

import java.net.InetSocketAddress
import scala.util.chaining.*

case class CassandraCredentials(username: String, password: String)

case class CassandraConfig(
  host: String,
  port: Int,
  credentials: Option[CassandraCredentials],
  keyspace: Option[String],
  datacenter: String
):
  protected [cql4s] def getSession(): CqlSession =
    CqlSession.builder()
      .addContactPoint(new InetSocketAddress(host, port))
      .withLocalDatacenter(datacenter)
      .pipe { builder => credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
      .pipe { builder => keyspace.fold(builder)(keyspace => builder.withKeyspace(keyspace)) }
      .build()
