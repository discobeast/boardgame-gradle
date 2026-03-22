import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*

fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    Collections.swap(this, index, index2)
}

fun <E> MutableList<E>.shift(index: Int, index2: Int) {
    TODO("Shift target index value by X")
}

fun waitforkey(reader: NonBlockingReader): Int {
    var char: Int = -1
    var numVals = 1
    var cursorKey = false

    while (numVals > 0) {
        val charCode = reader.read()
        numVals--
        when (charCode) {
            27 -> { // Cursor keys are 3 bytes, first is 27 (ESC)
                numVals += 2
                cursorKey = true
            }
        }
        char = charCode
    }

    return if (cursorKey) {
        when (char) {
            65 -> 1
            66 -> 2
            67 -> 3
            68 -> 4
            else -> -1
        }
    } else {
        char
    }
}

fun main() {
    val terminal = TerminalBuilder.builder().build()
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()
    val board = MutableList(12) { "#" }
    board[0] = "&"
    while (true) {
        val keypress = waitforkey(reader)

        print(board)
    }
}
