package logo.lsp.capabilities;

import java.util.Map;

public class LogoHoverDocs {
    private LogoHoverDocs() {}

    private static final Map<String, String> DOCS = Map.ofEntries(

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

            Map.entry("setheading",
                    "**setheading** *angle* &nbsp;·&nbsp; aliases: `seth`, `sh`  \n" +
                            "Rotates the turtle to the given absolute heading (0 = up, 90 = right).  \n" +
                            "```logo\nsetheading 90\nsh 145\n```"),

            Map.entry("seth",
                    "**seth** *angle* &nbsp;·&nbsp; alias for `setheading`  \n" +
                            "Rotates the turtle to an absolute heading."),

            Map.entry("sh",
                    "**sh** *angle* &nbsp;·&nbsp; alias for `setheading`  \n" +
                            "Rotates the turtle to an absolute heading."),

            Map.entry("arc",
                    "**arc** *angle* *radius*  \n" +
                            "Draws an arc of the given *radius* sweeping *angle* degrees.  \n" +
                            "```logo\narc 360 50   ; full circle\narc 180 30\n```"),

            Map.entry("ellipse",
                    "**ellipse** *width* *height*  \n" +
                            "Draws an ellipse with the specified *width* and *height*.  \n" +
                            "```logo\nellipse 80 40\n```"),

            Map.entry("home",
                    "**home**  \n" +
                            "Moves the turtle to the center of the screen (0, 0) and resets its heading to 0°.  \n" +
                            "```logo\nhome\n```"),

            // ── Motion queries ────────────────────────────────────────────────────

            Map.entry("pos",
                    "**pos**  \n" +
                            "Outputs the turtle's current position as a list `[ x y ]`.  \n" +
                            "```logo\nshow pos\n```"),

            Map.entry("xcor",
                    "**xcor**  \n" +
                            "Outputs the turtle's current X coordinate.  \n" +
                            "```logo\nshow xcor\n```"),

            Map.entry("ycor",
                    "**ycor**  \n" +
                            "Outputs the turtle's current Y coordinate.  \n" +
                            "```logo\nshow ycor\n```"),

            Map.entry("heading",
                    "**heading**  \n" +
                            "Outputs the turtle's current heading in degrees (0 = up, 90 = right).  \n" +
                            "```logo\nshow heading\n```"),

            Map.entry("towards",
                    "**towards** *[ x y ]*  \n" +
                            "Outputs the heading the turtle would need to face to point at *[ x y ]*.  \n" +
                            "```logo\nshow towards [100 0]\n```"),

            // ── Window control ────────────────────────────────────────────────────

            Map.entry("wrap",
                    "**wrap**  \n" +
                            "Enables wrap mode: the turtle reappears on the opposite edge when it moves off-screen.  \n" +
                            "```logo\nwrap\nforward 800\n```"),

            Map.entry("window",
                    "**window**  \n" +
                            "Enables window mode: the turtle can move past screen edges without wrapping or stopping.  \n" +
                            "```logo\nwindow\nforward 600\n```"),

            Map.entry("fence",
                    "**fence**  \n" +
                            "Enables fence mode: the turtle stops at screen edges instead of wrapping.  \n" +
                            "```logo\nfence\nforward 600\n```"),

            Map.entry("shownp",
                    "**shownp** &nbsp;·&nbsp; alias: `shown?`  \n" +
                            "Outputs `true` if the turtle is visible, `false` if hidden.  \n" +
                            "```logo\nshow shownp\n```"),

            Map.entry("shown?",
                    "**shown?** &nbsp;·&nbsp; alias for `shownp`  \n" +
                            "Outputs `true` if the turtle is visible."),

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
                    "**setpencolor** *color* &nbsp;·&nbsp; alias: `setcolor`  \n" +
                            "Sets the pen color. *color* is a number (0–15) or an `[r g b]` list.  \n" +
                            "```logo\nsetpencolor 4\nsetcolor [255 0 0]\n```"),

            Map.entry("setcolor",
                    "**setcolor** *color* &nbsp;·&nbsp; alias for `setpencolor`  \n" +
                            "Sets the pen color. *color* is a number (0–15) or an `[r g b]` list."),

            Map.entry("setpensize",
                    "**setpensize** *size* &nbsp;·&nbsp; alias: `setwidth`  \n" +
                            "Sets the pen width in pixels.  \n" +
                            "```logo\nsetpensize 3\nsetwidth 5\n```"),

