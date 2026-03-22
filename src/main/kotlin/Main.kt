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
        when (charbuffer){
            listOf(68, 79, 27) -> println("LEFT")
            listOf(65, 79, 27) -> println("UP")
            listOf(66, 79, 27) -> println("DOWN")
            listOf(67, 79, 27) -> println("RIGHT")
        }
    }
}
