package casa

import cats.data.Chain.Singleton

object SelectSpec:

  given valueOfEmptyTuple: ValueOf[EmptyTuple] = ValueOf(EmptyTuple)
  given valueOfNonEmptyTuple[H, T <: Tuple](using head: ValueOf[H], tail: ValueOf[T]): ValueOf[H *: T] = ValueOf(head.value *: tail.value)

  summon[ValueOf[("a", "b")]]

  object gigs extends Table["gigs", ("artists" :=: list[varchar], "start" :=: timestamp, "venue" :=: text)]

  val query =
    Select
      .from(gigs)
      .take("artists", "venue")