            Map.entry("setwidth",
                    "**setwidth** *size* &nbsp;·&nbsp; alias for `setpensize`  \n" +
                            "Sets the pen width in pixels."),

            Map.entry("pencolor",
                    "**pencolor** &nbsp;·&nbsp; alias: `pc`  \n" +
                            "Outputs the current pen color.  \n" +
                            "```logo\nshow pencolor\n```"),

            Map.entry("pendownp",
                    "**pendownp** &nbsp;·&nbsp; alias: `pendown?`  \n" +
                            "Outputs `true` if the pen is down, `false` if up.  \n" +
                            "```logo\nshow pendownp\n```"),

            Map.entry("pendown?",
                    "**pendown?** &nbsp;·&nbsp; alias for `pendownp`  \n" +
                            "Outputs `true` if the pen is down."),

            Map.entry("pensize",
                    "**pensize**  \n" +
                            "Outputs the current pen width in pixels.  \n" +
                            "```logo\nshow pensize\n```"),

            Map.entry("filled",
                    "**filled** *color* *[ body ]*  \n" +
                            "Executes *body*, tracing the turtle path, then fills the enclosed region with *color*.  \n" +
                            "```logo\nfilled \"blue [ repeat 4 [ fd 100 rt 90 ] ]\n```"),

            Map.entry("setlabelheight",
                    "**setlabelheight** *pixels*  \n" +
                            "Sets the font size used by `label`, in pixels.  \n" +
                            "```logo\nsetlabelheight 24\nlabel \"Hello\n```"),

            Map.entry("labelsize",
                    "**labelsize**  \n" +
                            "Outputs the current label font size in pixels.  \n" +
                            "```logo\nshow labelsize\n```"),

            Map.entry("changeshape",
                    "**changeshape** *shape* &nbsp;·&nbsp; alias: `csh`  \n" +
                            "Changes the turtle's appearance. *shape* is a number (0–7) or name: 0=turtle, 1=cat, 2=fish, 3=dog, 4=horse, 5=tiger, 6=crab, 7=snail.  \n" +
                            "```logo\nchangeshape 1   ; cat\ncsh \"dog\n```"),

            Map.entry("csh",
                    "**csh** *shape* &nbsp;·&nbsp; alias for `changeshape`  \n" +
                            "Changes the turtle's appearance."),

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

            Map.entry("localmake",
                    "**localmake** *\"name* *value*  \n" +
                            "Declares and assigns a local variable in one step. Shortcut for `local` + `make`.  \n" +
                            "```logo\nlocalmake \"temp 0\n```"),

            Map.entry("name",
                    "**name** *value* *\"varname*  \n" +
                            "Like `make` but with arguments reversed: assigns *value* to variable *varname*.  \n" +
                            "```logo\nname 42 \"answer\n```"),

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

            Map.entry("if",
                    "**if** *condition* *[ body ]*  \n" +
                            "Executes *body* only if *condition* is `true`.  \n" +
                            "```logo\nif :x > 0 [ forward :x ]\n```"),

            Map.entry("ifelse",
                    "**ifelse** *condition* *[ thenBody ]* *[ elseBody ]*  \n" +
                            "Executes *thenBody* if *condition* is true, otherwise executes *elseBody*.  \n" +
                            "```logo\nifelse :x > 0 [ forward :x ] [ back :x ]\n```"),

            Map.entry("test",
                    "**test** *condition*  \n" +
                            "Evaluates *condition* and stores the result. Use `iftrue`/`iffalse` to act on it.  \n" +
                            "```logo\ntest :x > 0\niftrue [ forward :x ]\niffalse [ back :x ]\n```"),

            Map.entry("iftrue",
                    "**iftrue** *[ body ]* &nbsp;·&nbsp; alias: `ift`  \n" +
                            "Executes *body* if the result of the last `test` was `true`.  \n" +
                            "```logo\ntest :x > 0\niftrue [ forward :x ]\n```"),

            Map.entry("ift",
                    "**ift** *[ body ]* &nbsp;·&nbsp; alias for `iftrue`  \n" +
                            "Executes *body* if the result of the last `test` was `true`."),

            Map.entry("iffalse",
                    "**iffalse** *[ body ]* &nbsp;·&nbsp; alias: `iff`  \n" +
                            "Executes *body* if the result of the last `test` was `false`.  \n" +
                            "```logo\ntest :x > 0\niffalse [ back :x ]\n```"),

            Map.entry("iff",
                    "**iff** *[ body ]* &nbsp;·&nbsp; alias for `iffalse`  \n" +
                            "Executes *body* if the result of the last `test` was `false`."),

