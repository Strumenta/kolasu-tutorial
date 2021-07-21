package com.strumenta.python3parser.ast

import com.strumenta.kolasu.model.*

data class CompilationUnit(
    val stmts: List<Statement>,
    override val specifiedPosition: Position? = null
) : Node(specifiedPosition)

sealed class Statement(override val specifiedPosition: Position? = null) : Node(specifiedPosition)

data class FunctionDeclaration(
    override val name: String,
    val params: List<ParameterDeclaration>,
    val body: List<Statement>,
    override val specifiedPosition: Position? = null
) : Statement(specifiedPosition), Named

data class ParameterDeclaration(
    override val name: String,
    val defaultValue: Expression? = null,
    override val specifiedPosition: Position? = null
) : Statement(specifiedPosition), Named

sealed class Expression(override val specifiedPosition: Position? = null) : Node(specifiedPosition)
