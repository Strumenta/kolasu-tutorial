package com.strumenta.python3parser.parsetreetoast

import com.strumenta.kolasu.mapping.position
import com.strumenta.kolasu.mapping.toPosition
import com.strumenta.python3parser.Python3Lexer
import com.strumenta.python3parser.Python3Parser
import com.strumenta.python3parser.ast.*
import org.antlr.v4.runtime.tree.TerminalNode

private val Python3Parser.TrailerContext.isInvocation: Boolean
    get() = this.OPEN_PAREN() != null

fun Python3Parser.File_inputContext.toAst(considerPosition: Boolean = true): CompilationUnit {
    return CompilationUnit(
        this.stmt().map { it.toAst(considerPosition) }.flatten(),
        toPosition(considerPosition)
    )
}

private fun Python3Parser.SuiteContext.toAst(considerPosition: Boolean = true): List<Statement> {
    val stmtsA: List<Statement> = this.simple_stmt()?.toAst(considerPosition) ?: emptyList()
    val stmtsB: List<Statement> = this.stmt().map { it.toAst(considerPosition) }.flatten()
    return stmtsA + stmtsB
}

private fun Python3Parser.Simple_stmtContext.toAst(considerPosition: Boolean = true): List<Statement> {
    return this.small_stmt().mapNotNull { it.toAst(considerPosition) }
}

private fun Python3Parser.Small_stmtContext.toAst(considerPosition: Boolean = true): Statement? {
    return when {
        this.import_stmt() != null -> null
        this.expr_stmt() != null -> this.expr_stmt().toAst(considerPosition)
        this.flow_stmt() != null -> {
            return when {
                this.flow_stmt().return_stmt() != null -> {
                    return ReturnStatement(this.flow_stmt().return_stmt().testlist()?.toAst(considerPosition), toPosition(considerPosition))
                }
                else -> TODO()
            }
        }
        else -> TODO()
    }
}

private fun Python3Parser.TestlistContext.toAst(considerPosition: Boolean = true): Expression {
    return if (this.test().size == 1) {
        this.test()[0].toAst(considerPosition)
    } else {
        TupleExpression(this.test().map { it.toAst(considerPosition) }, toPosition(considerPosition))
    }
}

private fun Python3Parser.Expr_stmtContext.toAst(considerPosition: Boolean = true): Statement {
    return when {
        this.ASSIGN().size == 1 -> {
            val assigned = this.testlist_star_expr()[0].toAst(considerPosition)
            val value = if (this.yield_expr().size == 1) {
                require(this.testlist_star_expr().size == 1)
                this.yield_expr()[0].toAst(considerPosition)
            } else {
                require(this.testlist_star_expr().size == 2)
                this.testlist_star_expr()[1].toAst(considerPosition)
            }
            ExpressionStatement(AssignmentExpression(assigned, value, toPosition(considerPosition)))
        }
        this.children.size == 1 -> {
            ExpressionStatement(this.testlist_star_expr()[0].toAst(considerPosition))
        }
        else -> TODO()
    }
}

private fun Python3Parser.Yield_exprContext.toAst(considerPosition: Boolean = true): Expression {
    TODO()
}

private fun Python3Parser.Testlist_star_exprContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.children.size == 1) {
        if (this.test().size == 1) {
            return this.test()[0].toAst(considerPosition)
        } else {
            TODO()
        }
    } else {
        return TupleExpression(this.test().map { it.toAst(considerPosition) }, toPosition(considerPosition))
    }
}

