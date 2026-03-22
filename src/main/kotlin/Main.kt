import org.jline.terminal.TerminalBuilder
fun waitforkey() {
    while (true) {
        val terminal = TerminalBuilder.builder().build()
        terminal.enterRawMode() // Disables line buffering
        val reader = terminal.reader()
        println("Press any key (Press 'q' to quit)...")
        val charbuffer = MutableList(3) { 0 }
        val charCode = reader.read()
        charbuffer.add(0, charCode)
        if (charbuffer.size > 3) charbuffer.removeLast()
        when (charbuffer){
            listOf(65, 79, 27) -> 1
            listOf(66, 79, 27) -> 2
            listOf(68, 79, 27) -> 3
            listOf(67, 79, 27) -> 4
        }
    }
}
fun main() {
    waitforkey()
}
