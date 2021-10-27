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

object EncoderFactory:
  def apply[Raw, A](raw: Raw)(using factory: EncoderFactory[Raw, A]): Encoder[A] = factory(raw)

  // Combines two encoders into one. All Encoder[Unit] will be absorbed
  trait CombinedEncoder[E1, E2, E]:
    def apply(e1: Encoder[E1], e2: Encoder[E2]): Encoder[E]

  trait SimpleCombiner:
    given simple[A, B]: CombinedEncoder[A, B, (A, B)] with
      def apply(e1: Encoder[A], e2: Encoder[B]): Encoder[(A, B)] =
        EncoderImpl((t: (A, B)) => e1.encode(t(0)) ++ e2.encode(t(1)))

  trait TupleCombiner extends SimpleCombiner:
    given tuple[A, B <: Tuple]: CombinedEncoder[A, B, A *: B] with
      def apply(e1: Encoder[A], e2: Encoder[B]): Encoder[A *: B] =
        EncoderImpl((t: A *: B) => e1.encode(t.head) ++ e2.encode(t.tail))

  trait UnitRightCombiner extends TupleCombiner:
    given unitRight[A]: CombinedEncoder[A, Unit, A] with
      def apply(e1: Encoder[A], e2: Encoder[Unit]): Encoder[A] =
        EncoderImpl((a: A) => e1.encode(a) ++ e2.encode(()))

  object CombinedEncoder extends UnitRightCombiner:
    given unitLeft[A]: CombinedEncoder[Unit, A, A] with
      def apply(e1: Encoder[Unit], e2: Encoder[A]): Encoder[A] =
        EncoderImpl((a: A) => e1.encode(()) ++ e2.encode(a))

  given placeholder[Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): EncoderFactory[Placeholder[Type], ST] with
    def apply(raw: Placeholder[Type]): Encoder[ST] =
      EncoderImpl(value => List(dt.encode(value)))

  given const[Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): EncoderFactory[Const[Type], Unit] with
    def apply(raw: Const[Type]): Encoder[Unit] =
      EncoderImpl(_ => List(raw.encoded))

  given empty: EncoderFactory[EmptyTuple, Unit] with
    def apply(raw: EmptyTuple): Encoder[Unit] = EncoderImpl(_ => Nil)

  given nonEmpty[RawHead, RawTail <: Tuple, Head, Tail, Output](
    using
    head: EncoderFactory[RawHead, Head],
    tail: EncoderFactory[RawTail, Tail],
    combined: CombinedEncoder[Head, Tail, Output]
  ): EncoderFactory[RawHead *: RawTail, Output] with
    def apply(raw: RawHead *: RawTail): Encoder[Output] =
      EncoderImpl((value: Output) => combined(head(raw.head), tail(raw.tail)).encode(value))

trait ColumnsEncoderFactory[Raw, Output] extends EncoderFactory[Raw, Output]

object ColumnsEncoderFactory:
  given column[Name, Type, JT, ST](using dt: DataTypeCodec[Type, JT, ST]): ColumnsEncoderFactory[Column[Name, Type], ST] with
    def apply(raw: Column[Name, Type]): Encoder[ST] =
      EncoderImpl(value => List(dt.encode(value)))

  given empty: ColumnsEncoderFactory[EmptyTuple, EmptyTuple] with
    def apply(raw: EmptyTuple): Encoder[EmptyTuple] = EncoderImpl(_ => Nil)

  given nonEmpty[RawHead, RawTail <: Tuple, Head, Tail <: Tuple](
    using
    head: ColumnsEncoderFactory[RawHead, Head],
    tail: ColumnsEncoderFactory[RawTail, Tail]
  ): ColumnsEncoderFactory[RawHead *: RawTail, Head *: Tail] with
    def apply(raw: RawHead *: RawTail): Encoder[Head *: Tail] =
      EncoderImpl((value: Head *: Tail) => head(raw.head).encode(value.head) ++ tail(raw.tail).encode(value.tail))
