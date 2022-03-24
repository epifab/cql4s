package cql4s.dsl
package props

import scala.util.NotGiven

object IsTypeSpec:
  summon[IsType[varchar, varchar]]
  summon[IsType[varchar, nullable[varchar]]]
  summon[IsType[varchar, Placeholder[varchar]]]
  summon[IsType[varchar, Placeholder[nullable[varchar]]]]

  summon[NotGiven[IsType[varchar, int]]]
  summon[NotGiven[IsType[varchar, nullable[int]]]]
  summon[NotGiven[IsType[varchar, Placeholder[int]]]]
  summon[NotGiven[IsType[varchar, Placeholder[nullable[int]]]]]
