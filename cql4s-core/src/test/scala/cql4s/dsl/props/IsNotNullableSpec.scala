package cql4s.dsl
package props

import scala.util.NotGiven

object IsNotNullableSpec:
  summon[IsNotNullable[text]]
  summon[NotGiven[IsNotNullable[nullable[text]]]]
