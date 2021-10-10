package casa
package compiler

import com.datastax.oss.driver.api.core.cql.Row

trait Decoder[T]:
  def decode(row: Row): T


trait DecoderAdapter[Raw, T] extends Decoder[T]:
  override final def decode(row: Row): T = decode(row, 0)
  def decode(row: Row, state: Int): T

object DecoderAdapter:
  given empty: DecoderAdapter[EmptyTuple, EmptyTuple] with
    def decode(row: Row, state: Int): EmptyTuple = EmptyTuple

  given nonEmpty[RawHead, Head, RawTail <: Tuple, Tail <: Tuple](using head: DecoderAdapter[RawHead, Head], tail: DecoderAdapter[RawTail, Tail]): DecoderAdapter[RawHead *: RawTail, Head *: Tail] with
    def decode(row: Row, state: Int): Head *: Tail =
      head.decode(row, state) *: tail.decode(row, state + 1)

  given column[Name, Type, JT, ST](using dbt: DbType.Aux[Type, JT, ST]): DecoderAdapter[Column[Name, Type], ST] with
    def decode(row: Row, state: Int): ST =
      println(s"About to decode ${dbt.dbName} for $state, ${row.getObject(state)}")
      dbt.decode(row.get(state, dbt.codec))
