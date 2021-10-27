package cql4s.dsl

object EncoderSpec:
  EncoderFactory(Placeholder[varchar])
    .encode("yo")

  EncoderFactory((Placeholder[varchar], Placeholder[int]))
    .encode(("yo", 123))

  EncoderFactory((4[int], Placeholder[varchar], 3[int]))
    .encode("hello")
