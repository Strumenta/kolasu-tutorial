parser grammar Python3Parser;

options {   tokenVocab = Python3Lexer; }

compilationUnit: helloStmt* EOF;

helloStmt: HELLO name=ID;