            Map.entry("until",
                    "**until** *condition* *[ body ]*  \n" +
                            "Executes *body* repeatedly until *condition* becomes true (opposite of `while`).  \n" +
                            "```logo\nuntil [:x = 0] [ make \"x :x - 1 ]\n```"),

            Map.entry("for",
                    "**for** *[ var start end ]* *[ body ]*  \n" +
                            "Loops with *var* running from *start* to *end* (inclusive), stepping by 1 (or optional step).  \n" +
                            "```logo\nfor [i 1 10] [ print :i ]\nfor [i 0 100 5] [ forward :i ]\n```"),

            Map.entry("while",
                    "**while** *condition* *[ body ]*  \n" +
                            "Executes *body* repeatedly as long as *condition* is true.  \n" +
                            "```logo\nwhile [:x > 0] [ make \"x :x - 1 ]\n```"),

            Map.entry("dotimes",
                    "**dotimes** *[ var count ]* *[ body ]*  \n" +
                            "Runs *body* *count* times; *var* holds the current iteration number (1-based).  \n" +
                            "```logo\ndotimes [i 5] [ show :i ]\n```"),

            Map.entry("do.while",
                    "**do.while** *[ body ]* *condition*  \n" +
                            "Runs *body* at least once, then repeats while *condition* is true.  \n" +
                            "```logo\ndo.while [ make \"a random 10  show :a ] :a < 8\n```"),

            Map.entry("do.until",
                    "**do.until** *[ body ]* *condition*  \n" +
                            "Runs *body* at least once, then repeats until *condition* is true.  \n" +
                            "```logo\ndo.until [ make \"a random 10  show :a ] :a >= 8\n```"),

            Map.entry("wait",
                    "**wait** *ticks*  \n" +
                            "Pauses execution for *ticks* / 60 seconds.  \n" +
                            "```logo\nrepeat 4 [ forward 50  wait 30 ]\n```"),

            Map.entry("bye",
                    "**bye**  \n" +
                            "Terminates the program.  \n" +
                            "```logo\nbye\n```"),

            Map.entry("repcount",
                    "**repcount**  \n" +
                            "Outputs the current iteration number inside the innermost `repeat` loop.  \n" +
                            "```logo\nrepeat 4 [ show repcount ]\n```"),

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

            Map.entry("define",
                    "**define** *\"name* *[ [ params ] [ body ] ]*  \n" +
                            "Defines a procedure from a list structure (alternative to `to...end`).  \n" +
                            "```logo\ndefine \"square [[:n] [repeat 4 [fd :n rt 90]]]\n```"),

            Map.entry("def",
                    "**def** *\"name*  \n" +
                            "Outputs the definition of the named procedure as a string.  \n" +
                            "```logo\nshow def \"square\n```"),

            Map.entry("to",
                    "**to** *name* *[:param ...]* *body* **end**  \n" +
                            "Defines a new procedure named *name* with optional parameters.  \n" +
                            "```logo\nto square :size\n    repeat 4 [ forward :size  right 90 ]\nend\n```"),

            Map.entry("end",
                    "**end**  \n" +
                            "Marks the end of a procedure definition started with `to`."),

            // ── I/O ──────────────────────────────────────────────────────────────

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

            Map.entry("readword",
                    "**readword**  \n" +
                            "Prompts the user for a line of input and outputs it as a single word (spaces included).  \n" +
                            "```logo\nmake \"name (readword [What is your name?])\nshow :name\n```"),

            Map.entry("readlist",
                    "**readlist**  \n" +
                            "Prompts the user for a line of input and outputs it as a list of words.  \n" +
                            "```logo\nmake \"colors (readlist [Type some colors:])\nshow :colors\n```"),

            // ── Arithmetic ────────────────────────────────────────────────────────

            Map.entry("sum",
                    "**sum** *a* *b*  \n" +
                            "Returns *a* + *b*. Equivalent to `a + b`.  \n" +
                            "```logo\nprint sum 3 4   ; prints 7\n```"),

            Map.entry("minus",
                    "**minus** *a* *b*  \n" +
                            "Returns *a* - *b*. Equivalent to `a - b`.  \n" +
                            "```logo\nprint minus 8 2   ; prints 6\n```"),

            Map.entry("power",
                    "**power** *base* *exp*  \n" +
                            "Returns *base* raised to the power *exp*.  \n" +
                            "```logo\nprint power 2 8   ; prints 256\n```"),

