import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.time.InstantSource.system
import java.util.*

fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    if (index2 == -1 || index2 >= this.size) return
    Collections.swap(this, index, index2)
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
        when (keypress) {
            3 -> board.switch(board.indexOf("&"),board.indexOf("&")+1)
            4 -> board.switch(board.indexOf("&"),board.indexOf("&")-1)
        }
        print("\u001b[H\u001b[2J")
        println(board)
    }
}
