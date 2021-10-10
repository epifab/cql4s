package cql4s

import cql4s._

object AssignementsSpec:
  summon[Assignments[Column["hello", varchar], "hello" ~~> Placeholder["hello", varchar]]]
  summon[Assignments[EmptyTuple, EmptyTuple]]
  summon[Assignments[Column["hello", varchar] *: EmptyTuple, "hello" ~~> Placeholder["hello", varchar] *: EmptyTuple]]
  summon[Assignments[(Column["hello", varchar], Column["world", text]), ("hello" ~~> Placeholder["hello", varchar], "world" ~~> Placeholder["world", text])]]
