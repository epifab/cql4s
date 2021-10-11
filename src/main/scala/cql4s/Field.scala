package cql4s

trait Field[T]:
  given dbType: DbType[T]

  def ===[G <: Field[_]](right: G)(using AreComparable[this.type, G]): Equals[this.type, G] = Equals(this, right)
  def ===(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): Equals[this.type, Placeholder[T]] = Equals(this, Placeholder[T])

  def !==[G <: Field[_]](right: G)(using AreComparable[this.type, G]): NotEquals[this.type, G] = NotEquals(this, right)
  def !==(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): NotEquals[this.type, Placeholder[T]] = NotEquals(this, Placeholder[T])

  def >[G <: Field[_]](right: G)(using AreComparable[this.type, G]): GreaterThan[this.type, G] = GreaterThan(this, right)
  def >(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): GreaterThan[this.type, Placeholder[T]] = GreaterThan(this, Placeholder[T])

  def <[G <: Field[_]](right: G)(using AreComparable[this.type, G]): LessThan[this.type, G] = LessThan(this, right)
  def <(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): LessThan[this.type, Placeholder[T]] = LessThan(this, Placeholder[T])

  def >=[G <: Field[_]](right: G)(using AreComparable[this.type, G]): GreaterThanOrEqual[this.type, G] = GreaterThanOrEqual(this, right)
  def >=(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): GreaterThanOrEqual[this.type, Placeholder[T]] = GreaterThanOrEqual(this, Placeholder[T])

  def <=[G <: Field[_]](right: G)(using AreComparable[this.type, G]): LessThanOrEqual[this.type, G] = LessThanOrEqual(this, right)
  def <=(p: :?.type)(using AreComparable[this.type, Placeholder[T]]): LessThanOrEqual[this.type, Placeholder[T]] = LessThanOrEqual(this, Placeholder[T])
