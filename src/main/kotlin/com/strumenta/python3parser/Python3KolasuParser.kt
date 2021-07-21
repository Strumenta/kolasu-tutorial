package com.strumenta.python3parser

import com.strumenta.kolasu.parsing.KolasuParser
import com.strumenta.python3parser.ast.CompilationUnit
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import java.io.InputStream

class Python3KolasuParser : KolasuParser<CompilationUnit, Python3Parser, Python3Parser.File_inputContext>() {

    override fun createANTLRLexer(inputStream: InputStream): Lexer {
        return Python3Lexer(CharStreams.fromStream(inputStream))
    }

    override fun createANTLRParser(tokenStream: TokenStream): Python3Parser {
        return Python3Parser(tokenStream)
    }

    override fun invokeRootRule(parser: Python3Parser): Python3Parser.File_inputContext? {
        return parser.file_input()
    }

    override fun parseTreeToAst(parseTreeRoot: Python3Parser.File_inputContext, considerPosition: Boolean): CompilationUnit? {
        TODO()
    }
}
