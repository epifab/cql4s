package casa

// Column assignments for insert and update commands
case class Assignment[+C <: Column[_, _], +Value <: Field[_]](val column: C, val value: Value)(using AreComparable[C, Value])

type :==[F <: Column[_, _], G <: Field[_]] = Assignments[F, G]

trait Assignments[-From, To]:
  def apply(from: From): To

object Assignments:
  given column[Name <: String with Singleton, T: DbType](using columnName: ValueOf[Name]): Assignments[Column[Name, T], (Name ~~> Placeholder[Name, T])] with
    def apply(from: Column[Name, T]): Name ~~> Placeholder[Name, T] = columnName.value ~~> Placeholder[Name, T]

  given expr[Name <: String with Singleton, T: DbType, Expr <: Field[_]](using columnName: ValueOf[Name]): Assignments[Assignment[Column[Name, T], Expr], (Name ~~> Expr)] with
    def apply(from: Assignment[Column[Name, T], Expr]): Name ~~> Expr = columnName.value ~~> from.value

  given empty: Assignments[EmptyTuple, EmptyTuple] with
    def apply(from: EmptyTuple): EmptyTuple = EmptyTuple

  given head[Head, HeadPlaceholder, Tail <: Tuple, TailPlaceholders <: Tuple](
                                                                               using
                                                                               head: Assignments[Head, HeadPlaceholder],
                                                                               tail: Assignments[Tail, TailPlaceholders]
                                                                             ): Assignments[Head *: Tail, HeadPlaceholder *: TailPlaceholders] with
    def apply(from: Head *: Tail): HeadPlaceholder *: TailPlaceholders = head(from.head) *: tail(from.tail)
