package logo.lsp.capabilities;

import java.util.Map;

public class LogoHoverDocs {
    private LogoHoverDocs() {}

    public static final Map<String, String> DOCS = Map.ofEntries(

            // ── Motion ────────────────────────────────────────────────────────────

            Map.entry("forward",
                    "**forward** *distance* &nbsp;·&nbsp; alias: `fd`  \n" +
                            "Moves the turtle forward by *distance* steps in the direction it is currently facing.  \n" +
                            "```logo\nforward 100\nfd 50\n```"),

            Map.entry("fd",
                    "**fd** *distance* &nbsp;·&nbsp; alias for `forward`  \n" +
                            "Moves the turtle forward by *distance* steps.  \n" +
                            "```logo\nfd 100\n```"),

            Map.entry("back",
                    "**back** *distance* &nbsp;·&nbsp; alias: `bk`  \n" +
                            "Moves the turtle backward by *distance* steps (opposite to the current heading).  \n" +
                            "```logo\nback 50\n```"),

            Map.entry("bk",
                    "**bk** *distance* &nbsp;·&nbsp; alias for `back`  \n" +
                            "Moves the turtle backward by *distance* steps.  \n" +
                            "```logo\nbk 50\n```"),

            Map.entry("left",
                    "**left** *degrees* &nbsp;·&nbsp; alias: `lt`  \n" +
                            "Rotates the turtle left (counter-clockwise) by *degrees*.  \n" +
                            "```logo\nleft 90\n```"),

            Map.entry("lt",
                    "**lt** *degrees* &nbsp;·&nbsp; alias for `left`  \n" +
                            "Rotates the turtle left by *degrees*.  \n" +
                            "```logo\nlt 90\n```"),

            Map.entry("right",
                    "**right** *degrees* &nbsp;·&nbsp; alias: `rt`  \n" +
                            "Rotates the turtle right (clockwise) by *degrees*.  \n" +
                            "```logo\nright 90\n```"),

            Map.entry("rt",
                    "**rt** *degrees* &nbsp;·&nbsp; alias for `right`  \n" +
                            "Rotates the turtle right by *degrees*.  \n" +
                            "```logo\nrt 90\n```"),

            Map.entry("setx",
                    "**setx** *x*  \n" +
                            "Moves the turtle to the given X coordinate, keeping its current Y.  \n" +
                            "```logo\nsetx 100\n```"),

            Map.entry("sety",
                    "**sety** *y*  \n" +
                            "Moves the turtle to the given Y coordinate, keeping its current X.  \n" +
                            "```logo\nsety -50\n```"),

            Map.entry("setxy",
                    "**setxy** *x* *y*  \n" +
                            "Moves the turtle to the given (X, Y) position without drawing.  \n" +
                            "```logo\nsetxy 0 0\n```"),

            Map.entry("setpos",
                    "**setpos** *[ x y ]*  \n" +
                            "Moves the turtle to the position given as a list.  \n" +
                            "```logo\nsetpos [100 50]\n```"),

            Map.entry("home",
                    "**home**  \n" +
                            "Moves the turtle to the center of the screen (0, 0) and resets its heading to 0°.  \n" +
                            "```logo\nhome\n```"),

            Map.entry("setspeed",
                    "**setspeed** *speed*  \n" +
                            "Sets the turtle's movement speed (0 = instant, higher = slower animation).  \n" +
                            "```logo\nsetspeed 5\n```"),

            // ── Pen ───────────────────────────────────────────────────────────────

            Map.entry("penup",
                    "**penup** &nbsp;·&nbsp; alias: `pu`  \n" +
                            "Lifts the pen — turtle moves without drawing.  \n" +
                            "```logo\npenup\n```"),

            Map.entry("pu",
                    "**pu** &nbsp;·&nbsp; alias for `penup`  \n" +
                            "Lifts the pen so the turtle moves without drawing."),

            Map.entry("pendown",
                    "**pendown** &nbsp;·&nbsp; alias: `pd`  \n" +
                            "Lowers the pen — turtle draws as it moves.  \n" +
                            "```logo\npendown\n```"),

            Map.entry("pd",
                    "**pd** &nbsp;·&nbsp; alias for `pendown`  \n" +
                            "Lowers the pen so the turtle draws as it moves."),

            Map.entry("setpencolor",
                    "**setpencolor** *color*  \n" +
                            "Sets the pen color. *color* can be a number (0–15) or a color name.  \n" +
                            "```logo\nsetpencolor 4   ; red\nsetpencolor \"blue\n```"),

            Map.entry("setpensize",
                    "**setpensize** *size*  \n" +
                            "Sets the pen width in pixels.  \n" +
                            "```logo\nsetpensize 3\n```"),

            Map.entry("pencolor",
                    "**pencolor** &nbsp;·&nbsp; alias: `pc`  \n" +
                            "Returns the current pen color number."),

            Map.entry("fill",
                    "**fill**  \n" +
                            "Flood-fills the area around the turtle with the current pen color.  \n" +
                            "```logo\nfill\n```"),

            Map.entry("label",
                    "**label** *text*  \n" +
                            "Draws *text* at the turtle's current position.  \n" +
                            "```logo\nlabel \"Hello\n```"),

            // ── Screen ────────────────────────────────────────────────────────────

            Map.entry("clearscreen",
                    "**clearscreen** &nbsp;·&nbsp; alias: `cs`  \n" +
                            "Clears the drawing canvas and returns the turtle to home.  \n" +
                            "```logo\nclearscreen\n```"),

            Map.entry("cs",
                    "**cs** &nbsp;·&nbsp; alias for `clearscreen`  \n" +
                            "Clears the canvas and returns the turtle to home."),

            Map.entry("clean",
                    "**clean**  \n" +
                            "Clears the drawing canvas without moving the turtle.  \n" +
                            "```logo\nclean\n```"),

            Map.entry("hideturtle",
                    "**hideturtle** &nbsp;·&nbsp; alias: `ht`  \n" +
                            "Hides the turtle icon (drawing continues normally).  \n" +
                            "```logo\nhideturtle\n```"),

            Map.entry("ht",
                    "**ht** &nbsp;·&nbsp; alias for `hideturtle`  \n" +
                            "Hides the turtle icon."),

            Map.entry("showturtle",
                    "**showturtle** &nbsp;·&nbsp; alias: `st`  \n" +
                            "Makes the turtle icon visible again.  \n" +
                            "```logo\nshowturtle\n```"),

            Map.entry("st",
                    "**st** &nbsp;·&nbsp; alias for `showturtle`  \n" +
                            "Makes the turtle icon visible."),

            // ── Variables & data ──────────────────────────────────────────────────

            Map.entry("make",
                    "**make** *\"name* *value*  \n" +
                            "Assigns *value* to the global variable *name*.  \n" +
                            "```logo\nmake \"x 10\nmake \"name \"Logo\n```"),

            Map.entry("local",
                    "**local** *\"name*  \n" +
                            "Declares *name* as a local variable in the current procedure scope.  \n" +
                            "```logo\nlocal \"temp\n```"),

            Map.entry("thing",
                    "**thing** *\"name*  \n" +
                            "Returns the value of the variable named *name*. Equivalent to `:name`.  \n" +
                            "```logo\nthing \"x   ; same as :x\n```"),

            // ── Control flow ──────────────────────────────────────────────────────

            Map.entry("repeat",
                    "**repeat** *count* *[ body ]*  \n" +
                            "Executes *body* exactly *count* times.  \n" +
                            "```logo\nrepeat 4 [\n    forward 100\n    right 90\n]\n```"),

            Map.entry("forever",
                    "**forever** *[ body ]*  \n" +
                            "Executes *body* in an infinite loop. Use `stop` inside to exit.  \n" +
                            "```logo\nforever [\n    forward 10\n    right 5\n]\n```"),

            Map.entry("if",
                    "**if** *condition* *[ body ]*  \n" +
                            "Executes *body* only if *condition* is `true`.  \n" +
                            "```logo\nif :x > 0 [ forward :x ]\n```"),

            Map.entry("ifelse",
                    "**ifelse** *condition* *[ thenBody ]* *[ elseBody ]*  \n" +
                            "Executes *thenBody* if *condition* is true, otherwise executes *elseBody*.  \n" +
                            "```logo\nifelse :x > 0 [ forward :x ] [ back :x ]\n```"),

            Map.entry("for",
                    "**for** *[ var start end ]* *[ body ]*  \n" +
                            "Loops with *var* running from *start* to *end* (inclusive), stepping by 1 (or optional step).  \n" +
                            "```logo\nfor [i 1 10] [ print :i ]\nfor [i 0 100 5] [ forward :i ]\n```"),

            Map.entry("while",
                    "**while** *condition* *[ body ]*  \n" +
                            "Executes *body* repeatedly as long as *condition* is true.  \n" +
                            "```logo\nwhile [:x > 0] [ make \"x :x - 1 ]\n```"),

            Map.entry("stop",
                    "**stop**  \n" +
                            "Exits the current procedure immediately (like `return` with no value).  \n" +
                            "```logo\nto safeforward :n\n    if :n < 0 [stop]\n    forward :n\nend\n```"),

            Map.entry("output",
                    "**output** *value* &nbsp;·&nbsp; alias: `op`  \n" +
                            "Returns *value* from the current procedure (like `return` with a value).  \n" +
                            "```logo\nto double :n\n    output :n * 2\nend\n```"),

            Map.entry("op",
                    "**op** *value* &nbsp;·&nbsp; alias for `output`  \n" +
                            "Returns *value* from the current procedure."),

            // ── Procedure definition ──────────────────────────────────────────────

            Map.entry("to",
                    "**to** *name* *[:param ...]* *body* **end**  \n" +
                            "Defines a new procedure named *name* with optional parameters.  \n" +
                            "```logo\nto square :size\n    repeat 4 [ forward :size  right 90 ]\nend\n```"),

            Map.entry("end",
                    "**end**  \n" +
                            "Marks the end of a procedure definition started with `to`."),

            // ── I/O ───────────────────────────────────────────────────────────────

            Map.entry("print",
                    "**print** *value* &nbsp;·&nbsp; alias: `pr`  \n" +
                            "Prints *value* to the output, followed by a newline.  \n" +
                            "```logo\nprint \"Hello\nprint :x + 1\n```"),

            Map.entry("pr",
                    "**pr** *value* &nbsp;·&nbsp; alias for `print`  \n" +
                            "Prints *value* followed by a newline."),

            Map.entry("show",
                    "**show** *value*  \n" +
                            "Prints *value* with its type delimiters (lists shown with brackets).  \n" +
                            "```logo\nshow [1 2 3]   ; prints [1 2 3]\n```"),

            Map.entry("type",
                    "**type** *value*  \n" +
                            "Prints *value* without a trailing newline.  \n" +
                            "```logo\ntype \"Hello  type \" \"  type \"World\n```"),

            // ── Arithmetic ────────────────────────────────────────────────────────

            Map.entry("sum",
                    "**sum** *a* *b*  \n" +
                            "Returns *a* + *b*. Equivalent to `a + b`.  \n" +
                            "```logo\nprint sum 3 4   ; prints 7\n```"),

            Map.entry("difference",
                    "**difference** *a* *b*  \n" +
                            "Returns *a* - *b*.  \n" +
                            "```logo\nprint difference 10 3   ; prints 7\n```"),

            Map.entry("product",
                    "**product** *a* *b*  \n" +
                            "Returns *a* × *b*.  \n" +
                            "```logo\nprint product 4 5   ; prints 20\n```"),

            Map.entry("quotient",
                    "**quotient** *a* *b*  \n" +
                            "Returns *a* ÷ *b* (integer division).  \n" +
                            "```logo\nprint quotient 10 3   ; prints 3\n```"),

            Map.entry("remainder",
                    "**remainder** *a* *b*  \n" +
                            "Returns the remainder of *a* ÷ *b*.  \n" +
                            "```logo\nprint remainder 10 3   ; prints 1\n```"),

            Map.entry("sqrt",
                    "**sqrt** *n*  \n" +
                            "Returns the square root of *n*.  \n" +
                            "```logo\nprint sqrt 16   ; prints 4\n```"),

            Map.entry("abs",
                    "**abs** *n*  \n" +
                            "Returns the absolute value of *n*.  \n" +
                            "```logo\nprint abs -5   ; prints 5\n```"),

            Map.entry("power",
                    "**power** *base* *exp*  \n" +
                            "Returns *base* raised to the power *exp*.  \n" +
                            "```logo\nprint power 2 8   ; prints 256\n```"),

            // ── Comparison / logic ────────────────────────────────────────────────

            Map.entry("equalp",
                    "**equalp** *a* *b* &nbsp;·&nbsp; alias: `equal?`  \n" +
                            "Returns `true` if *a* equals *b*, otherwise `false`.  \n" +
                            "```logo\nif equalp :x 0 [ print \"zero ]\n```"),

            Map.entry("lessp",
                    "**lessp** *a* *b* &nbsp;·&nbsp; alias: `less?`  \n" +
                            "Returns `true` if *a* < *b*."),

            Map.entry("greaterp",
                    "**greaterp** *a* *b* &nbsp;·&nbsp; alias: `greater?`  \n" +
                            "Returns `true` if *a* > *b*."),

            Map.entry("and",
                    "**and** *a* *b*  \n" +
                            "Returns `true` if both *a* and *b* are true.  \n" +
                            "```logo\nif and :x > 0 :y > 0 [ print \"both positive ]\n```"),

            Map.entry("or",
                    "**or** *a* *b*  \n" +
                            "Returns `true` if at least one of *a* or *b* is true."),

            Map.entry("not",
                    "**not** *value*  \n" +
                            "Returns the logical negation of *value*.  \n" +
                            "```logo\nif not :done [ repeat 1 [ forward 10 ] ]\n```"),

            // ── List operations ───────────────────────────────────────────────────

            Map.entry("list",
                    "**list** *a* *b*  \n" +
                            "Returns a two-element list containing *a* and *b*.  \n" +
                            "```logo\nprint list 1 2   ; prints [1 2]\n```"),

            Map.entry("first",
                    "**first** *list*  \n" +
                            "Returns the first element of *list*.  \n" +
                            "```logo\nprint first [1 2 3]   ; prints 1\n```"),

            Map.entry("last",
                    "**last** *list*  \n" +
                            "Returns the last element of *list*.  \n" +
                            "```logo\nprint last [1 2 3]   ; prints 3\n```"),

            Map.entry("butfirst",
                    "**butfirst** *list* &nbsp;·&nbsp; alias: `bf`  \n" +
                            "Returns *list* without its first element.  \n" +
                            "```logo\nprint butfirst [1 2 3]   ; prints [2 3]\n```"),

            Map.entry("butlast",
                    "**butlast** *list* &nbsp;·&nbsp; alias: `bl`  \n" +
                            "Returns *list* without its last element.  \n" +
                            "```logo\nprint butlast [1 2 3]   ; prints [1 2]\n```"),

            Map.entry("count",
                    "**count** *list*  \n" +
                            "Returns the number of elements in *list*.  \n" +
                            "```logo\nprint count [1 2 3]   ; prints 3\n```"),

            Map.entry("item",
                    "**item** *index* *list*  \n" +
                            "Returns the element at position *index* (1-based) in *list*.  \n" +
                            "```logo\nprint item 2 [10 20 30]   ; prints 20\n```"),

            Map.entry("sentence",
                    "**sentence** *a* *b* &nbsp;·&nbsp; alias: `se`  \n" +
                            "Combines *a* and *b* into a flat list.  \n" +
                            "```logo\nprint sentence [1 2] [3 4]   ; prints [1 2 3 4]\n```"),

            Map.entry("fput",
                    "**fput** *item* *list*  \n" +
                            "Returns a new list with *item* prepended.  \n" +
                            "```logo\nprint fput 0 [1 2 3]   ; prints [0 1 2 3]\n```"),

            Map.entry("lput",
                    "**lput** *item* *list*  \n" +
                            "Returns a new list with *item* appended.  \n" +
                            "```logo\nprint lput 4 [1 2 3]   ; prints [1 2 3 4]\n```"),

            Map.entry("run",
                    "**run** *[ commands ]*  \n" +
                            "Executes *commands* as if they were typed directly.  \n" +
                            "```logo\nrun [ forward 100  right 90 ]\n```")
    );

    /**
     * Returns the markdown hover documentation for the given word (case-insensitive),
     * or null if no documentation is available.
     */
    public static String get(String word) {
        return DOCS.get(word.toLowerCase());
    }

}
