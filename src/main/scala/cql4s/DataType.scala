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


trait DataType[T]:
  type JavaType >: Null
  type ScalaType
  def codec: TypeCodec[JavaType]
  def encode: ScalaType => JavaType
  def decode: JavaType => ScalaType
  def dbName: String

  def map[U, NewScalaType](f: NewScalaType => ScalaType, g: ScalaType => NewScalaType): DataTypeCodec[U, JavaType, NewScalaType] =
    new DataTypeMap[U, JavaType, NewScalaType](codec, dbName, encode.compose(f), g.compose(decode))


sealed private[cql4s] trait DataTypeImpl[T, J >: Null, S](override val encode: S => J, override val decode: J => S) extends DataType[T]:
  type JavaType = J
  type ScalaType = S

final private[cql4s] class DataTypeMap[T, J >: Null, S](
  override val codec: TypeCodec[J],
  override val dbName: String,
  encode: S => J,
  decode: J => S
) extends DataTypeImpl[T, J, S](encode, decode)


sealed private[cql4s] trait SimpleDataType[T, U >: Null] extends DataType[T]:
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


type DataTypeCodec[T, J, S] = DataType[T] {
  type JavaType = J
  type ScalaType = S
}


object DataType:
  type Aux[T, ST] = DataType[T] {
    type ScalaType = ST
  }

  def apply[T](using dataType: DataType[T]): DataType[T] = dataType

  given asciiCodec: DataType[ascii] with SimpleDataType[ascii, String] with
    override val codec: TypeCodec[String] = TypeCodecs.ASCII
    override val dbName: String = "ascii"

  given bigintCodec: DataType[bigint] with DataTypeImpl[bigint, java.lang.Long, Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.BIGINT
    override val dbName: String = "bigint"

  given blobCodec: DataType[blob] with SimpleDataType[blob, ByteBuffer] with
    override val codec: TypeCodec[ByteBuffer] = TypeCodecs.BLOB
    override val dbName: String = "blob"

  given booleanCodec: DataType[boolean] with DataTypeImpl[boolean, java.lang.Boolean, Boolean](identity, identity) with
    override val codec: TypeCodec[java.lang.Boolean] = TypeCodecs.BOOLEAN
    override val dbName: String = "boolean"

  given counterCodec: DataType[counter] with DataTypeImpl[counter, java.lang.Long, Long](identity, identity) with
    override val codec: TypeCodec[java.lang.Long] = TypeCodecs.COUNTER
    override val dbName: String = "counter"

  given dateCodec: DataType[date] with SimpleDataType[date, LocalDate] with
    override val codec: TypeCodec[LocalDate] = TypeCodecs.DATE
    override val dbName: String = "date"

  given decimalCodec: DataType[decimal] with DataTypeImpl[decimal, java.math.BigDecimal, BigDecimal](_.bigDecimal, identity) with
    override val codec: TypeCodec[java.math.BigDecimal] = TypeCodecs.DECIMAL
    override val dbName: String = "decimal"

  given doubleCodec: DataType[double] with DataTypeImpl[double, java.lang.Double, Double](identity, identity) with
    override val codec: TypeCodec[java.lang.Double] = TypeCodecs.DOUBLE
    override val dbName: String = "double"

  // This is implemented with CqlDuration, as it is effectively a mix of Period and Duration
  // given durationCodec:ven DbType[duration]

  given floatCodec: DataType[float] with DataTypeImpl[float, java.lang.Float, Float](identity, identity) with
    override val codec: TypeCodec[java.lang.Float] = TypeCodecs.FLOAT
    override val dbName: String = "float"

  given inetCodec: DataType[inet] with SimpleDataType[inet, InetAddress] with
    override val codec: TypeCodec[InetAddress] = TypeCodecs.INET
    override val dbName: String = "inet"

  given intCodec: DataType[int] with DataTypeImpl[int, java.lang.Integer, Int](identity, identity) with
    override val codec: TypeCodec[java.lang.Integer] = TypeCodecs.INT
    override val dbName: String = "int"

  given smallintCodec: DataType[smallint] with DataTypeImpl[smallint, java.lang.Short, Short](identity, identity) with
    override val codec: TypeCodec[java.lang.Short] = TypeCodecs.SMALLINT
    override val dbName: String = "smallint"

  given textCodec: DataType[text] with SimpleDataType[text, String] with
    override val codec: TypeCodec[String] = TypeCodecs.TEXT
    override val dbName: String = "text"

  given timeCodec: DataType[time] with SimpleDataType[time, LocalTime] with
    override val codec: TypeCodec[LocalTime] = TypeCodecs.TIME
    override val dbName: String = "time"

  given timestampCodec: DataType[timestamp] with SimpleDataType[timestamp, Instant] with
    override val codec: TypeCodec[Instant] = TypeCodecs.TIMESTAMP
    override val dbName: String = "timestamp"

  given timeuuidCodec: DataType[timeuuid] with SimpleDataType[timeuuid, UUID] with
    override val codec: TypeCodec[UUID] = TypeCodecs.TIMEUUID
    override val dbName: String = "timeuuid"

  given tinyintCodec: DataType[tinyint] with DataTypeImpl[tinyint, java.lang.Byte, Byte](identity, identity) with
    override val codec: TypeCodec[java.lang.Byte] = TypeCodecs.TINYINT
    override val dbName: String = "tinyint"

  given uuidCodec: DataType[uuid] with SimpleDataType[uuid, UUID] with
    override val codec: TypeCodec[UUID] = TypeCodecs.UUID
    override val dbName: String = "uuid"

  given varcharCodec: DataType[varchar] with SimpleDataType[varchar, String] with
    override val codec: TypeCodec[String] = TypeCodecs.TEXT
    override val dbName: String = "varchar"

  given varintCodec: DataType[varint] with DataTypeImpl[varint, java.math.BigInteger, BigInt](_.bigInteger, identity) with
    override val codec: TypeCodec[java.math.BigInteger] = TypeCodecs.VARINT
    override val dbName: String = "varint"

  given mapCodec[K, KJT, KST, V, VJT, VST](using key: DataTypeCodec[K, KJT, KST], value: DataTypeCodec[V, VJT, VST]): DataType[map[K, V]] with DataTypeImpl[map[K, V], java.util.Map[KJT, VJT], Map[KST, VST]](_.map { case (k, v) => key.encode(k) -> value.encode(v) }.asJava, _.asScala.map { case (k, v) => key.decode(k) -> value.decode(v) }.toMap) with
    override val codec: TypeCodec[java.util.Map[KJT, VJT]] = TypeCodecs.mapOf(key.codec, value.codec)
    override val dbName: String = s"map<${key.dbName},${value.dbName}>"

  given listCodec[T, JT, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[list[T]] with DataTypeImpl[list[T], java.util.List[JT], List[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toList) with
    override val codec: TypeCodec[java.util.List[JT]] = TypeCodecs.listOf(nested.codec)
    override val dbName: String = s"list<${nested.dbName}>"

  given setCodec[T, JT, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[set[T]] with DataTypeImpl[set[T], java.util.Set[JT], Set[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toSet) with
    override val codec: TypeCodec[java.util.Set[JT]] = TypeCodecs.setOf(nested.codec)
    override val dbName: String = s"set<${nested.dbName}>"

  given nullableCodec[T, JT >: Null, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[nullable[T]] with DataTypeImpl[nullable[T], JT, Option[ST]](s => s.map(nested.encode).orNull, j => Option(j).map(nested.decode)) with
    override val codec: TypeCodec[JT] = nested.codec
    override val dbName: String = nested.dbName

  given jsonCodec[T](using io.circe.Encoder[T], io.circe.Decoder[T]): DataTypeCodec[json[T], String, T] = {
    import io.circe.syntax._
    import io.circe.parser.{decode => decodeJson}

    textCodec.map[json[T], T](
      _.asJson.noSpaces,
      s => decodeJson[T](s).getOrElse(throw RuntimeException(s"Cannot decode json '$s'"))
    )
  }
