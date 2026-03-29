import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*

private var bluepoints = 0
private var redpoints = 0
private const val POINTS_TO_WIN = 10
private const val BOT_TEAM = 1
private const val BOT = false

fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    if (index2 == -1 || index2 >= this.size) return
    Collections.swap(this, index, index2)
}

fun <E> MutableList<E>.findAdjacent(size: Int = 2, skip: List<E> = listOf()): MutableMap<Int, Int> {
    val chains = mutableMapOf<Int, Int>()
    this.forEachIndexed { index, value ->
//        println("running index $index")
        if (value in skip) return@forEachIndexed
        chains.forEach { (key, num) ->
//            println("Checking if $index is in range ${(key..<num + key)}")
            if (index in (key..<num + key) || value in skip) {
//                println("Index $index is part of chain $key")
                return@forEachIndexed
            }
        }
//        println("Index $index is not part of a chain")
        var num = 1
        var matches = 1
        while (num > 0) {
            num--
//            println("Checking ${index + matches}")
            if (index + matches - 1 != this.size - 1 && this[index + matches] == this[index + matches - 1]) {
                num++
                matches++
//                println("Found match ${index + num} Chain length: $matches")
            } //else println("No more matches $matches")
        }
//        println("appending chain")
        if (matches >= size) chains[index] = matches
    }
    return chains
}

fun waitForKey(reader: NonBlockingReader): Int {
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

private fun checkBoard(board: MutableList<Int>) {
    board.forEachIndexed { index, value ->
        if (index == 0 || index == board.size - 1 || value == -1) {
            return@forEachIndexed
        }
        val opponentValue = (board[index] + 1) % 2
        if (board[index - 1] == opponentValue && board[index + 1] == opponentValue) {
            board[index] = -1
        }
    }
    board.findAdjacent(3, listOf(-1)).forEach { (chain, size) ->
        when (board[chain]) {
            1 -> bluepoints += size
            0 -> redpoints += size
        }
        for (index in 0 until size) {
            board[index + chain] = -1
        }
    }

}

private fun displayBoard(board: MutableList<Int>, cursorPos: Int) {
    print("[")
    board.forEachIndexed { index, value ->
        var displayed = "#"
        when (value) {
            0 -> displayed = displayed.red()
            1 -> displayed = displayed.blue()
        }
        if (index == cursorPos)
            displayed = displayed.bgGrey()
        if (index == board.size - 1) {
            print(displayed)
        } else {
            print("$displayed, ")
        }
    }
    println("]")
}

private fun checkValidPosition(cursorPos: Int, board: MutableList<Int>, opponent: Int) =
    ((cursorPos == 0 || cursorPos == board.size - 1) || (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)) && (board[cursorPos] == -1)

private fun bestNextMove(board: MutableList<Int>, team: Int): Int {
    val opponent = (team + 1) % 2
    //Find positions of opponent pieces:
    val opponentPositions = mutableListOf<Int>()
    board.forEachIndexed { index, value ->
        if (value == opponent)
            opponentPositions.add(index)
    }
    //Operations
    //Find a spot to score points
    //Find spot to remove enemy counters
    //Find a spot to block an enemy chain
    //Find a spot to create a chain precursor (Preferably with center open so enemy cannot block it
    //Find a spot to place a counter that is safe from enemy (1 free space on either side)
    //Place counter randomly
    //Forfit


    return TODO()
}

fun main() {
    val terminal = TerminalBuilder.builder().build()
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()
    val board = MutableList(12) { -1 }
    val sel = MutableList(12) { " " }
    sel[0] = "^"
    var player = (0..1).random()
    var opponent = (player + 1) % 2
    var cursorPos = 0
    while (true) {
        if (bluepoints >= POINTS_TO_WIN || redpoints >= POINTS_TO_WIN) {
            break
        }
        when (waitForKey(reader)) {
            1 -> {
                //( (cursorPos == 0 || cursorPos == board.size - 1) || (board[cursorPos-1] != opponent || board[cursorPos+1] != opponent) ) && (board[cursorPos] == -1)
                if (checkValidPosition(cursorPos, board, opponent)) {
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
        println("${if (player == 0) "Red" else "Blue"}'s turn.")
//        println(board.findAdjacent(3, listOf(-1)))
        checkBoard(board)
        displayBoard(board, cursorPos)
        println(sel)
        println("Blue: $bluepoints | Red: $redpoints")
    }
    if (bluepoints >= POINTS_TO_WIN) {
        println("Blue won")
    } else {
        println("Red won")
    }
}




