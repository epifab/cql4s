package cql4s

import com.datastax.oss.driver.api.core.ProtocolVersion
import com.datastax.oss.driver.api.core.`type`.{UserDefinedType, DataType as DriverDataType, DataTypes as DriverDataTypes}
import com.datastax.oss.driver.api.core.`type`.codec.{TypeCodec as DriverTypeCodec, TypeCodecs as DriverTypeCodecs}
import com.datastax.oss.driver.api.core.`type`.reflect.GenericType
import com.datastax.oss.driver.api.core.data.{CqlDuration, UdtValue}
import com.datastax.oss.driver.internal.core.`type`.{PrimitiveType, UserDefinedTypeBuilder}
import cql4s.utils.{ColumnsFactory, NonEmptyListOfColumns}

import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.*
import java.util.UUID
import scala.collection.JavaConverters.*
import scala.concurrent.duration.Duration


trait DataType[-T]:
  type JavaType >: Null
  type ScalaType
  def driverDataType: DriverDataType
  def driverCodec: DriverTypeCodec[JavaType]
  def encode: ScalaType => JavaType
  def decode: JavaType => ScalaType
  def dbName: String

  def map[U, NewScalaType](f: NewScalaType => ScalaType, g: ScalaType => NewScalaType): DataTypeCodec[U, JavaType, NewScalaType] =
    new DataTypeMap[U, JavaType, NewScalaType](driverDataType, driverCodec, dbName, encode.compose(f), g.compose(decode))


sealed private[cql4s] trait DataTypeImpl[T, J >: Null, S](override val encode: S => J, override val decode: J => S) extends DataType[T]:
  type JavaType = J
  type ScalaType = S

