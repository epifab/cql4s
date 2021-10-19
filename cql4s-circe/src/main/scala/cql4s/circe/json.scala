package cql4s.circe

import cql4s.dsl.*
import io.circe.parser.decode as decodeJson
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

type json[T]

given [T](using Encoder[T], Decoder[T]): DataTypeCodec[json[T], String, T] = {
  DataType.textCodec.map[json[T], T](
    _.asJson.noSpaces,
    s => decodeJson[T](s).getOrElse(throw RuntimeException(s"Cannot decode json '$s'"))
  )
}
