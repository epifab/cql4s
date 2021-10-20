package cql4s.dsl

sealed trait Order[+F <: Field[_]]:
  def field: F

case class Asc[+F <: Field[_]](field: F) extends Order[F]
case class Desc[+F <: Field[_]](field: F) extends Order[F]

/**
 * Ensures X is a valid sort by clause
 * @tparam X
 */
trait OrderByClasue[-X]

object OrderByClasue:
  given [F <: Field[_]]: OrderByClasue[F] with { }
  given [S <: Order[_]]: OrderByClasue[S] with { }
  given OrderByClasue[EmptyTuple] with { }
  given [H, T <: Tuple](using OrderByClasue[H], OrderByClasue[T]): OrderByClasue[H *: T] with { }
