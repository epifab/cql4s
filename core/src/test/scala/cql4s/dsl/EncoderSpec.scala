package cql4s.dsl

object EncoderSpec:
  EncoderAdapter(Placeholder[varchar])
    .encode("yo")

  EncoderAdapter((Placeholder[varchar], Placeholder[int]))
    .encode(("yo", 123))
