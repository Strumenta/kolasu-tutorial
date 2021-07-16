package com.strumenta.python3parser

import org.junit.Test
import kotlin.test.assertEquals

class Python3LexerTest {

    @Test
    fun lexExample1FromString() {
        val code = "hello John"
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.lex(code)
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())
        assertEquals(3, result.tokens.size)

        var i = 0
        assertEquals(Python3Lexer.HELLO, result.tokens[i].type)
        assertEquals("hello", result.tokens[i].text)

        i++
        assertEquals(Python3Lexer.ID, result.tokens[i].type)
        assertEquals("John", result.tokens[i].text)

        i++
        assertEquals(Python3Lexer.EOF, result.tokens[i].type)
    }

    @Test
    fun lexExample1FromFile() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.lex(this.javaClass.getResourceAsStream("/example1.hello"))
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())
        assertEquals(3, result.tokens.size)

        var i = 0
        assertEquals(Python3Lexer.HELLO, result.tokens[i].type)
        assertEquals("hello", result.tokens[i].text)

        i++
        assertEquals(Python3Lexer.ID, result.tokens[i].type)
        assertEquals("John", result.tokens[i].text)

        i++
        assertEquals(Python3Lexer.EOF, result.tokens[i].type)
    }
}
