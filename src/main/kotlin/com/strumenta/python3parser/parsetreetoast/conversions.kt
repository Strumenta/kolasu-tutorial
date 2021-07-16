package com.strumenta.python3parser.parsetreetoast

import com.strumenta.kolasu.mapping.toPosition
import com.strumenta.python3parser.Python3Parser
import com.strumenta.python3parser.ast.CompilationUnit
import com.strumenta.python3parser.ast.HelloStmt

fun Python3Parser.CompilationUnitContext.toAst(considerPosition: Boolean = true): CompilationUnit {
    return CompilationUnit(this.helloStmt().map { it.toAst(considerPosition) }, toPosition(considerPosition))
}

fun Python3Parser.HelloStmtContext.toAst(considerPosition: Boolean = true): HelloStmt {
    return HelloStmt(this.name.text, toPosition(considerPosition))
}
