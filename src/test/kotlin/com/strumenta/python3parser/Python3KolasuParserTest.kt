package com.strumenta.python3parser

import com.strumenta.kolasu.testing.assertASTsAreEqual
import com.strumenta.python3parser.ast.CompilationUnit
import com.strumenta.python3parser.ast.HelloStmt
import org.junit.Test
import kotlin.test.assertEquals

class Python3KolasuParserTest {

    @Test
    fun parseExample1FromString() {
        val code = "hello John"
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(code, considerPosition = false)
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())
        assertASTsAreEqual(CompilationUnit(listOf(HelloStmt("John"))), result.root!!)
    }

    @Test
    fun parseExample1FromFile() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(
            this.javaClass.getResourceAsStream("/example1.hello"),
            considerPosition = false
        )
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())
        assertASTsAreEqual(CompilationUnit(listOf(HelloStmt("John"))), result.root!!)
    }
}
