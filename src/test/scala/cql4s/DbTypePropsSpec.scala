package cql4s

import scala.util.NotGiven

trait DbTypePropsSpec

object AreComparableSpec extends DbTypePropsSpec:
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

  summon[AreComparable[Placeholder["world", varchar], Column["hello", varchar]]]
  summon[AreComparable[Placeholder["world", text], Column["hello", varchar]]]
  summon[NotGiven[AreComparable[Field[int], Field[varchar]]]]
  summon[AreComparable[Field[inet], Field[inet]]]
  summon[AreComparable[Column["hello", nullable[inet]], Placeholder["world", inet]]]
  summon[AreComparable[Column["hello", inet], Placeholder["world", nullable[inet]]]]
  summon[AreComparable[Column["hello", nullable[inet]], Placeholder["world", nullable[inet]]]]
  summon[AreComparable[Column["hello", nullable[text]], Placeholder["world", varchar]]]
  summon[AreComparable[Column["hello", text], Placeholder["world", nullable[varchar]]]               ]
  summon[AreComparable[Column["hello", nullable[text]], Placeholder["world", nullable[varchar]]]]
