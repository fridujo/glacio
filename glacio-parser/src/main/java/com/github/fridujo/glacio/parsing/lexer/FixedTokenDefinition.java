package com.github.fridujo.glacio.parsing.lexer;

public enum FixedTokenDefinition {
    EOL(TokenType.EOL, '\n'),
    COLON(TokenType.COLON, ':'),
    TAG_DELIMITER(TokenType.TAG_DELIMITER, '@'),
    COMMENT_DELIMITER(TokenType.COMMENT_DELIMITER, '#'),
    TABLE_DELIMITER(TokenType.TABLE_DELIMITER, '|'),
    DOC_STRING_DELIMITER(TokenType.DOC_STRING_DELIMITER, "\"\"\"");

    private final TokenType type;
    private final char singleCharacter;
    private final String literalString;

    FixedTokenDefinition(TokenType type, char literalCharacter) {
        this.type = type;
        this.singleCharacter = literalCharacter;
        this.literalString = String.valueOf(literalCharacter);
    }

    FixedTokenDefinition(TokenType type, String literalString) {
        this.type = type;
        this.singleCharacter = Character.MIN_VALUE;
        this.literalString = literalString;
    }

    public TokenType getType() {
        return type;
    }

    public char getSingleCharacter() {
        return singleCharacter;
    }

    public String getLiteralString() {
        return literalString;
    }
}
