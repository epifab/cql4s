package cql4s.dsl
package props

import scala.util.NotGiven

object IsNullableSpec:
  summon[IsNullable[nullable[text]]]
  summon[NotGiven[IsNullable[text]]]
