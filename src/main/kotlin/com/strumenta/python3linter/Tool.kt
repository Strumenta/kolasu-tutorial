package com.strumenta.python3linter

import java.io.File

fun analyzeFile(file: File) {
    println("[File $file]")
    val analysisResult = Python3Linter().analyze(file)
    if (analysisResult.findings.isEmpty()) {
        println("  no findings")
    } else {
        analysisResult.findings.sortedBy { it.severity }.forEach {
            println(" [${it.severity}] ${it.message}")
        }
    }
    println()
}

fun analyzeDirectory(file: File) {
    file.listFiles()?.forEach { child ->
        when {
            child.isDirectory -> {
                analyzeDirectory(child)
            }
            child.isFile -> {
                analyzeFile(child)
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please specify which programs you want us to analyze")
    }
    args.forEach {
        val file = File(it)
        if (file.exists()) {
            when {
                file.isDirectory -> {
                    println("path '$it' indicates a directory, analyzing each file in it")
                    analyzeDirectory(file)
                }
                file.isFile -> {
                    println("path '$it' indicates a file, analyzing it")
                    analyzeFile(file)
                }
                else -> {
                    System.err.println("path '$it' is neither a directory or a file: ignoring it")
                }
            }
        } else {
            System.err.println("path '$it' does not exist")
        }
    }
}
