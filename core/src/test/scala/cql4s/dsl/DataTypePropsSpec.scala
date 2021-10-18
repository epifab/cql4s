package cql4s.dsl

import scala.util.NotGiven

trait DataTypePropsSpec

object AreComparableSpec extends DataTypePropsSpec:
  summon[AreComparable[varchar, varchar]]
  summon[AreComparable[text, varchar]]
  summon[NotGiven[AreComparable[int, varchar]]]
  summon[AreComparable[inet, inet]]
  summon[AreComparable[nullable[inet], inet]]
  summon[AreComparable[inet, nullable[inet]]]
  summon[AreComparable[nullable[inet], nullable[inet]]]
  summon[AreComparable[nullable[text], varchar]]
  summon[AreComparable[text, nullable[varchar]]]
  summon[AreComparable[nullable[text], nullable[varchar]]]

  summon[AreComparable[Placeholder[varchar], Column["hello", varchar]]]
  summon[AreComparable[Placeholder[text], Column["hello", varchar]]]
  summon[NotGiven[AreComparable[Field[int], Field[varchar]]]]
  summon[AreComparable[Field[inet], Field[inet]]]
  summon[AreComparable[Column["hello", nullable[inet]], Placeholder[inet]]]
  summon[AreComparable[Column["hello", inet], Placeholder[nullable[inet]]]]
  summon[AreComparable[Column["hello", nullable[inet]], Placeholder[nullable[inet]]]]
  summon[AreComparable[Column["hello", nullable[text]], Placeholder[varchar]]]
  summon[AreComparable[Column["hello", text], Placeholder[nullable[varchar]]]               ]
  summon[AreComparable[Column["hello", nullable[text]], Placeholder[nullable[varchar]]]]
