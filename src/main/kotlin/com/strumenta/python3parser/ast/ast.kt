package com.strumenta.python3parser.ast

import com.strumenta.kolasu.model.*

data class CompilationUnit(
    val helloStmts: List<HelloStmt>,
    override val specifiedPosition: Position? = null
) : Node(specifiedPosition)

data class HelloStmt(
    val name: String,
    override val specifiedPosition: Position? = null
) : Node(specifiedPosition)
