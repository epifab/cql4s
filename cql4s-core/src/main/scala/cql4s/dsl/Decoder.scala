package cql4s.dsl

import com.datastax.oss.driver.api.core.data.GettableByIndex

trait Decoder[T]:
  def decode(row: GettableByIndex): T
  def map[U](f: T => U): Decoder[U] = DecoderMap(this, f)

class DecoderMap[T, U](tDec: Decoder[T], map: T => U) extends Decoder[U]:
  def decode(row: GettableByIndex): U = map(tDec.decode(row))

trait DecoderAdapter[-Raw, T] extends Decoder[T]:
  override final def decode(row: GettableByIndex): T = decode(row, 0)
  private[dsl] def decode(row: GettableByIndex, state: Int): T

trait DefaultDecoderAdapter[-Raw, T] extends DecoderAdapter[Raw, T]:
  def size: Int

object DefaultDecoderAdapter:
  given empty: DefaultDecoderAdapter[EmptyTuple, EmptyTuple] with
    override val size: Int = 0
    def decode(row: GettableByIndex, state: Int): EmptyTuple = EmptyTuple

  given nonEmpty[RawHead, Head, RawTail <: Tuple, Tail <: Tuple](
    using
    head: DefaultDecoderAdapter[RawHead, Head],
    tail: DefaultDecoderAdapter[RawTail, Tail]
  ): DefaultDecoderAdapter[RawHead *: RawTail, Head *: Tail] with
    override val size: Int = head.size + tail.size
    def decode(row: GettableByIndex, state: Int): Head *: Tail =
      head.decode(row, state) *: tail.decode(row, state + head.size)

  given field[Type, F <: Field[Type], JT, ST](using dt: DataTypeCodec[Type, JT, ST]): DefaultDecoderAdapter[F, ST] with
    override val size: Int = 1
    def decode(row: GettableByIndex, state: Int): ST =
      dt.decode(row.get(state, dt.driverCodec))

trait LowPriorityDecoderAdapter:
  given default[A, B](using base: DefaultDecoderAdapter[A, B]): DecoderAdapter[A, B] = base

object DecoderAdapter extends LowPriorityDecoderAdapter:
  def apply[Raw, Output](raw: Raw)(using da: DecoderAdapter[Raw, Output]): Decoder[Output] = da

  given unit: DecoderAdapter[EmptyTuple, Unit] with
    def decode(row: GettableByIndex, state: Int): Unit = ()

  given singleElement[Raw, A](using default: DefaultDecoderAdapter[Raw, A]): DecoderAdapter[Raw *: EmptyTuple, A] with
    def decode(row: GettableByIndex, state: Int): A = default.decode(row, state)
