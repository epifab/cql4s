package cql4s.utils

import cql4s.dsl.*

/**
 * Given Type, it ensures that X is either None, Some[Placeholder[*, Type]] or Some[Const[Type]]
 * @tparam Type
 * @tparam X
 */
trait OptionalInput[Type, -X]

object OptionalInput:
  given none[Type]: OptionalInput[Type, None.type] with { }
  given field[Type]: OptionalInput[Type, Field[Type]] with { }
