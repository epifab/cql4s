package cql4s.dsl

import cql4s.dsl.props.{AreComparable, CanContain, IsNumerical}

trait Field[T]:
  given dataType: DataType[T]

  def ===[G <: Field[_]](that: G)(using AreComparable[this.type, G]): Equals[this.type, G] = Equals(this, that)
  def ===(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): Equals[this.type, Placeholder[T]] = Equals(this, Placeholder[T])

  def !==[G <: Field[_]](that: G)(using AreComparable[this.type, G]): NotEquals[this.type, G] = NotEquals(this, that)
  def !==(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): NotEquals[this.type, Placeholder[T]] = NotEquals(this, Placeholder[T])

  def >[G <: Field[_]](that: G)(using AreComparable[this.type, G]): GreaterThan[this.type, G] = GreaterThan(this, that)
  def >(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): GreaterThan[this.type, Placeholder[T]] = GreaterThan(this, Placeholder[T])

  def <[G <: Field[_]](that: G)(using AreComparable[this.type, G]): LessThan[this.type, G] = LessThan(this, that)
  def <(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): LessThan[this.type, Placeholder[T]] = LessThan(this, Placeholder[T])

  def >=[G <: Field[_]](that: G)(using AreComparable[this.type, G]): GreaterThanOrEqual[this.type, G] = GreaterThanOrEqual(this, that)
  def >=(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): GreaterThanOrEqual[this.type, Placeholder[T]] = GreaterThanOrEqual(this, Placeholder[T])

  def <=[G <: Field[_]](that: G)(using AreComparable[this.type, G]): LessThanOrEqual[this.type, G] = LessThanOrEqual(this, that)
  def <=(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): LessThanOrEqual[this.type, Placeholder[T]] = LessThanOrEqual(this, Placeholder[T])

  def in[G <: Field[_]](that: G)(using CanContain[G, this.type]): In[this.type, G] = In(this, that)
  def in(p: :?.type)(using CanContain[Placeholder[list[T]], this.type], DataType[list[T]]): In[this.type, Placeholder[list[T]]] = In(this, Placeholder[list[T]])

  def +[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], ArithmeticType[T, U, V], DataType[V]): Add[T, this.type, U, G, V] = Add(this, that)
  def -[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], ArithmeticType[T, U, V], DataType[V]): Sub[T, this.type, U, G, V] = Sub(this, that)
  def *[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], ArithmeticType[T, U, V], DataType[V]): Mul[T, this.type, U, G, V] = Mul(this, that)
  def /[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], ArithmeticType[T, U, V], DataType[V]): Div[T, this.type, U, G, V] = Div(this, that)
  def %[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], ArithmeticType[T, U, V], DataType[V]): Mod[T, this.type, U, G, V] = Mod(this, that)
//  def %[U, G <: Field[U], V](that: G)(using IsNumerical[T], IsNumerical[U], DataType[int]): Mod[T, this.type, U, G] = Mod(this, that)

  def asc: Asc[this.type] = Asc(this)
  def desc: Desc[this.type] = Desc(this)

  def castTo[U](using DataType[U]): Cast[this.type, U] = Cast(this)
