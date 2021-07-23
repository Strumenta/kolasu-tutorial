package com.strumenta.python3parser

import com.strumenta.kolasu.model.walkDescendants
import com.strumenta.kolasu.testing.IgnoreChildren
import com.strumenta.kolasu.testing.assertASTsAreEqual
import com.strumenta.python3parser.ast.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class Python3KolasuParserTest {

    @Test
    fun parsePlatesFromFile() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(this.javaClass.getResourceAsStream("/plates.py"), considerPosition = false)
        assert(result.correct)
        assertEquals(3, result.root!!.stmts.size)
        val generatePlatesSimulation = FunctionDeclaration(
            "generate_plates_simulation",
            listOf(
                ParameterDeclaration("seed"),
                ParameterDeclaration("width"),
                ParameterDeclaration("height"),
                ParameterDeclaration("sea_level", defaultValue = NumberLiteral("0.65")),
                ParameterDeclaration("erosion_period", defaultValue = NumberLiteral("60")),
                ParameterDeclaration("folding_ratio", defaultValue = NumberLiteral("0.02")),
                ParameterDeclaration("aggr_overlap_abs", defaultValue = NumberLiteral("1000000")),
                ParameterDeclaration("aggr_overlap_rel", defaultValue = NumberLiteral("0.33")),
                ParameterDeclaration("cycle_count", defaultValue = NumberLiteral("2")),
                ParameterDeclaration("num_plates", defaultValue = NumberLiteral("10")),
                ParameterDeclaration("verbose", defaultValue = FunctionInvocation(ReferenceExpression("get_verbose"), emptyList())),
            ),
            body = IgnoreChildren()
        )
        val platesSimulation = FunctionDeclaration(
            "_plates_simulation",
            listOf(
                ParameterDeclaration("name"),
                ParameterDeclaration("width"),
                ParameterDeclaration("height"),
                ParameterDeclaration("seed"),
                ParameterDeclaration(
                    "temps",
                    defaultValue = ArrayLiteral(
                        listOf(
                            NumberLiteral(".874"),
                            NumberLiteral(".765"),
                            NumberLiteral(".594"),
                            NumberLiteral(".439"),
                            NumberLiteral(".366"),
                            NumberLiteral(".124"),
                        )
                    )
                ),
                ParameterDeclaration(
                    "humids",
                    defaultValue = ArrayLiteral(
                        listOf(
                            NumberLiteral(".941"),
                            NumberLiteral(".778"),
                            NumberLiteral(".507"),
                            NumberLiteral(".236"),
                            NumberLiteral("0.073"),
                            NumberLiteral(".014"),
                            NumberLiteral(".002"),
                        )
                    )
                ),
                ParameterDeclaration("gamma_curve", defaultValue = NumberLiteral("1.25")),
                ParameterDeclaration("curve_offset", defaultValue = NumberLiteral(".2")),
                ParameterDeclaration("num_plates", defaultValue = NumberLiteral("10")),
                ParameterDeclaration("ocean_level", defaultValue = NumberLiteral("1.0")),
                ParameterDeclaration("step", defaultValue = FunctionInvocation(FieldAccess(ReferenceExpression("Step"), "full"), emptyList())),
                ParameterDeclaration("verbose", defaultValue = FunctionInvocation(ReferenceExpression("get_verbose"), emptyList())),
            ),
            body = IgnoreChildren()
        )
        val worldGen = FunctionDeclaration(
            "world_gen",
            listOf(
                ParameterDeclaration("name"),
                ParameterDeclaration("width"),
                ParameterDeclaration("height"),
                ParameterDeclaration("seed"),
                ParameterDeclaration(
                    "temps",
                    defaultValue = ArrayLiteral(
                        listOf(
                            NumberLiteral(".874"),
                            NumberLiteral(".765"),
                            NumberLiteral(".594"),
                            NumberLiteral(".439"),
                            NumberLiteral(".366"),
                            NumberLiteral(".124"),
                        )
                    )
                ),
                ParameterDeclaration(
                    "humids",
                    defaultValue = ArrayLiteral(
                        listOf(
                            NumberLiteral(".941"),
                            NumberLiteral(".778"),
                            NumberLiteral(".507"),
                            NumberLiteral(".236"),
                            NumberLiteral("0.073"),
                            NumberLiteral(".014"),
                            NumberLiteral(".002"),
                        )
                    )
                ),
                ParameterDeclaration("num_plates", defaultValue = NumberLiteral("10")),
                ParameterDeclaration("ocean_level", defaultValue = NumberLiteral("1.0")),
                ParameterDeclaration("step", defaultValue = FunctionInvocation(FieldAccess(ReferenceExpression("Step"), "full"), emptyList())),
                ParameterDeclaration("gamma_curve", defaultValue = NumberLiteral("1.25")),
                ParameterDeclaration("curve_offset", defaultValue = NumberLiteral(".2")),
                ParameterDeclaration("fade_borders", defaultValue = BooleanLiteral(true)),
                ParameterDeclaration("verbose", defaultValue = FunctionInvocation(ReferenceExpression("get_verbose"), emptyList())),
            ),
            body = IgnoreChildren()
        )
        assertASTsAreEqual(generatePlatesSimulation, result.root!!.stmts[0])
        assertASTsAreEqual(platesSimulation, result.root!!.stmts[1])
        assertASTsAreEqual(worldGen, result.root!!.stmts[2])
        assertASTsAreEqual(
            CompilationUnit(
                listOf(
                    generatePlatesSimulation,
                    platesSimulation,
                    worldGen
                )
            ),
            result.root!!
        )
    }

    @Test
    fun parseStatementsInGeneratePlatesSimulation() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(this.javaClass.getResourceAsStream("/plates.py"), considerPosition = false)
        val generatePlatesSimulation = result.root!!.topLevelFunctions.find { it.name == "generate_plates_simulation" }
        assertNotNull(generatePlatesSimulation)
        assertEquals(7, generatePlatesSimulation.body.size)
    }

    @Test
    fun distinguishParameterReference() {
        val kolasuParser = Python3KolasuParser()
        val result = kolasuParser.parse(this.javaClass.getResourceAsStream("/params_usage.py"), considerPosition = false)
        val fun2 = result.root!!.topLevelFunctions.find { it.name == "fun2" }
        assertNotNull(fun2)
        val assignments = fun2.walkDescendants(AssignmentExpression::class).toList()

        assertEquals("a", (assignments[0].assigned as ReferenceExpression).reference)
        assertEquals(true, (assignments[0].value as ReferenceExpression).isParameterReference)

        assertEquals("b", (assignments[1].assigned as ReferenceExpression).reference)
        assertEquals(false, (assignments[1].value as ReferenceExpression).isParameterReference)
    }
}