private fun Python3Parser.StmtContext.toAst(considerPosition: Boolean = true): List<Statement> {
    return when {
        this.compound_stmt()?.funcdef() != null -> {
            val parseTreeNode = this.compound_stmt().funcdef()!!
            val params = parseTreeNode.parameters().typedargslist().tfpdef().sortedBy { it.position }
            val initialValues = parseTreeNode.parameters().typedargslist().test().sortedBy { it.position }
            val initialValuesByParam = HashMap<Python3Parser.TfpdefContext, Python3Parser.TestContext>()
            initialValues.forEach { iv ->
                val param = params.last { p -> p.position.end.isBefore(iv.position.start) }
                require(param !in initialValuesByParam)
                initialValuesByParam[param] = iv
            }
            val paramsWithInitialValues = params.map { Pair(it, initialValuesByParam[it]) }

            return listOf(
                FunctionDeclaration(
                    parseTreeNode.NAME().text,
                    paramsWithInitialValues.map { it.toAst(considerPosition) },
                    parseTreeNode.suite().toAst(considerPosition),
                    toPosition(considerPosition)
                )
            )
        }
        this.compound_stmt()?.if_stmt() != null -> {
            val parseTreeNode = this.compound_stmt()?.if_stmt()!!
            return listOf(
                IfStatement(
                    parseTreeNode.test()[0].toAst(considerPosition),
                    parseTreeNode.suite()[0].toAst(considerPosition),
                    toPosition(considerPosition)
                )
            )
        }
        this.compound_stmt()?.while_stmt() != null -> {
            val parseTreeNode = this.compound_stmt()?.while_stmt()!!
            require(parseTreeNode.suite().size == 1)
            return listOf(
                WhileStatement(
                    parseTreeNode.test().toAst(considerPosition),
                    parseTreeNode.suite()[0].toAst(considerPosition),
                    toPosition(considerPosition)
                )
            )
        }
        this.simple_stmt() != null -> {
            return this.simple_stmt().toAst(considerPosition)
        }
        else -> TODO()
    }
}

private fun Pair<Python3Parser.TfpdefContext, Python3Parser.TestContext?>.toAst(considerPosition: Boolean = true): ParameterDeclaration {
    return ParameterDeclaration(
        this.first.NAME().text, this.second?.toAst(considerPosition),
        first.toPosition(considerPosition)
    )
}

private fun Python3Parser.TestContext.toAst(considerPosition: Boolean = true): Expression {
    // this.or_test()[0].and_test()[0].not_test()[0].comparison().expr()[0].xor_expr()[0].and_expr()
    return when {
        this.or_test().size == 1 -> this.or_test()[0].toAst(considerPosition)
        else -> TODO()
    }
}

private fun Python3Parser.Or_testContext.toAst(considerPosition: Boolean = true): Expression {
    return when {
        this.and_test().size == 1 -> this.and_test()[0].toAst(considerPosition)
        else -> TODO()
    }
}

private fun Python3Parser.And_testContext.toAst(considerPosition: Boolean = true): Expression {
    return when {
        this.not_test().size == 1 -> this.not_test()[0].toAst(considerPosition)
        else -> TODO()
    }
}