            Map.entry("random",
                    "**random** *n*  \n" +
                            "Returns a random integer from 0 to *n*−1.  \n" +
                            "```logo\nprint random 10   ; prints 0..9\n```"),

            // ── Comparison ────────────────────────────────────────────────────────

            Map.entry("equalp",
                    "**equalp** *a* *b* &nbsp;·&nbsp; alias: `equal?`  \n" +
                            "Returns `true` if *a* equals *b*, otherwise `false`.  \n" +
                            "```logo\nif equalp :x 0 [ print \"zero ]\n```"),

            Map.entry("notequalp",
                    "**notequalp** *a* *b* &nbsp;·&nbsp; alias: `notequal?`  \n" +
                            "Returns `true` if *a* does not equal *b*.  \n" +
                            "```logo\nnotequalp \"no \"yes\n```"),

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

            Map.entry("item",
                    "**item** *index* *list*  \n" +
                            "Returns the element at position *index* (1-based) in *list*.  \n" +
                            "```logo\nprint item 2 [10 20 30]   ; prints 20\n```"),

            Map.entry("pick",
                    "**pick** *list*  \n" +
                            "Returns a randomly chosen element from *list*.  \n" +
                            "```logo\nshow pick [1 2 3 4 5]\n```"),

            // ── Predicates ────────────────────────────────────────────────────────

            Map.entry("wordp",
                    "**wordp** *thing* &nbsp;·&nbsp; alias: `word?`  \n" +
                            "Outputs `true` if *thing* is a word (string), `false` otherwise.  \n" +
                            "```logo\nshow wordp \"hello   ; true\n```"),

            Map.entry("word?",
                    "**word?** *thing* &nbsp;·&nbsp; alias for `wordp`  \n" +
                            "Outputs `true` if *thing* is a word."),

            Map.entry("listp",
                    "**listp** *thing* &nbsp;·&nbsp; alias: `list?`  \n" +
                            "Outputs `true` if *thing* is a list.  \n" +
                            "```logo\nshow listp [1 2 3]   ; true\n```"),

            Map.entry("list?",
                    "**list?** *thing* &nbsp;·&nbsp; alias for `listp`  \n" +
                            "Outputs `true` if *thing* is a list."),

            Map.entry("numberp",
                    "**numberp** *thing* &nbsp;·&nbsp; alias: `number?`  \n" +
                            "Outputs `true` if *thing* is a number.  \n" +
                            "```logo\nshow numberp 42   ; true\n```"),

            Map.entry("number?",
                    "**number?** *thing* &nbsp;·&nbsp; alias for `numberp`  \n" +
                            "Outputs `true` if *thing* is a number."),

            Map.entry("emptyp",
                    "**emptyp** *thing* &nbsp;·&nbsp; alias: `empty?`  \n" +
                            "Outputs `true` if *thing* is an empty list or empty string.  \n" +
                            "```logo\nshow emptyp []   ; true\n```"),

            Map.entry("empty?",
                    "**empty?** *thing* &nbsp;·&nbsp; alias for `emptyp`  \n" +
                            "Outputs `true` if *thing* is empty."),

            Map.entry("beforep",
                    "**beforep** *word1* *word2* &nbsp;·&nbsp; alias: `before?`  \n" +
                            "Outputs `true` if *word1* comes before *word2* in alphabetical order.  \n" +
                            "```logo\nshow beforep \"apple \"banana   ; true\n```"),

            Map.entry("before?",
                    "**before?** *word1* *word2* &nbsp;·&nbsp; alias for `beforep`  \n" +
                            "Outputs `true` if *word1* is alphabetically before *word2*."),

            Map.entry("array",
                    "**array** *n*  \n" +
                            "Creates and outputs an array of *n* elements, indexed from 1.  \n" +
                            "```logo\nmake \"a array 5\nprint arrayp :a   ; true\n```"),

            Map.entry("substringp",
                    "**substringp** *part* *whole* &nbsp;·&nbsp; alias: `substring?`  \n" +
                            "Outputs `true` if *part* is a substring of *whole*.  \n" +
                            "```logo\nshow substringp \"hello \"helloworld   ; true\n```"),

            Map.entry("substring?",
                    "**substring?** *part* *whole* &nbsp;·&nbsp; alias for `substringp`  \n" +
                            "Outputs `true` if *part* is contained within *whole*.")
    );

    /**
     * Returns the markdown hover documentation for the given word (case-insensitive),
     * or null if no documentation is available.
     */
    public static String get(final String word) {
        return DOCS.get(word.toLowerCase());
    }

}
