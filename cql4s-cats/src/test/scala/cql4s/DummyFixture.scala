package cql4s

import cql4s.dsl.*
import cql4s.test.schema.dummy

object DummyFixture:
  val select = Select.from(dummy).where(_("id") === :?)

  val insert = Insert.into(dummy).fields(_("id")).compile
