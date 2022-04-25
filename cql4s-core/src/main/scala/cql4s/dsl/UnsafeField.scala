package cql4s.dsl

class UnsafeField[T](val plainCql: String)(using override val dataType: DataType[T]) extends Field[T]
