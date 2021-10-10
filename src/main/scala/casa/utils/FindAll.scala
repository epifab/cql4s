package casa
package utils


trait FindAll[Haystack, Names, Values]:
  def get(haystack: Haystack): Values

object FindAll:

  given done[H]: FindAll[H, EmptyTuple, EmptyTuple] with
    def get(haystack: H): EmptyTuple = EmptyTuple

  given oneInOne[Name, Type]: FindAll[Column[Name, Type], Name, Column[Name, Type]] with
    def get(column: Column[Name, Type]): Column[Name, Type] = column

  given oneInHead[Name, Type, T <: Tuple]: FindAll[Column[Name, Type] *: T, Name, Column[Name, Type]] with
    def get(haystack: Column[Name, Type] *: T): Column[Name, Type] = haystack.head

  given oneInTail[Name, Type, H, T <: Tuple](using tailFinder: FindAll[T, Name, Column[Name, Type]]): FindAll[H *: T, Name, Column[Name, Type]] with
    def get(haystack: H *: T): Column[Name, Type] = tailFinder.get(haystack.tail)

  given tuple[FirstName, FirstValue, OtherNames <: Tuple, OtherValues <: Tuple, Haystack](using head: FindAll[Haystack, FirstName, FirstValue], tail: FindAll[Haystack, OtherNames, OtherValues]): FindAll[Haystack, FirstName *: OtherNames, FirstValue *: OtherValues] with
    def get(haystack: Haystack): FirstValue *: OtherValues = head.get(haystack) *: tail.get(haystack)

