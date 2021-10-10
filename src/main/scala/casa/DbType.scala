package casa

import com.datastax.oss.driver.api.core.ProtocolVersion
import com.datastax.oss.driver.api.core.`type`.DataType
import com.datastax.oss.driver.api.core.`type`.codec.{TypeCodec, TypeCodecs}
import com.datastax.oss.driver.api.core.`type`.reflect.GenericType
import com.datastax.oss.driver.api.core.data.CqlDuration

import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.*
import java.util.UUID
import scala.collection.JavaConverters.*
import scala.concurrent.duration.Duration


trait DbType[T]:
  type JavaType
  type ScalaType
  def codec: TypeCodec[JavaType]
  def encode: ScalaType => JavaType
  def decode: JavaType => ScalaType
  def dbName: String


trait DbTypeMap[T, S, J](toJava: S => J, toScala: J => S) extends DbType[T]:
  type JavaType = J
  type ScalaType = S
  def encode: ScalaType => JavaType = toJava
  def decode: JavaType => ScalaType = toScala


trait SimpleDbType[T, U] extends DbType[T]:
  type JavaType = U
  type ScalaType = U
  def encode: ScalaType => JavaType = identity
  def decode: JavaType => ScalaType = identity


type ascii
type bigint
type blob
type boolean
type counter
type date
type decimal
type double
type duration
type float
type inet
type int
type smallint
type text
type time
type timestamp
type timeuuid
type tinyint
type uuid
type varchar
type varint

type map[K, V]
type list[T]
type set[T]


object DbType:
  type Aux[T, JT, ST] = DbType[T] {
    type JavaType = JT
    type ScalaType = ST 
  }

  given DbType[ascii] with SimpleDbType[ascii, String] with
    override val codec: TypeCodec[String] = TypeCodecs.ASCII
    override val dbName: String = "ascii"

  given DbType[bigint] with DbTypeMap[bigint, Long, java.lang.Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.BIGINT
    override val dbName: String = "bigint"

  given DbType[blob] with SimpleDbType[blob, ByteBuffer] with
    override val codec: TypeCodec[ByteBuffer] = TypeCodecs.BLOB
    override val dbName: String = "blob"

  given DbType[boolean] with DbTypeMap[boolean, Boolean, java.lang.Boolean](identity, identity) with
    override val codec: TypeCodec[java.lang.Boolean] = TypeCodecs.BOOLEAN
    override val dbName: String = "boolean"

  given DbType[counter] with DbTypeMap[counter, Long, java.lang.Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.COUNTER
    override val dbName: String = "counter"

  given DbType[date] with SimpleDbType[date, LocalDate] with
    override val codec: TypeCodec[LocalDate] = TypeCodecs.DATE
    override val dbName: String = "date"

  given DbType[decimal] with DbTypeMap[decimal, BigDecimal, java.math.BigDecimal](_.bigDecimal, identity) with
    override val codec: TypeCodec[java.math.BigDecimal] = TypeCodecs.DECIMAL
    override val dbName: String = "decimal"

  given DbType[double] with DbTypeMap[double, Double, java.lang.Double](identity, identity) with
    override val codec: TypeCodec[java.lang.Double] = TypeCodecs.DOUBLE
    override val dbName: String = "double"

  // This is implemented with CqlDuration, as it is effectively a mix of Period and Duration
  // given DbType[duration]

  given DbType[float] with DbTypeMap[float, Float, java.lang.Float](identity, identity) with
    override val codec: TypeCodec[java.lang.Float] = TypeCodecs.FLOAT
    override val dbName: String = "float"

  given DbType[inet] with SimpleDbType[inet, InetAddress] with
    override val codec: TypeCodec[InetAddress] = TypeCodecs.INET
    override val dbName: String = "inet"

  given DbType[int] with DbTypeMap[int, Int, java.lang.Integer](identity, identity) with
    override val codec: TypeCodec[java.lang.Integer] = TypeCodecs.INT
    override val dbName: String = "int"

  given DbType[smallint] with DbTypeMap[smallint, Short, java.lang.Short](identity, identity) with
    override val codec: TypeCodec[java.lang.Short] = TypeCodecs.SMALLINT
    override val dbName: String = "smallint"

  given DbType[text] with SimpleDbType[text, String] with
    override val codec: TypeCodec[String] = TypeCodecs.TEXT
    override val dbName: String = "text"

  given DbType[time] with SimpleDbType[time, LocalTime] with
    override val codec: TypeCodec[LocalTime] = TypeCodecs.TIME
    override val dbName: String = "time"

  given DbType[timestamp] with SimpleDbType[timestamp, Instant] with
    override val codec: TypeCodec[Instant] = TypeCodecs.TIMESTAMP
    override val dbName: String = "timestamp"

  given DbType[timeuuid] with SimpleDbType[timeuuid, UUID] with
    override val codec: TypeCodec[UUID] = TypeCodecs.TIMEUUID
    override val dbName: String = "timeuuid"

  given DbType[tinyint] with DbTypeMap[tinyint, Byte, java.lang.Byte](identity, identity) with
    override val codec: TypeCodec[java.lang.Byte] = TypeCodecs.TINYINT
    override val dbName: String = "tinyint"

  given DbType[uuid] with SimpleDbType[uuid, UUID] with
    override val codec: TypeCodec[UUID] = TypeCodecs.UUID
    override val dbName: String = "uuid"

  given DbType[varchar] with SimpleDbType[varchar, String] with
    override val codec: TypeCodec[String] = TypeCodecs.TEXT
    override val dbName: String = "varchar"

  given DbType[varint] with DbTypeMap[varint, BigInt, java.math.BigInteger](_.bigInteger, identity) with
    override val codec: TypeCodec[java.math.BigInteger] = TypeCodecs.VARINT
    override val dbName: String = "varint"

  given mapType[K, V](using key: DbType[K], value: DbType[V]): DbType[map[K, V]] with DbTypeMap[map[K, V], Map[key.ScalaType, value.ScalaType], java.util.Map[key.JavaType, value.JavaType]](_.map { case (k, v) => key.encode(k) -> value.encode(v) }.asJava, _.asScala.map { case (k, v) => key.decode(k) -> value.decode(v) }.toMap) with
    override val codec: TypeCodec[java.util.Map[key.JavaType, value.JavaType]] = TypeCodecs.mapOf(key.codec, value.codec)
    override val dbName: String = s"map<${key.dbName},${value.dbName}>"

  given listType[T](using nested: DbType[T]): DbType[list[T]] with DbTypeMap[list[T], List[nested.ScalaType], java.util.List[nested.JavaType]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toList) with
    override val codec: TypeCodec[java.util.List[nested.JavaType]] = TypeCodecs.listOf(nested.codec)
    override val dbName: String = s"list<${nested.dbName}>"

  given setType[T](using nested: DbType[T]): DbType[set[T]] with DbTypeMap[set[T], Set[nested.ScalaType], java.util.Set[nested.JavaType]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toSet) with
    override val codec: TypeCodec[java.util.Set[nested.JavaType]] = TypeCodecs.setOf(nested.codec)
    override val dbName: String = s"set<${nested.dbName}>"
