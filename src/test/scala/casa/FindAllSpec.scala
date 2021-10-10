package casa

import casa.utils.{ColumnsFactory, FindAll}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class FindAllSpec extends AnyFreeSpec with Matchers:

  class Find[Names]:
    def in[Haystack](haystack: Haystack): FindIn[Names, Haystack] = FindIn(haystack)

  class FindIn[Names, Haystack](haystack: Haystack):
    def get[Values](using finder: FindAll[Haystack, Names, Values]): Values = finder.get(haystack)

  "Find one column" - {
    "in a haystack made of a single column" in {
      Find["hello"].in(Column["hello", varchar]).get shouldBe a[Column["hello", varchar]]
    }

    "in the head of the haystack" in {
      Find["hello"].in((Column["hello", varchar], Column["world", int])).get shouldBe a[Column["hello", varchar]]
    }

    "in the tail of the haystack" in {
      Find["hello"].in((Column["world", int], Column["hello", varchar])).get shouldBe a[Column["hello", varchar]]
    }
  }

  "Find one column of a tuple" - {
    "in a haystack made of a single column" in {
      Find["hello" *: EmptyTuple].in(Column["hello", varchar]).get shouldBe a[(Column["hello", varchar] *: EmptyTuple)]
    }

    "in the head of the haystack" in {
      Find["hello" *: EmptyTuple].in((Column["hello", varchar], Column["world", int])).get shouldBe a[(Column["hello", varchar] *: EmptyTuple)]
    }

    "in the tail of the haystack" in {
      Find["hello" *: EmptyTuple].in((Column["world", int], Column["hello", varchar])).get shouldBe a[(Column["hello", varchar] *: EmptyTuple)]
    }
  }

  "Find two columns" - {
    "in a haystack made of two columns in the same order" in {
      Find[("hello", "world")].in((Column["hello", varchar], Column["world", int])).get shouldBe a[(Column["hello", varchar], Column["world", int])]
    }

    "in a haystack made of two columns in the opposite order" in {
      Find[("hello", "world")].in((Column["world", int], Column["hello", varchar])).get shouldBe a[(Column["hello", varchar], Column["world", int])]
    }

    "with the same name" in {
      Find[("hello", "hello")].in((Column["world", int], Column["hello", varchar])).get shouldBe a[(Column["hello", varchar], Column["hello", varchar])]
    }
  }
