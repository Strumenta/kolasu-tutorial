package com.strumenta.python3parser

import com.strumenta.kolasu.testing.assertASTsAreEqual
import com.strumenta.python3parser.ast.CompilationUnit
import com.strumenta.python3parser.ast.HelloStmt
import org.junit.Test
import kotlin.test.assertEquals

class Python3KolasuParserTest {

    @Test
    fun parsePlatesFromFile() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(
            this.javaClass.getResourceAsStream("/plates.py"),
            considerPosition = false
        )
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())
        assertASTsAreEqual(CompilationUnit(listOf(HelloStmt("John"))), result.root!!)
    }
}
