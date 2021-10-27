package cql4s.dsl

trait DbFunction[+Params <: Tuple, Type] extends Field[Type]:
  def params: Params
  def dbName: String
  override def toString: String = s"$dbName$params"

trait DbFunction1[+F, Type] extends DbFunction[F *: EmptyTuple, Type]:
  def param: F
  def params: F *: EmptyTuple = param *: EmptyTuple

trait DbFunction2[+F, +G, Type] extends DbFunction[(F, G), Type]:
  def param1: F
  def param2: G
  def params: (F, G) = (param1, param2)
  def infixNotation: Boolean = false

trait DbFunction3[+F, +G, +H, Type] extends DbFunction[(F, G, H), Type]:
  def param1: F
  def param2: G
  def param3: H
  def params: (F, G, H) = (param1, param2, param3)
