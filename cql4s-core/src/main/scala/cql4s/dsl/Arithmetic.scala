package cql4s.dsl

trait ArithmeticType[A, B, C]

object ArithmeticType:

  // https://cassandra.apache.org/doc/latest/cassandra/cql/operators.html#number-arithmetic
  //            tinyint   smallint  int     bigint	counter	float	  double	varint	decimal
  //  tinyint   tinyint   smallint  int     bigint  bigint  float   double  varint  decimal
  //  smallint  smallint  smallint  int     bigint  bigint  float   double  varint  decimal
  //  int       int       int       int     bigint  bigint  float   double  varint  decimal
  //  bigint    bigint    bigint    bigint  bigint  bigint  double  double  varint  decimal
  //  counter   bigint    bigint    bigint  bigint  bigint  double  double  varint  decimal
  //  float     float     float     float   double  double  float   double  decimal decimal
  //  double    double    double    double  double  double  double  double  decimal decimal
  //  varint    varint    varint    varint  decimal decimal decimal decimal decimal decimal
  //  decimal   decimal   decimal   decimal decimal decimal decimal decimal decimal decimal

  given ArithmeticType[tinyint,  tinyint,  tinyint ] with { }
  given ArithmeticType[tinyint,  smallint, smallint] with { }
  given ArithmeticType[tinyint,  int,      int     ] with { }
  given ArithmeticType[tinyint,  bigint,   bigint  ] with { }
  given ArithmeticType[tinyint,  counter,  bigint  ] with { }
  given ArithmeticType[tinyint,  float,    float   ] with { }
  given ArithmeticType[tinyint,  double,   double  ] with { }
  given ArithmeticType[tinyint,  varint,   varint  ] with { }
  given ArithmeticType[tinyint,  decimal,  decimal ] with { }

  given ArithmeticType[smallint, tinyint,  smallint] with { }
  given ArithmeticType[smallint, smallint, smallint] with { }
  given ArithmeticType[smallint, int,      int     ] with { }
  given ArithmeticType[smallint, bigint,   bigint  ] with { }
  given ArithmeticType[smallint, counter,  bigint  ] with { }
  given ArithmeticType[smallint, float,    float   ] with { }
  given ArithmeticType[smallint, double,   double  ] with { }
  given ArithmeticType[smallint, varint,   varint  ] with { }
  given ArithmeticType[smallint, decimal,  decimal ] with { }

  given ArithmeticType[int,      tinyint,  int     ] with { }
  given ArithmeticType[int,      smallint, int     ] with { }
  given ArithmeticType[int,      int,      int     ] with { }
  given ArithmeticType[int,      bigint,   bigint  ] with { }
  given ArithmeticType[int,      counter,  bigint  ] with { }
  given ArithmeticType[int,      float,    float   ] with { }
  given ArithmeticType[int,      double,   double  ] with { }
  given ArithmeticType[int,      varint,   varint  ] with { }
  given ArithmeticType[int,      decimal,  decimal ] with { }

  given ArithmeticType[bigint,   tinyint,  bigint  ] with { }
  given ArithmeticType[bigint,   smallint, bigint  ] with { }
  given ArithmeticType[bigint,   int,      bigint  ] with { }
  given ArithmeticType[bigint,   bigint,   bigint  ] with { }
  given ArithmeticType[bigint,   counter,  bigint  ] with { }
  given ArithmeticType[bigint,   float,    double  ] with { }
  given ArithmeticType[bigint,   double,   double  ] with { }
  given ArithmeticType[bigint,   varint,   varint  ] with { }
  given ArithmeticType[bigint,   decimal,  decimal ] with { }

  given ArithmeticType[counter,  tinyint,  bigint  ] with { }
  given ArithmeticType[counter,  smallint, bigint  ] with { }
  given ArithmeticType[counter,  int,      bigint  ] with { }
  given ArithmeticType[counter,  bigint,   bigint  ] with { }
  given ArithmeticType[counter,  counter,  bigint  ] with { }
  given ArithmeticType[counter,  float,    double  ] with { }
  given ArithmeticType[counter,  double,   double  ] with { }
  given ArithmeticType[counter,  varint,   varint  ] with { }
  given ArithmeticType[counter,  decimal,  decimal ] with { }

  given ArithmeticType[float,    tinyint,  float   ] with { }
  given ArithmeticType[float,    smallint, float   ] with { }
  given ArithmeticType[float,    int,      float   ] with { }
  given ArithmeticType[float,    bigint,   double  ] with { }
  given ArithmeticType[float,    counter,  double  ] with { }
  given ArithmeticType[float,    float,    float   ] with { }
  given ArithmeticType[float,    double,   double  ] with { }
  given ArithmeticType[float,    varint,   decimal ] with { }
  given ArithmeticType[float,    decimal,  decimal ] with { }

  given ArithmeticType[double,   tinyint,  double  ] with { }
  given ArithmeticType[double,   smallint, double  ] with { }
  given ArithmeticType[double,   int,      double  ] with { }
  given ArithmeticType[double,   bigint,   double  ] with { }
  given ArithmeticType[double,   counter,  double  ] with { }
  given ArithmeticType[double,   float,    double  ] with { }
  given ArithmeticType[double,   double,   double  ] with { }
  given ArithmeticType[double,   varint,   decimal ] with { }
  given ArithmeticType[double,   decimal,  decimal ] with { }

  given ArithmeticType[varint,   tinyint,  varint  ] with { }
  given ArithmeticType[varint,   smallint, varint  ] with { }
  given ArithmeticType[varint,   int,      varint  ] with { }
  given ArithmeticType[varint,   bigint,   varint  ] with { }
  given ArithmeticType[varint,   counter,  decimal ] with { }
  given ArithmeticType[varint,   float,    decimal ] with { }
  given ArithmeticType[varint,   double,   decimal ] with { }
  given ArithmeticType[varint,   varint,   varint  ] with { }
  given ArithmeticType[varint,   decimal,  decimal ] with { }

  given ArithmeticType[decimal,  tinyint,  decimal ] with { }
  given ArithmeticType[decimal,  smallint, decimal ] with { }
  given ArithmeticType[decimal,  int,      decimal ] with { }
  given ArithmeticType[decimal,  bigint,   decimal ] with { }
  given ArithmeticType[decimal,  counter,  decimal ] with { }
  given ArithmeticType[decimal,  float,    decimal ] with { }
  given ArithmeticType[decimal,  double,   decimal ] with { }
  given ArithmeticType[decimal,  varint,   decimal ] with { }
  given ArithmeticType[decimal,  decimal,  decimal ] with { }

