package cql4s.utils

import com.datastax.oss.driver.api.core.data.{GettableByIndex, TupleValue}
import cql4s.dsl.{DataType, DataTypeCodec}

trait TupleCodecs[Types]:
  def toList: List[DataType[_]]

object TupleCodecs:
  given one[A](using dt: DataType[A]): TupleCodecs[A *: EmptyTuple] with
    val toList: List[DataType[_]] = List(dt)

  given many[A, B <: Tuple](using dt: DataType[A], codecs: TupleCodecs[B]): TupleCodecs[A *: B] with
    val toList: List[DataType[_]] = dt :: codecs.toList


trait TupleDecoder[Types, Output]:
  def decode(row: TupleValue): Output = decode(row, 0)
  private[utils] def decode(row: GettableByIndex, state: Int): Output

object TupleDecoder:
  given TupleDecoder[EmptyTuple, EmptyTuple] with
    def decode(row: GettableByIndex, state: Int): EmptyTuple = EmptyTuple

  given [Head, Tail <: Tuple, J, SHead, STail <: Tuple](
    using
    dataType: DataTypeCodec[Head, J, SHead],
    tailDecoder: TupleDecoder[Tail, STail]
  ): TupleDecoder[Head *: Tail, SHead *: STail] with
    def decode(row: GettableByIndex, state: Int): SHead *: STail =
      dataType.decode(row.get(state, dataType.driverCodec)) *: tailDecoder.decode(row, state + 1)

trait TupleEncoder[Types, Output]:
  def encode(output: Output): List[Any]

object TupleEncoder:
  given TupleEncoder[EmptyTuple, EmptyTuple] with
    override def encode(output: EmptyTuple): List[Any] = Nil

  given [T, J, S, TTail <: Tuple, STail <: Tuple](
    using
    dataType: DataTypeCodec[T, J, S],
    tail: TupleEncoder[TTail, STail]
  ): TupleEncoder[T *: TTail, S *: STail] with
    override def encode(output: S *: STail): List[Any] =
      dataType.encode(output.head) :: tail.encode(output.tail)
