package com.strumenta.python3parser

import com.strumenta.kolasu.testing.assertParseTreeStr
import org.junit.Test
import kotlin.test.assertEquals

class Python3FirstStageParserTest {

    @Test
    fun parseSimpleFunctionFromString() {
        val code = """def foo(a, b=10):
                     |    pass
        """.trimMargin("|")
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parseFirstStage(code)
        assertEquals(true, result.correct)
        assertEquals(true, result.issues.isEmpty())

        assertParseTreeStr(
            """File_input
                  |  Stmt
                  |    Compound_stmt
                  |      Funcdef
                  |        T:DEF[def]
                  |        T:NAME[foo]
                  |        Parameters
                  |          T:OPEN_PAREN[(]
                  |          Typedargslist
                  |            Tfpdef
                  |              T:NAME[a]
                  |            T:COMMA[,]
                  |            Tfpdef
                  |              T:NAME[b]
                  |            T:ASSIGN[=]
                  |            Test
                  |              Or_test
                  |                And_test
                  |                  Not_test
                  |                    Comparison
                  |                      Expr
                  |                        Xor_expr
                  |                          And_expr
                  |                            Shift_expr
                  |                              Arith_expr
                  |                                Term
                  |                                  Factor
                  |                                    Power
                  |                                      Atom_expr
                  |                                        Atom
                  |                                          T:NUMBER[10]
                  |          T:CLOSE_PAREN[)]
                  |        T:COLON[:]
                  |        Suite
                  |          T:NEWLINE[ ]
                  |          T:INDENT[    ]
                  |          Stmt
                  |            Simple_stmt
                  |              Small_stmt
                  |                Pass_stmt
                  |                  T:PASS[pass]
                  |              T:NEWLINE[s]
                  |          T:DEDENT[s]
                  |  T:EOF[ pass]""".trimMargin("|"),
            result.root!!, Python3Parser.VOCABULARY
        )
    }
}