trait Arithemtic

final class Add[T: IsNumerical, +F1 <: Field[T], U: IsNumerical, +F2 <: Field[U], V](val param1: F1, val param2: F2)(
  using
  additionType: ArithmeticType[T, U, V],
  override val dataType: DataType[V]
) extends Arithemtic with DbFunction2[F1, F2, V]:
  override val dbName: String = "+"
  override val infixNotation: Boolean = true

final class Sub[T: IsNumerical, +F1 <: Field[T], U: IsNumerical, +F2 <: Field[U], V](val param1: F1, val param2: F2)(
  using
  additionType: ArithmeticType[T, U, V],
  override val dataType: DataType[V]
) extends Arithemtic with DbFunction2[F1, F2, V]:
  override val dbName: String = "-"
  override val infixNotation: Boolean = true

final class Mul[T: IsNumerical, +F1 <: Field[T], U: IsNumerical, +F2 <: Field[U], V](val param1: F1, val param2: F2)(
  using
  additionType: ArithmeticType[T, U, V],
  override val dataType: DataType[V]
) extends Arithemtic with DbFunction2[F1, F2, V]:
  override val dbName: String = "*"
  override val infixNotation: Boolean = true

final class Div[T: IsNumerical, +F1 <: Field[T], U: IsNumerical, +F2 <: Field[U], V](val param1: F1, val param2: F2)(
  using
  additionType: ArithmeticType[T, U, V],
  override val dataType: DataType[V]
) extends Arithemtic with DbFunction2[F1, F2, V]:
  override val dbName: String = "/"
  override val infixNotation: Boolean = true
