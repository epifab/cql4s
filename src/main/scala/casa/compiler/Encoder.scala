package casa
package compiler

import com.datastax.oss.driver.api.core.cql.Row

trait Encoder[T]:
  def encode(params: T): List[Any]


trait EncoderAdapter[Raw, T] extends Encoder[T]

object EncoderAdapter:
  given empty: EncoderAdapter[EmptyTuple, EmptyTuple] with
    def encode(params: EmptyTuple): List[Any] = Nil

  given nonEmpty[RawHead, Head, RawTail <: Tuple, Tail <: Tuple](using head: EncoderAdapter[RawHead, Head], tail: EncoderAdapter[RawTail, Tail]): EncoderAdapter[RawHead *: RawTail, Head *: Tail] with
    def encode(params: Head *: Tail): List[Any] =
      head.encode(params.head) ++ tail.encode(params.tail)

  given placeholder[Name, Type, JT, ST](using dbt: DbType.Aux[Type, JT, ST]): EncoderAdapter[Placeholder[Name, Type], ST] with
    def encode(params: ST): List[Any] = List(dbt.encode(params))
