package cql4s
package keyspaces


object People:
  case class User(
    name: String,
    address: (String, String, String, List[(Int, String)])
  )

  class phone extends udt[
    "people",
    "phone",
    (
      "country_code" :=: int,
      "number" :=: text
    )
  ]

  class address extends udt[
    "people",
    "address",
    (
      "street" :=: text,
      "city" :=: text,
      "zip" :=: text,
      "phones" :=: list[phone]
    )
  ]

  object users extends Table[
    "people",
    "users",
    (
      "name" :=: text,
      "address" :=: address
    )
  ]
