package cql4s.dsl
package props

object NullableConversionSpec:
  summon[NullableConversion[int, nullable[int]]]
  summon[NullableConversion[nullable[int], nullable[int]]]
