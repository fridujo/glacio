package com.github.fridujo.glacio.parsing.lexer;

public enum TokenType {
    INDENT, DEDENT, EOL, EOF,
    SPACE, COLON, TAG_DELIMITER, DOC_STRING_DELIMITER, TABLE_DELIMITER, COMMENT_DELIMITER,
    TEXT,
    FEATURE, BACKGROUND, SCENARIO_OUTLINE, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES
}
