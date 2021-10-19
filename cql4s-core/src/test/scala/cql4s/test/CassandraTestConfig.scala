package cql4s.test

import cql4s.CassandraConfig

object CassandraTestConfig extends CassandraConfig(
  host = "0.0.0.0",
  port = 9042,
  credentials = None,
  keyspace = None,
  datacenter = "testdc"
)
