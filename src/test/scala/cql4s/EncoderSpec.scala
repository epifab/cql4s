package cql4s

object EncoderSpec:
  EncoderAdapter(Placeholder[varchar])
    .encode("yo")

  EncoderAdapter((Placeholder[varchar], Placeholder[int]))
    .encode(("yo", 123))