final private[cql4s] class DataTypeMap[T, J >: Null, S](
  override val driverDataType: DriverDataType,
  override val driverCodec: DriverTypeCodec[J],
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

trait udt[Keyspace: DbIdentifier, Name: DbIdentifier, Components: ColumnsFactory]

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
    override val driverDataType: DriverDataType = DriverDataTypes.ASCII
    override val driverCodec: DriverTypeCodec[String] = DriverTypeCodecs.ASCII
    override val dbName: String = "ascii"

  given bigintCodec: DataType[bigint] with DataTypeImpl[bigint, java.lang.Long, Long](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.BIGINT
    override val driverCodec: DriverTypeCodec[java.lang.Long] = DriverTypeCodecs.BIGINT
    override val dbName: String = "bigint"

  given blobCodec: DataType[blob] with SimpleDataType[blob, ByteBuffer] with
    override val driverDataType: DriverDataType = DriverDataTypes.BLOB
    override val driverCodec: DriverTypeCodec[ByteBuffer] = DriverTypeCodecs.BLOB
    override val dbName: String = "blob"

  given booleanCodec: DataType[boolean] with DataTypeImpl[boolean, java.lang.Boolean, Boolean](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.BOOLEAN
    override val driverCodec: DriverTypeCodec[java.lang.Boolean] = DriverTypeCodecs.BOOLEAN
    override val dbName: String = "boolean"

  given counterCodec: DataType[counter] with DataTypeImpl[counter, java.lang.Long, Long](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.COUNTER
    override val driverCodec: DriverTypeCodec[java.lang.Long] = DriverTypeCodecs.COUNTER
    override val dbName: String = "counter"

  given dateCodec: DataType[date] with SimpleDataType[date, LocalDate] with
    override val driverDataType: DriverDataType = DriverDataTypes.DATE
    override val driverCodec: DriverTypeCodec[LocalDate] = DriverTypeCodecs.DATE
    override val dbName: String = "date"

  given decimalCodec: DataType[decimal] with DataTypeImpl[decimal, java.math.BigDecimal, BigDecimal](_.bigDecimal, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.DECIMAL
    override val driverCodec: DriverTypeCodec[java.math.BigDecimal] = DriverTypeCodecs.DECIMAL
    override val dbName: String = "decimal"

  given doubleCodec: DataType[double] with DataTypeImpl[double, java.lang.Double, Double](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.DOUBLE
    override val driverCodec: DriverTypeCodec[java.lang.Double] = DriverTypeCodecs.DOUBLE
    override val dbName: String = "double"

  // This is implemented with CqlDuration, as it is effectively a mix of Period and Duration
  // given durationCodec:ven DbType[duration]

  given floatCodec: DataType[float] with DataTypeImpl[float, java.lang.Float, Float](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.FLOAT
    override val driverCodec: DriverTypeCodec[java.lang.Float] = DriverTypeCodecs.FLOAT
    override val dbName: String = "float"

  given inetCodec: DataType[inet] with SimpleDataType[inet, InetAddress] with
    override val driverDataType: DriverDataType = DriverDataTypes.INET
    override val driverCodec: DriverTypeCodec[InetAddress] = DriverTypeCodecs.INET
    override val dbName: String = "inet"

  given intCodec: DataType[int] with DataTypeImpl[int, java.lang.Integer, Int](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.INT
    override val driverCodec: DriverTypeCodec[java.lang.Integer] = DriverTypeCodecs.INT
    override val dbName: String = "int"

  given smallintCodec: DataType[smallint] with DataTypeImpl[smallint, java.lang.Short, Short](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.SMALLINT
    override val driverCodec: DriverTypeCodec[java.lang.Short] = DriverTypeCodecs.SMALLINT
    override val dbName: String = "smallint"

  given textCodec: DataType[text] with SimpleDataType[text, String] with
    override val driverDataType: DriverDataType = DriverDataTypes.TEXT
    override val driverCodec: DriverTypeCodec[String] = DriverTypeCodecs.TEXT
    override val dbName: String = "text"

  given timeCodec: DataType[time] with SimpleDataType[time, LocalTime] with
    override val driverDataType: DriverDataType = DriverDataTypes.TIME
    override val driverCodec: DriverTypeCodec[LocalTime] = DriverTypeCodecs.TIME
    override val dbName: String = "time"

  given timestampCodec: DataType[timestamp] with SimpleDataType[timestamp, Instant] with
    override val driverDataType: DriverDataType = DriverDataTypes.TIMESTAMP
    override val driverCodec: DriverTypeCodec[Instant] = DriverTypeCodecs.TIMESTAMP
    override val dbName: String = "timestamp"

  given timeuuidCodec: DataType[timeuuid] with SimpleDataType[timeuuid, UUID] with
    override val driverDataType: DriverDataType = DriverDataTypes.TIMEUUID
    override val driverCodec: DriverTypeCodec[UUID] = DriverTypeCodecs.TIMEUUID
    override val dbName: String = "timeuuid"

  given tinyintCodec: DataType[tinyint] with DataTypeImpl[tinyint, java.lang.Byte, Byte](identity, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.TINYINT
    override val driverCodec: DriverTypeCodec[java.lang.Byte] = DriverTypeCodecs.TINYINT
    override val dbName: String = "tinyint"

  given uuidCodec: DataType[uuid] with SimpleDataType[uuid, UUID] with
    override val driverDataType: DriverDataType = DriverDataTypes.UUID
    override val driverCodec: DriverTypeCodec[UUID] = DriverTypeCodecs.UUID
    override val dbName: String = "uuid"

  given varcharCodec: DataType[varchar] with SimpleDataType[varchar, String] with
    override val driverDataType: DriverDataType = DriverDataTypes.TEXT
    override val driverCodec: DriverTypeCodec[String] = DriverTypeCodecs.TEXT
    override val dbName: String = "varchar"

  given varintCodec: DataType[varint] with DataTypeImpl[varint, java.math.BigInteger, BigInt](_.bigInteger, identity) with
    override val driverDataType: DriverDataType = DriverDataTypes.VARINT
    override val driverCodec: DriverTypeCodec[java.math.BigInteger] = DriverTypeCodecs.VARINT
    override val dbName: String = "varint"

  given mapCodec[K, KJT, KST, V, VJT, VST](using key: DataTypeCodec[K, KJT, KST], value: DataTypeCodec[V, VJT, VST]): DataType[map[K, V]] with DataTypeImpl[map[K, V], java.util.Map[KJT, VJT], Map[KST, VST]](_.map { case (k, v) => key.encode(k) -> value.encode(v) }.asJava, _.asScala.map { case (k, v) => key.decode(k) -> value.decode(v) }.toMap) with
    override val driverDataType: DriverDataType = DriverDataTypes.mapOf(key.driverDataType, value.driverDataType)
    override val driverCodec: DriverTypeCodec[java.util.Map[KJT, VJT]] = DriverTypeCodecs.mapOf(key.driverCodec, value.driverCodec)
    override val dbName: String = s"map<${key.dbName},${value.dbName}>"

  given listCodec[T, JT, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[list[T]] with DataTypeImpl[list[T], java.util.List[JT], List[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toList) with
    override val driverDataType: DriverDataType = DriverDataTypes.listOf(nested.driverDataType)
    override val driverCodec: DriverTypeCodec[java.util.List[JT]] = DriverTypeCodecs.listOf(nested.driverCodec)
    override val dbName: String = s"list<${nested.dbName}>"

  given setCodec[T, JT, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[set[T]] with DataTypeImpl[set[T], java.util.Set[JT], Set[ST]](_.map(nested.encode).asJava, _.asScala.map(nested.decode).toSet) with
    override val driverDataType: DriverDataType = DriverDataTypes.setOf(nested.driverDataType)
    override val driverCodec: DriverTypeCodec[java.util.Set[JT]] = DriverTypeCodecs.setOf(nested.driverCodec)
    override val dbName: String = s"set<${nested.dbName}>"

  given nullableCodec[T, JT >: Null, ST](using nested: DataTypeCodec[T, JT, ST]): DataType[nullable[T]] with DataTypeImpl[nullable[T], JT, Option[ST]](s => s.map(nested.encode).orNull, j => Option(j).map(nested.decode)) with
    override val driverDataType: DriverDataType = nested.driverDataType
    override val driverCodec: DriverTypeCodec[JT] = nested.driverCodec
    override val dbName: String = nested.dbName

  given jsonCodec[T](using io.circe.Encoder[T], io.circe.Decoder[T]): DataTypeCodec[json[T], String, T] = {
    import io.circe.parser.decode as decodeJson
    import io.circe.syntax.*

    textCodec.map[json[T], T](
      _.asJson.noSpaces,
      s => decodeJson[T](s).getOrElse(throw RuntimeException(s"Cannot decode json '$s'"))
    )
  }

  given udtCodec[Keyspace, Name, Components, Output](
    using
    keyspace: DbIdentifier[Keyspace],
    name: DbIdentifier[Name],
    columnsFactory: ColumnsFactory[Components],
    decoderAdapter: DecoderAdapter[Components, Output],
    encoderAdapter: EncoderAdapter[Components, Output]
  ): DataType[udt[Keyspace, Name, Components]] with
    override type JavaType = UdtValue
    override type ScalaType = Output

    override val driverDataType: UserDefinedType =
      columnsFactory
        .toList
        .foldLeft(new UserDefinedTypeBuilder(keyspace.escaped, name.escaped)) { case (builder, column) =>
          builder.withField(column.name.escaped, column.dataType.driverDataType)
        }
        .build()
    override val driverCodec: DriverTypeCodec[UdtValue] = DriverTypeCodecs.udtOf(driverDataType)

    override def decode: UdtValue => Output = decoderAdapter.decode
    override def encode: Output => UdtValue = (output => encoderAdapter
      .encode(output)
      .zip(columnsFactory.toList)
      .zipWithIndex
      .foldLeft(driverDataType.newValue()) { case (udtValue, ((value, column), index)) =>
          udtValue.set(index, value, (column.dataType.driverCodec.asInstanceOf[DriverTypeCodec[value.type]]))
      })

    override val dbName: String = name.escaped
