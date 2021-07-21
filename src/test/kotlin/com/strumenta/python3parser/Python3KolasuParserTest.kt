package com.strumenta.python3parser

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
        TODO()
    }
}
