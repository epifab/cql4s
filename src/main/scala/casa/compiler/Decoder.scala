package casa
package compiler

import com.datastax.oss.driver.api.core.cql.Row

trait Decoder[T]:
  def decode(row: Row): T
  def map[U](f: T => U): Decoder[U] = DecoderMap(this, f)

class DecoderMap[T, U](tDec: Decoder[T], map: T => U) extends Decoder[U]:
  def decode(row: Row): U = map(tDec.decode(row))


trait DecoderAdapter[Raw, T] extends Decoder[T]:
  override final def decode(row: Row): T = decode(row, 0)
  def decode(row: Row, state: Int): T

object DecoderAdapter:
  given empty: DecoderAdapter[EmptyTuple, EmptyTuple] with
    def decode(row: Row, state: Int): EmptyTuple = EmptyTuple

  given nonEmpty[RawHead, Head, RawTail <: Tuple, Tail <: Tuple](using head: DecoderAdapter[RawHead, Head], tail: DecoderAdapter[RawTail, Tail]): DecoderAdapter[RawHead *: RawTail, Head *: Tail] with
    def decode(row: Row, state: Int): Head *: Tail =
      head.decode(row, state) *: tail.decode(row, state + 1)

  given field[Type, F <: Field[Type], JT, ST](using dbt: DbType.Aux[Type, JT, ST]): DecoderAdapter[F, ST] with
    def decode(row: Row, state: Int): ST =
      dbt.decode(row.get(state, dbt.codec))
