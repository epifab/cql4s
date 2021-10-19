package cql4s.compiler

import cql4s.dsl.*

trait KeyFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object KeyFragment:
  given [K <: String with Singleton, V](using id: DbIdentifier[K]): KeyFragment[K ~~> V, EmptyTuple] with
    def build(x: K ~~> V): CompiledFragment[EmptyTuple] = CompiledFragment(id.escaped)
