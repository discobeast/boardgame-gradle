import org.jline.terminal.TerminalBuilder

fun main() {
    val terminal = TerminalBuilder.builder().build()
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()

    println("Press any key (Press 'q' to quit)...")
    while (true) {
        val charCode = reader.read()
        val char = charCode.toChar()
        println("Key detected: $char (Code: $charCode)")
        if (char == 'q') break
    }
    terminal.close()
}
