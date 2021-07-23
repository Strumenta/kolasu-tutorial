package com.strumenta.python3linter

import com.strumenta.kolasu.model.walkDescendants
import com.strumenta.python3parser.Python3KolasuParser
import com.strumenta.python3parser.ast.ReferenceExpression
import java.io.File
import java.util.*

enum class Severity {
    LOW,
    MEDIUM,
    HIGH
}

data class Finding(
    val severity: Severity,
    val message: String,
    val line: Int? = null
)

data class AnalysisResult(val file: File, val findings: List<Finding>)

class Python3Linter {

    fun analyze(file: File): AnalysisResult {
        val findings = LinkedList<Finding>()
        val parsingResult = Python3KolasuParser().parse(file, true)
        if (parsingResult.root == null) {
            findings.add(Finding(Severity.HIGH, "The AST could not be produced. Aborting"))
        } else {
            if (!parsingResult.correct) {
                findings.add(Finding(Severity.MEDIUM, "The file contains error, this may affect the analysis"))
            }

            val ast = parsingResult.root!!

            // Check if the file is too long
            val numberOfLines = ast.position!!.end.line
            when {
                numberOfLines > 5000 -> findings.add(Finding(Severity.HIGH, "File way too long!"))
                numberOfLines > 2000 -> findings.add(Finding(Severity.MEDIUM, "File too long!"))
                numberOfLines > 500 -> findings.add(Finding(Severity.LOW, "File a bit long"))
            }

            // Check if we have too many functions
            when {
                ast.topLevelFunctions.size > 50 -> findings.add(Finding(Severity.HIGH, "Way too many top level functions!"))
                ast.topLevelFunctions.size > 30 -> findings.add(Finding(Severity.MEDIUM, "Too many top level functions!"))
                ast.topLevelFunctions.size > 15 -> findings.add(Finding(Severity.LOW, "A bit too many top level functions"))
            }

            ast.topLevelFunctions.forEach { fd ->
                // Check if functions have too many parameters
                val nParams = fd.params.size
                when {
                    nParams > 10 -> findings.add(Finding(Severity.HIGH, "Way too many parameters in function ${fd.name}", fd.position?.start?.line))
                    nParams > 7 -> findings.add(Finding(Severity.MEDIUM, "Too many parameters in function ${fd.name}", fd.position?.start?.line))
                    nParams > 4 -> findings.add(Finding(Severity.LOW, "A bit too many parameters in function ${fd.name}", fd.position?.start?.line))
                }

                // Check if there are unused parameters
                val referredNames: List<String> = fd.body.map { it.walkDescendants(ReferenceExpression::class).map { it.reference }.distinct().toList() }.flatten()
                fd.params.forEach {
                    if (it.name !in referredNames) {
                        findings.add(Finding(Severity.MEDIUM, "Unused parameter ${it.name} in ${fd.name}", it.position?.start?.line))
                    }
                }
            }
        }
        return AnalysisResult(file, findings)
    }
}
