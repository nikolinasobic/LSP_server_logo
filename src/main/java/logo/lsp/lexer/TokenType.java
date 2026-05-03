package logo.lsp.lexer;

public enum TokenType {
    NUMBER,
    STRING,
    BOOLEAN,

    IDENTIFIER,

    VARIABLE,

    // turtle motion
    FORWARD, BACK, LEFT, RIGHT,
    SETX, SETY, SETXY, SETPOS, SETHEADING, SETSPEED,
    HOME, ARC, ELLIPSE,

    // turtle motion queries
    POS, XCOR, YCOR, HEADING, TOWARDS,

    // turtle/window control
    WRAP, WINDOW, FENCE,
    HIDETURTLE, SHOWTURTLE,

    // turtle/window queries
    SHOWNP,

    // pen control
    PENUP, PENDOWN, PENCOLOR, SETPENCOLOR, SETPENSIZE,
    FILL, FILLED, LABEL, SETLABELHEIGHT, CHANGESHAPE,

    // pen queries
    PENDOWNP, PENSIZE, LABELSIZE,

    // screen
    CLEAN, CLEARSCREEN,

    // variables
    MAKE, LOCAL, THING, NAME, LOCALMAKE,

    // I/O
    PRINT, SHOW, TYPE, READWORD, READLIST,

    // control flow
    IF, IFELSE, TEST, IFTRUE, IFFALSE,
    REPEAT, FOREVER, WHILE, UNTIL, FOR,
    DOTIMES, DO_WHILE, DO_UNTIL,
    WAIT, BYE, REPCOUNT,

    // procedure
    TO, END, OUTPUT, STOP, DEFINE, DEF,
    RUN, APPLY,

    // lists
    LIST, FIRST, LAST, BUTFIRST, BUTLAST, ITEM, COUNT,
    SENTENCE, FPUT, LPUT, PICK,

    // logic
    AND, OR, NOT,

    // arithmetic
    SUM, DIFFERENCE, PRODUCT, QUOTIENT, REMAINDER, MODULO, POWER, SQRT, ABS, MINUS,
    RANDOM,

    // comparison
    EQUALP, NOTEQUALP, LESSP, GREATERP, LESSEQUALP, GREATEREQUALP, EQUAL_SIGN,

    // predicates
    WORDP, LISTP, ARRAYP, ARRAY, NUMBERP, EMPTYP, BEFOREP, SUBSTRINGP,

    LPAREN,
    RPAREN,
    LBRACKET,
    RBRACKET,
    COLON,          // before variable names
    PLUS,
    STAR,
    SLASH,
    LESS,
    GREATER,
    CARET,      // ^

    NEWLINE,
    EOF,
    UNKNOWN

}
