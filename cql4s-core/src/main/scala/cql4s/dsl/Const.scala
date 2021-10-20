package cql4s.dsl

final class Const[T](val dataType: DataType[T], val value: dataType.ScalaType) extends Field[T]:
  def encoded: dataType.JavaType = dataType.encode(value)

extension[U](value: U)
  def apply[T](using dataType: DataType.Aux[T, U]): Const[T] = Const(dataType, value)
