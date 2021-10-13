package cql4s
package keyspaces


object People:
  case class Phone(countryCode: Int, number: String)
  case class Address(street: String, city: String, zip: String, phones: List[Phone])
  case class User(name: String, address: Address)

  class _phone extends udt[
    Phone,
    "people",
    "phone",
    (
      "country_code" :=: int,
      "number" :=: text
    )
  ]

  class _address extends udt[
    Address,
    "people",
    "address",
    (
      "street" :=: text,
      "city" :=: text,
      "zip" :=: text,
      "phones" :=: list[_phone]
    )
  ]

  object users extends Table[
    "people",
    "users",
    (
      "name" :=: text,
      "address" :=: _address
    )
  ]
