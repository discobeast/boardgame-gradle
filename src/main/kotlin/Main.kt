import org.jline.terminal.TerminalBuilder

fun main() {
    val terminal = TerminalBuilder.builder().build()
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()
    println("Press any key (Press 'q' to quit)...")
    val charbuffer = MutableList(3) { 0 }
    while (true) {
        val charCode = reader.read()
        charbuffer.add(0, charCode)
        if (charbuffer.size > 3) charbuffer.removeLast()
        var char = charCode.toChar()
        if (charCode == 27) {
            char = '^'
        }
        if (charbuffer == listOf(68, 79, 27))
            println("Left")
        println(charbuffer)
        println("Key detected: $char (Code: $charCode)")

    }
}
