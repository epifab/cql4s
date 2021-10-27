package cql4s.dsl

class Cast[+F <: Field[_], U](val field: F)(using val dataType: DataType[U]) extends Field[U]
