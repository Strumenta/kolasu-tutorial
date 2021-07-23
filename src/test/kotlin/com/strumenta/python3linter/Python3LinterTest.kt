package com.strumenta.python3linter

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class Python3LinterTest {

    @Test
    fun analyzePlates() {
        val file = File("src/test/resources/plates.py")
        val analysisResult = Python3Linter().analyze(file)
        assertEquals(
            AnalysisResult(
                file = file,
                findings = listOf(
                    Finding(severity = Severity.HIGH, message = "Way too many parameters in function generate_plates_simulation", line = 13),
                    Finding(severity = Severity.HIGH, message = "Way too many parameters in function _plates_simulation", line = 38),
                    Finding(severity = Severity.HIGH, message = "Way too many parameters in function world_gen", line = 55)
                )
            ),
            analysisResult
        )
    }

    @Test
    fun analyzeParamsUsage() {
        val file = File("src/test/resources/params_usage.py")
        val analysisResult = Python3Linter().analyze(file)
        assertEquals(
            AnalysisResult(
                file = file,
                findings = listOf(
                    Finding(severity = Severity.MEDIUM, message = "Unused parameter p2 in fun1", line = 1)
                )
            ),
            analysisResult
        )
    }
}
