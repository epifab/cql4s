package casa
package compiler

trait ValueFragment[-T, I <: Tuple] extends FragmentCompiler[T, I]

object ValueFragment:
  given [K <: String with Singleton, V, X <: Tuple] (using fragment: FieldFragment[V, X]): ValueFragment[K ~~> V, X] with
    def build(x: K ~~> V): CompiledFragment[X] = fragment.build(x.value)
