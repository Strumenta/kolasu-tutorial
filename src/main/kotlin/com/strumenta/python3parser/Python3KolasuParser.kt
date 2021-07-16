package com.strumenta.python3parser

import com.strumenta.kolasu.parsing.KolasuParser
import com.strumenta.python3parser.ast.CompilationUnit
import com.strumenta.python3parser.parsetreetoast.toAst
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import java.io.InputStream

class Python3KolasuParser : KolasuParser<CompilationUnit, Python3Parser, Python3Parser.CompilationUnitContext>() {

    override fun createANTLRLexer(inputStream: InputStream): Lexer {
        return Python3Lexer(CharStreams.fromStream(inputStream))
    }

    override fun createANTLRParser(tokenStream: TokenStream): Python3Parser {
        return Python3Parser(tokenStream)
    }

    override fun invokeRootRule(parser: Python3Parser): Python3Parser.CompilationUnitContext? {
        return parser.compilationUnit()
    }

    override fun parseTreeToAst(parseTreeRoot: Python3Parser.CompilationUnitContext, considerPosition: Boolean): CompilationUnit? {
        return parseTreeRoot.toAst(considerPosition)
    }
}
