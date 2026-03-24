import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*

fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    if (index2 == -1 || index2 >= this.size) return
    Collections.swap(this, index, index2)
}

fun <E> MutableList<E>.findAdjacent(size: Int = 1): MutableMap<Int, Int> {
    val chains = mutableMapOf<Int, Int>()
    this.forEachIndexed { index, _ ->
        println("running index $index")
        chains.forEach { (key, num) ->
            println("Checking if index $index is part of a chain")
            println("Checking if $index is in range ${(key..<num + key)}")
            if (index in (key..<num)) {
                println("Index $index is part of a chain $key")
                return@forEachIndexed
            }
            println("Index $index is not part of a chain")

        }
        var num = 1
        var matches = 1
        while (num > 0) {
            num--
            println("Checking ${index + matches}")
            if (index + matches - 1 != this.size - 1 && this[index + matches] == this[index + matches - 1]) {
                num++
                matches++
                println("Found match ${index + num} Chain length: $matches")
            } else println("No more matches $matches")
        }
        println("appending chain")
        if (matches > size) chains[index] = matches
    }
    return chains
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
    val board = MutableList(12) { (-1..1).random() }
    val sel = MutableList(12) { " " }
    sel[0] = "^"
    var valueSelected = false
    var index1 = -1
    var player = (0..1).random()
    var opponent = (player + 1) % 2
    var cursorPos = 0
    while (true) {
        val keypress = waitforkey(reader)
        when (keypress) {
            1 -> {
                //( (cursorPos == 0 || cursorPos == board.size - 1) || (board[cursorPos-1] != opponent || board[cursorPos+1] != opponent) ) && (board[cursorPos] == -1)
                if (((cursorPos == 0 || cursorPos == board.size - 1) || (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)) && (board[cursorPos] == -1)) {
                    board[cursorPos] = player
                    player = opponent
                    opponent = (player + 1) % 2
                }
            }

            3 -> sel.switch(cursorPos, cursorPos + 1)
            4 -> sel.switch(cursorPos, cursorPos - 1)
        }
        cursorPos = sel.indexOf("^")
        print("\u001b[H\u001b[2J")
        println(board.findAdjacent())
        board.forEachIndexed { index, value ->
            if (index == 0 || index == board.size - 1) {
                return@forEachIndexed
            }
            if (board[index - 1] == (board[index - 1] + 1) % 2 && board[index - 1] == (board[index - 1] + 1) % 2) {
                board[index] = -1
            }
        }
        print("[")
        board.forEachIndexed { index, value ->
            when (value) {
                -1 -> print("#, ")
                0 -> print("${"#".bgBlue()}, ")
                1 -> print("${"#".bgRed()}, ")
            }
        }
        println("]")
        println(opponent)
        println(player)
        println(sel)
        println(cursorPos)
    }
}
