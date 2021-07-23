package com.strumenta.python3parser.ast

import com.strumenta.kolasu.model.*

data class CompilationUnit(
    val stmts: List<Statement>,
    override val specifiedPosition: Position? = null
) : Node(specifiedPosition) {

    @Derived
    val topLevelFunctions
        get() = stmts.filterIsInstance(FunctionDeclaration::class.java)
}

//
// Statements
//

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

data class IfStatement(
    val condition: Expression,
    val body: List<Statement>,
    override val specifiedPosition: Position? = null
) : Statement(specifiedPosition)

data class WhileStatement(
    val condition: Expression,
    val body: List<Statement>,
    override val specifiedPosition: Position? = null
) : Statement(specifiedPosition)

data class ExpressionStatement(val expression: Expression, override val specifiedPosition: Position? = null) :
    Statement(specifiedPosition)

data class ReturnStatement(val value: Expression?, override val specifiedPosition: Position? = null) :
    Statement(specifiedPosition)

//
// Expressions
//

sealed class Expression(override val specifiedPosition: Position? = null) : Node(specifiedPosition)

data class NumberLiteral(val value: String, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class ReferenceExpression(val reference: String, override val specifiedPosition: Position? = null) : Expression(specifiedPosition) {
    val isParameterReference: Boolean
        get() = this.findAncestorOfType(FunctionDeclaration::class.java)?.params?.any { it.name == reference } ?: false
}

data class ArrayLiteral(val elements: List<Expression>, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class FunctionInvocation(
    val function: Expression,
    val paramValues: List<ParameterAssignment>,
    override val specifiedPosition: Position? = null
) : Expression(specifiedPosition)

data class FieldAccess(val container: Expression, val fieldName: String, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class BooleanLiteral(val value: Boolean, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class StringLiteral(val value: String, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class AssignmentExpression(val assigned: Expression, val value: Expression, override val specifiedPosition: Position? = null) :
    Expression(specifiedPosition)

enum class ComparisonOp {
    LESS_THAN(),
    LESS_EQ(),
    GREATER_THAN(),
    GREATER_EQ(),
    EQUAL();
    companion object {
        fun from(text: String): ComparisonOp {
            return when (text) {
                "==" -> EQUAL
                else -> TODO()
            }
        }
    }
}

data class ComparisonExpr(val op: ComparisonOp, val left: Expression, val right: Expression, override val specifiedPosition: Position? = null) :
    Expression(specifiedPosition)

data class AdditionExpression(val left: Expression, val right: Expression, override val specifiedPosition: Position? = null) :
    Expression(specifiedPosition)

data class SubtractionExpression(val left: Expression, val right: Expression, override val specifiedPosition: Position? = null) :
    Expression(specifiedPosition)

data class TupleExpression(val elements: List<Expression>, override val specifiedPosition: Position? = null) : Expression(specifiedPosition)

data class ParameterAssignment(val value: Expression, val name: String? = null, override val specifiedPosition: Position? = null) : Node(specifiedPosition)

data class NoneLiteral(override val specifiedPosition: Position? = null) : Expression(specifiedPosition)
