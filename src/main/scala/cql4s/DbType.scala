package cql4s

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
  type JavaType >: Null
  type ScalaType
  def codec: TypeCodec[JavaType]
  def encode: ScalaType => JavaType
  def decode: JavaType => ScalaType
  def dbName: String


trait DbTypeImpl[T, J >: Null, S](override val encode: S => J, override val decode: J => S) extends DbType[T]:
  type JavaType = J
  type ScalaType = S


trait SimpleDbType[T, U >: Null] extends DbType[T]:
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

type nullable[T]
type json[T]


object DbType:
  type Aux[T, JT, ST] = DbType[T] {
    type JavaType = JT
    type ScalaType = ST 
  }

  type Out[T, ST] = DbType[T] {
    type ScalaType = ST
  }

  given DbType[ascii] with SimpleDbType[ascii, String] with
    override val codec: TypeCodec[String] = TypeCodecs.ASCII
    override val dbName: String = "ascii"

  given DbType[bigint] with DbTypeImpl[bigint, java.lang.Long, Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.BIGINT
    override val dbName: String = "bigint"

  given DbType[blob] with SimpleDbType[blob, ByteBuffer] with
    override val codec: TypeCodec[ByteBuffer] = TypeCodecs.BLOB
    override val dbName: String = "blob"

  given DbType[boolean] with DbTypeImpl[boolean, java.lang.Boolean, Boolean](identity, identity) with
    override val codec: TypeCodec[java.lang.Boolean] = TypeCodecs.BOOLEAN
    override val dbName: String = "boolean"

  given DbType[counter] with DbTypeImpl[counter, java.lang.Long, Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.COUNTER
    override val dbName: String = "counter"

  given DbType[date] with SimpleDbType[date, LocalDate] with
    override val codec: TypeCodec[LocalDate] = TypeCodecs.DATE
    override val dbName: String = "date"

  given DbType[decimal] with DbTypeImpl[decimal, java.math.BigDecimal, BigDecimal](_.bigDecimal, identity) with
    override val codec: TypeCodec[java.math.BigDecimal] = TypeCodecs.DECIMAL
    override val dbName: String = "decimal"

  given DbType[double] with DbTypeImpl[double, java.lang.Double, Double](identity, identity) with
    override val codec: TypeCodec[java.lang.Double] = TypeCodecs.DOUBLE
    override val dbName: String = "double"

  // This is implemented with CqlDuration, as it is effectively a mix of Period and Duration
  // given DbType[duration]

  given DbType[float] with DbTypeImpl[float, java.lang.Float, Float](identity, identity) with
    override val codec: TypeCodec[java.lang.Float] = TypeCodecs.FLOAT
    override val dbName: String = "float"

  given DbType[inet] with SimpleDbType[inet, InetAddress] with
    override val codec: TypeCodec[InetAddress] = TypeCodecs.INET
    override val dbName: String = "inet"

  given DbType[int] with DbTypeImpl[int, java.lang.Integer, Int](identity, identity) with
    override val codec: TypeCodec[java.lang.Integer] = TypeCodecs.INT
    override val dbName: String = "int"

  given DbType[smallint] with DbTypeImpl[smallint, java.lang.Short, Short](identity, identity) with
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

  given DbType[tinyint] with DbTypeImpl[tinyint, java.lang.Byte, Byte](identity, identity) with
    override val codec: TypeCodec[java.lang.Byte] = TypeCodecs.TINYINT
    override val dbName: String = "tinyint"

  given DbType[uuid] with SimpleDbType[uuid, UUID] with
    override val codec: TypeCodec[UUID] = TypeCodecs.UUID
    override val dbName: String = "uuid"

  given DbType[varchar] with SimpleDbType[varchar, String] with
    override val codec: TypeCodec[String] = TypeCodecs.TEXT
    override val dbName: String = "varchar"

  given DbType[varint] with DbTypeImpl[varint, java.math.BigInteger, BigInt](_.bigInteger, identity) with
    override val codec: TypeCodec[java.math.BigInteger] = TypeCodecs.VARINT
    override val dbName: String = "varint"

  given mapType[K, KJT, KST, V, VJT, VST](using key: DbType.Aux[K, KJT, KST], value: DbType.Aux[V, VJT, VST]): DbType[map[K, V]] with DbTypeImpl[map[K, V], java.util.Map[KJT, VJT], Map[KST, VST]](_.map { case (k, v) => key.encode(k) -> value.encode(v) }.asJava, _.asScala.map { case (k, v) => key.decode(k) -> value.decode(v) }.toMap) with
    override val codec: TypeCodec[java.util.Map[KJT, VJT]] = TypeCodecs.mapOf(key.codec, value.codec)
    override val dbName: String = s"map<${key.dbName},${value.dbName}>"

  given listType[T, JT, ST](using nested: DbType.Aux[T, JT, ST]): DbType[list[T]] with DbTypeImpl[list[T], java.util.List[JT], List[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toList) with
    override val codec: TypeCodec[java.util.List[JT]] = TypeCodecs.listOf(nested.codec)
    override val dbName: String = s"list<${nested.dbName}>"

  given setType[T, JT, ST](using nested: DbType.Aux[T, JT, ST]): DbType[set[T]] with DbTypeImpl[set[T], java.util.Set[JT], Set[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toSet) with
    override val codec: TypeCodec[java.util.Set[JT]] = TypeCodecs.setOf(nested.codec)
    override val dbName: String = s"set<${nested.dbName}>"

  given nullableType[T, JT >: Null, ST](using nested: DbType.Aux[T, JT, ST]): DbType[nullable[T]] with DbTypeImpl[nullable[T], JT, Option[ST]](s => s.map(nested.encode).orNull, j => Option(j).map(nested.decode)) with
    override val codec: TypeCodec[JT] = nested.codec
    override val dbName: String = nested.dbName

  given jsonType[T](using io.circe.Encoder[T], io.circe.Decoder[T]): DbType[json[T]] with
    import io.circe.syntax._
    import io.circe.parser.{decode => decodeJson}

    override type JavaType = String
    override type ScalaType = T
    override def codec: TypeCodec[jsonType.this.JavaType] = TypeCodecs.TEXT
    override def dbName: String = "text"

    override def encode: T => String = _.asJson.noSpaces
    override def decode: String => ScalaType = s => decodeJson[T](s).getOrElse(throw RuntimeException(s"Cannot decode json '$s'"))
