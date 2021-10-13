package cql4s

final class Placeholder[T](using val dataType: DataType[T]) extends Field[T]

object :?

case class KeyValue[A <: String with Singleton, +T](key: A, value: T)

type ~~>[A <: String with Singleton, +T] = KeyValue[A, T]

extension[Key <: String with Singleton](key: Key)
  def ~~>[T](value: T): KeyValue[Key, T] = KeyValue(key, value)