private fun Python3Parser.Not_testContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.NOT() == null) {
        return this.comparison().toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.ComparisonContext.toAst(considerPosition: Boolean = true): Expression {
    // this.expr()[0].xor_expr()[0].and_expr()[0].shift_expr()[0].arith_expr()[0].term()[0]
    if (this.expr().size == 1) {
        return this.expr()[0].toAst(considerPosition)
    } else if (this.expr().size == 2) {
        return ComparisonExpr(
            ComparisonOp.from(this.comp_op()[0].text), this.expr()[0].toAst(considerPosition),
            this.expr()[1].toAst(considerPosition), toPosition(considerPosition)
        )
    } else {
        TODO()
    }
}

private fun Python3Parser.ExprContext.toAst(considerPosition: Boolean = true): Expression {
    if (xor_expr().size == 1) {
        return xor_expr()[0].toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.Xor_exprContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.and_expr().size == 1) {
        return this.and_expr()[0].toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.And_exprContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.shift_expr().size == 1) {
        return this.shift_expr()[0].toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.Shift_exprContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.arith_expr().size == 1) {
        return this.arith_expr()[0].toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.Arith_exprContext.toAst(considerPosition: Boolean = true, startIndex: Int = 0): Expression {
    val relevantChildren = this.children.subList(startIndex, this.childCount)
    when (relevantChildren.size) {
        1 -> {
            return (relevantChildren[0] as Python3Parser.TermContext).toAst(considerPosition)
        }
        else -> {
            val left = (relevantChildren[0] as Python3Parser.TermContext).toAst(considerPosition)
            val right = this.toAst(considerPosition, startIndex + 2)
            val operator = relevantChildren[1] as TerminalNode
            return when (operator.symbol.type) {
                Python3Lexer.ADD -> {
                    AdditionExpression(left, right, toPosition(considerPosition))
                }
                Python3Lexer.MINUS -> {
                    SubtractionExpression(left, right, toPosition(considerPosition))
                }
                else -> {
                    TODO()
                }
            }
        }
    }
}

private fun Python3Parser.TermContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.factor().size == 1) {
        return this.factor()[0].toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.FactorContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.power() != null) {
        return this.power().toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.PowerContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.factor() == null) {
        return this.atom_expr().toAst(considerPosition)
    } else {
        TODO()
    }
}

private fun Python3Parser.Atom_exprContext.toAst(considerPosition: Boolean = true): Expression {
    if (this.AWAIT() != null) {
        TODO()
    }

    fun processTrailers(currentExpr: Expression, trailers: List<Python3Parser.TrailerContext>): Expression {
        return if (trailers.isEmpty()) {
            currentExpr
        } else {
            processTrailers(trailers.first().toAst(currentExpr, considerPosition), trailers.subList(1, trailers.size))
        }
    }

    return processTrailers(this.atom().toAst(considerPosition), this.trailer())
}

private fun Python3Parser.TrailerContext.toAst(currentExpr: Expression, considerPosition: Boolean = true): Expression {
    return when {
        this.OPEN_PAREN() != null -> {
            FunctionInvocation(currentExpr, this.arglist()?.toAst(considerPosition) ?: emptyList(), toPosition(considerPosition))
        }
        this.NAME() != null -> {
            FieldAccess(currentExpr, this.NAME().text, toPosition(considerPosition))
        }
        else -> TODO("Cannot convert: ${this.text}")
    }
}

private fun Python3Parser.ArglistContext.toAst(considerPosition: Boolean = true): List<ParameterAssignment> {
    return this.argument().map { it.toAst(considerPosition) }
}

private fun Python3Parser.ArgumentContext.toAst(considerPosition: Boolean = true): ParameterAssignment {
    require(this.comp_for() == null)
    if (this.test().size == 2) {
        return ParameterAssignment(this.test()[1].toAst(considerPosition), this.test()[0].text, toPosition(considerPosition))
    }
    return ParameterAssignment(this.test()[0].toAst(considerPosition), null, toPosition(considerPosition))
}

private fun Python3Parser.AtomContext.toAst(considerPosition: Boolean = true): Expression {
    return when {
        this.NUMBER() != null -> NumberLiteral(this.NUMBER().text, toPosition(considerPosition))
        this.OPEN_BRACK() != null -> {
            return ArrayLiteral(this.testlist_comp()?.toAst(considerPosition) ?: emptyList())
        }
        this.NAME() != null -> {
            return ReferenceExpression(this.NAME().text, toPosition(considerPosition))
        }
        this.TRUE() != null -> {
            return BooleanLiteral(true, toPosition(considerPosition))
        }
        this.FALSE() != null -> {
            return BooleanLiteral(false, toPosition(considerPosition))
        }
        this.STRING().size == 1 -> {
            return StringLiteral(this.STRING()[0].text, toPosition(considerPosition))
        }
        this.OPEN_PAREN() != null -> {
            return if (this.yield_expr() != null) {
                this.yield_expr().toAst(considerPosition)
            } else if (this.testlist_comp() != null) {
                TupleExpression(this.testlist_comp().toAst(considerPosition), toPosition(considerPosition))
            } else {
                TupleExpression(emptyList(), toPosition(considerPosition))
            }
        }
        this.NONE() != null -> {
            return NoneLiteral(toPosition(considerPosition))
        }
        else -> TODO("Cannot convert: ${this.text}")
    }
}

private fun Python3Parser.Testlist_compContext.toAst(considerPosition: Boolean = true): List<Expression> {
    if (this.comp_for() != null) {
        TODO("Cannot convert: ${this.text}")
    }
    if (this.star_expr().size != 0) {
        TODO("Cannot convert: ${this.text}")
    }
    return this.test().map { it.toAst(considerPosition) }
}
