package cql4s.dsl
package props

import scala.util.NotGiven

object CanContainSpec:
  summon[CanContain[list[time], time]]
  summon[CanContain[list[varchar], text]]
  summon[NotGiven[CanContain[time, time]]]
  summon[CanContain[list[time], nullable[time]]]
  summon[CanContain[nullable[list[time]], time]]
  summon[CanContain[nullable[list[time]], nullable[time]]]
  summon[CanContain[Placeholder[nullable[list[time]]], Column["hello", nullable[time]]]]
