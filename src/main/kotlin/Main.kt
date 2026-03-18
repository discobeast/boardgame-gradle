import org.jline.terminal.TerminalBuilder

fun main() {
    val terminal = TerminalBuilder.builder().build()
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()
    println("Press any key (Press 'q' to quit)...")
    val charbuffer = MutableList(2) { 'a' }
    while (true) {
        val charCode = reader.read()
        val char = charCode.toChar()
        charbuffer.add(0, char)
        if (charbuffer.size > 2) charbuffer.removeLast()
        println("Key detected: $char (Code: $charCode)")
        println("Last two keys detected: $charbuffer")
    }
}
