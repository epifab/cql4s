package cql4s.dsl

import cql4s.compiler.CommandCompiler

class Truncate[Keyspace, TableName, TableColumns](val table: Table[Keyspace, TableName, TableColumns]):

  def compile[Input](using compiler: CommandCompiler[this.type, Input]): Command[Input] =
    compiler.build(this)
