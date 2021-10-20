package cql4s.dsl

import com.datastax.oss.driver.api.core.cql.Row

trait Encoder[T]:
  def encode(params: T): List[Any]
  def contramap[U](f: U => T): Encoder[U] = EncoderContramap(this, f)

class EncoderImpl[A](val f: A => List[Any]) extends Encoder[A]:
  def encode(params: A): List[Any] = f(params)

class EncoderContramap[U, T](tEnc: Encoder[T], contramap: U => T) extends Encoder[U]:
  def encode(params: U): List[Any] = tEnc.encode(contramap(params))


/**
 * Builds and Encoder[T] for Raw
 * @tparam Raw The raw DSL object
 * @tparam T The output type
 */
trait EncoderFactory[-Raw, T]:
  def apply(raw: Raw): Encoder[T]

trait DefaultEncoderFactory[-Raw, T] extends EncoderFactory[Raw, T]

object DefaultEncoderFactory:
  given empty: DefaultEncoderFactory[EmptyTuple, EmptyTuple] with
    def apply(raw: EmptyTuple): Encoder[EmptyTuple] = EncoderImpl(_ => Nil)

  given nonEmpty[RawHead, RawTail <: Tuple, Head, Tail <: Tuple](
    using
    head: DefaultEncoderFactory[RawHead, Head],
    tail: DefaultEncoderFactory[RawTail, Tail]
  ): DefaultEncoderFactory[RawHead *: RawTail, Head *: Tail] with
    def apply(raw: RawHead *: RawTail): Encoder[Head *: Tail] =
      EncoderImpl(value => head(raw.head).encode(value.head) ++ tail(raw.tail).encode(value.tail))

  given placeholder[Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): DefaultEncoderFactory[Placeholder[Type], ST] with
    def apply(raw: Placeholder[Type]): Encoder[ST] =
      EncoderImpl(value => List(dt.encode(value)))

  // todo: this is required somehow by the udt, otherwise it's completely unnecessary
  given column[Name, Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): DefaultEncoderFactory[Column[Name, Type], ST] with
    def apply(raw: Column[Name, Type]): Encoder[ST] =
      EncoderImpl(value => List(dt.encode(value)))

  given const[Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): DefaultEncoderFactory[Const[Type], EmptyTuple] with
    def apply(raw: Const[Type]): Encoder[EmptyTuple] =
      EncoderImpl(_ => List(raw.encoded))


trait LowPriorityEncoderAdapter:
  given default[A, B](using base: DefaultEncoderFactory[A, B]): EncoderFactory[A, B] = base

object EncoderFactory extends LowPriorityEncoderAdapter:
  def apply[A, B](x: A)(using enc: EncoderFactory[A, B]): Encoder[B] = enc(x)

  given unit: EncoderFactory[EmptyTuple, Unit] with
    def apply(raw: EmptyTuple): Encoder[Unit] =
      EncoderImpl(_ => Nil)

  given singleElement[Raw, A](using default: DefaultEncoderFactory[Raw, A]): EncoderFactory[Raw *: EmptyTuple, A] with
    def apply(raw: Raw *: EmptyTuple): Encoder[A] = default(raw.head)
