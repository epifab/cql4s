package cql4s.dsl

object AssignementsSpec:
  summon[Assignments[Column["hello", varchar], "hello" ~~> Placeholder[varchar]]]
  summon[Assignments[EmptyTuple, EmptyTuple]]
  summon[Assignments[Column["hello", varchar] *: EmptyTuple, "hello" ~~> Placeholder[varchar] *: EmptyTuple]]
  summon[Assignments[(Column["hello", varchar], Column["world", text]), ("hello" ~~> Placeholder[varchar], "world" ~~> Placeholder[text])]]
