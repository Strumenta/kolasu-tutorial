package com.strumenta.python3parser

import com.strumenta.kolasu.testing.assertParseTreeStr
import org.junit.Test
import kotlin.test.assertEquals

class Python3FirstStageParserTest {

    @Test
    fun parsePlatesFromFile() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parseFirstStage(this.javaClass.getResourceAsStream("/plates.py"))
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())

        assertParseTreeStr(
            """CompilationUnit
                  |  HelloStmt
                  |    T:HELLO[hello]
                  |    T:ID[John]
                  |  T:EOF[<EOF>]""".trimMargin("|"),
            result.root!!, Python3Parser.VOCABULARY
        )
    }
}
