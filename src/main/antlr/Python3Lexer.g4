lexer grammar Python3Lexer;

// Keywords
HELLO : 'hello';

// Identifiers
ID : [a-zA-Z][_a-zA-Z0-9]*;

// Whitespace
WS : [\r\n\t ] -> channel(HIDDEN);
