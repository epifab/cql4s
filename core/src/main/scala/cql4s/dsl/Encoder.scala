package cql4s.dsl

import com.datastax.oss.driver.api.core.cql.Row

trait Encoder[T]:
  def encode(params: T): List[Any]
  def contramap[U](f: U => T): Encoder[U] = EncoderContramap(this, f)

class EncoderContramap[U, T](tEnc: Encoder[T], contramap: U => T) extends Encoder[U]:
  def encode(params: U): List[Any] = tEnc.encode(contramap(params))

trait EncoderAdapter[-Raw, T] extends Encoder[T]

trait DefaultEncoderAdapter[-Raw, T] extends EncoderAdapter[Raw, T]

object DefaultEncoderAdapter:
  given empty: DefaultEncoderAdapter[EmptyTuple, EmptyTuple] with
    def encode(params: EmptyTuple): List[Any] = Nil

  given nonEmpty[RawHead, RawTail <: Tuple, Head, Tail <: Tuple](
    using
    head: DefaultEncoderAdapter[RawHead, Head],
    tail: DefaultEncoderAdapter[RawTail, Tail]
  ): DefaultEncoderAdapter[RawHead *: RawTail, Head *: Tail] with
    def encode(params: Head *: Tail): List[Any] =
      head.encode(params.head) ++ tail.encode(params.tail)

  given field[Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): DefaultEncoderAdapter[Field[Type], ST] with
    def encode(params: ST): List[Any] = List(dt.encode(params))

trait LowPriorityEncoderAdapter:
  given default[A, B](using base: DefaultEncoderAdapter[A, B]): EncoderAdapter[A, B] = base

object EncoderAdapter extends LowPriorityEncoderAdapter:
  def apply[A, B](x: A)(using enc: EncoderAdapter[A, B]): Encoder[B] = enc

  given unit: EncoderAdapter[EmptyTuple, Unit] with
    def encode(params: Unit): List[Any] = Nil

  given singleElement[Raw, A](using default: DefaultEncoderAdapter[Raw, A]): EncoderAdapter[Raw *: EmptyTuple, A] with
    def encode(params: A): List[Any] = default.encode(params)
