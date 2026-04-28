import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*

private var bluepoints = 0
private var redpoints = 0

//Runtime settings
private const val POINTS_TO_WIN = 10
private const val BOT_TEAM = 1
private const val BOT = true

//Switches the 2 selected indexes of the list it is called upon
fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    if (index2 == -1 || index2 >= this.size) return
    Collections.swap(this, index, index2)
}

/*
    Returns the start indexes of unbroken chains of values in the list as well as the size of the chain
    Inputs: Size=Minimum size of chain to return, skip= List of values to ignore when checking chains,
 */

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

/*
    Returns the center index of a pattern of 3 values with a configurable offset
    Inputs:
    pattern list<Int/Null/List<Int>> List size of 3 values to be checked as patterns in an input list
        Null = match with any value, List<Int> List of values to match against
    offset= index offset
 */
fun <E> MutableList<E>.findPattern(pattern: List<Any?>, offset: Int = 0): List<Int> {
    val matches = mutableListOf<Int>()
    this.forEachIndexed { index, _ ->

        if (index > 0 && index < this.size - 1) {
            val slice = this.slice(index - 1..index + 1) as List<*>
            val isMatch = pattern.zip(slice).all { (p, s) ->
                when (p) {
                    null -> true           // wildcard, matches anything
                    is List<*> -> p.contains(s)  // multi-match, s must be in the list
                    else -> p == s          // exact match
                }
            }
            if (isMatch) {
                matches.add(index + offset)
            }
        }
    }
    return matches
}

/*
Returns 1-4 depending on what arrow key has been pressed
 */
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

/*
Checks if every index in the board is in a valid position
Awards point according to existing chains
 */
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

/*
Basic display for the board
 */
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

/*
If statement that defines the legal position for a point
 */
private fun checkValidPosition(cursorPos: Int, board: MutableList<Int>, opponent: Int): Boolean {
    // Revised: First check if point is either (free on either side and valid spot) or (0,board.size - 1) then check if value is not -1
    // (cursorPos == 0 || cursorPos == board.size - 1)
    // (cursorPos > 0 && cursorPos < board.size-1)
    // (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)
    // (board[cursorPos] == -1)
    // (cursorPos > 0 && cursorPos < board.size-1) && (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)
    // (((cursorPos == 0 || cursorPos == board.size - 1)||((cursorPos > 0 && cursorPos < board.size-1) && (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent))) && board[cursorPos] == -1)

    return (((cursorPos == 0 || cursorPos == board.size - 1) || ((cursorPos > 0 && cursorPos < board.size - 1) && (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent))) && board[cursorPos] == -1)
}

/*
Ai opponent behavior tree
 */
private fun bestNextMove(board: MutableList<Int>): Int {
    val opponent = (BOT_TEAM + 1) % 2
    val possiblePoints = mutableListOf<Int>()
    val possiblePointsWeighted = mutableMapOf<Int, Int>()
    //with 1 as bot counter and 0 as opponent counter
    //Operations
    //Find a spot to score points
    //Go through the list and either find patterns like 1 -1 1 or -1 1 1 or 1 1 -1
    println("TRYING TO FIND SPOT TO SCORE")
    board.findPattern(listOf(-1, BOT_TEAM, BOT_TEAM), -1)
        .forEach { possiblePoints.add(it) } //Find patterns and append indexes to list
    board.findPattern(listOf(BOT_TEAM, -1, BOT_TEAM)).forEach { possiblePoints.add(it) }
    board.findPattern(listOf(BOT_TEAM, BOT_TEAM, -1), 1).forEach { possiblePoints.add(it) }

    possiblePoints.forEachIndexed { _, it ->
        val boardCopy = board.toMutableList() //Create temporary copy list
        boardCopy[it] = BOT_TEAM
        boardCopy.findAdjacent(3, listOf(-1, opponent)).forEach { (key, value) -> //Assign the weight to
            if (it in (key..key + value)) {
                possiblePointsWeighted[it] = value
                return@forEach
            }
        }
    }
    if (possiblePointsWeighted.isNotEmpty()) {
        return possiblePointsWeighted.maxByOrNull { it.value }!!.key
    }
    println("TRYING TO FIND SPOT TO BLOCK")
    //Find a spot to block an enemy chain
    //find patterns like -1 0 0 or 0 0 -1
    board.findPattern(listOf(opponent, opponent, -1)).forEach {
        if (it < board.size - 2 && board[it + 2] == opponent) return@forEach
        possiblePoints.add(it + 1)
    }
    board.findPattern(listOf(-1, opponent, opponent)).forEach {
        if (it > 1 && board[it - 2] == opponent) return@forEach
        possiblePoints.add(it - 1)
    }
    println("FOUND $possiblePoints")
    possiblePoints.forEachIndexed { _, i ->
        val boardCopy = board.toMutableList()
        boardCopy[i] = opponent
        boardCopy.findAdjacent(skip = listOf(-1, BOT_TEAM)).forEach { (key, value) ->
            if (i in (key..key + value)) {
                possiblePointsWeighted[i] = value
            }
        }
    }
    if (possiblePointsWeighted.isNotEmpty()) {
        return possiblePointsWeighted.maxBy { it.value }.key
    }
    println("TRYING TO FIND SPOT TO REMOVE OPPONENT")
    //Find spot to remove enemy counters
    // look for patterns like -1 0 1 or 1 0 -1
    board.findPattern(listOf(-1, opponent, BOT_TEAM)).forEach {
        if (it > 1 && board[it - 2] == opponent) return@forEach
        possiblePoints.add(it - 1)
    }
    board.findPattern(listOf(BOT_TEAM, opponent, -1)).forEach {
        if (it < board.size - 2 && board[it + 2] == opponent) return@forEach
        possiblePoints.add(it + 1)
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }
    println("TRYING TO FIND SPOT TO CONTINUE CHAIN")
    //Find a spot to create a chain precursor (Preferably with center open so enemy cannot block it
    // look for patterns like 1 -1 -1 or -1 1 -1 or -1 -1 1
    board.findPattern(listOf(BOT_TEAM, -1, null), 0).forEach {
        if (it - 1 <= 0) return@forEach
        if (board[it + 1] == -1) {
            possiblePointsWeighted[it + 1] = 1
        } else possiblePointsWeighted[it] = 0
    }
    board.findPattern(listOf(-1, BOT_TEAM, -1), 0).forEach {
        possiblePointsWeighted[it + listOf(-1, 1).random()] = 0
    }
    board.findPattern(listOf(null, -1, BOT_TEAM), 0).forEach {
        if (it + 1 >= board.size - 1) return@forEach
        if (board[it - 1] == -1) {
            possiblePointsWeighted[it - 1] = 1
        }
        possiblePointsWeighted[it] = 0
    }
    if (possiblePointsWeighted.isNotEmpty()) {
        return possiblePointsWeighted.maxBy { it.value }.key
    }
    println("TRYING TO FIND SAFE SPOT")
    //Find a spot to place a counter that is safe from enemy (1 free space on either side)
    // look for patterns like 0 -1 -1 or -1 -1 0 or -1 -1 -1
    board.findPattern(listOf(-1, -1, -1)).forEach {
        if (it - 1 <= 0) {
            possiblePoints.add(it - 1)
        } else if (it + 1 >= board.size - 1) {
            possiblePoints.add(it + 1)
        } else possiblePoints.add(it)
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }
    println("TRYING TO PICK RANDOM SPOT")
    val valid = mutableListOf<Int>()
    (0..<board.size).forEach {
        if (checkValidPosition(it, board, opponent)) valid.add(it)
    }
    if (valid.isNotEmpty()) {
        return valid.random()
    }
    return -1
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
        println("${if (player == 0) "Red" else "Blue"}'s turn.")
        var valid = 0
        (0..<board.size).forEach {
            if (checkValidPosition(it, board, opponent)) valid++
        }
        if (valid <= 0) break

        // if BOT is true then check if it's the bots turn else always return true
        if (BOT && player == BOT_TEAM) {
            val botMove = bestNextMove(board)
            if (botMove != -1) {
                if (!checkValidPosition(botMove, board, opponent)) {
                    println("Bot attempted an illegal move $botMove")
                } else board[botMove] = player
            } else {
                print("\u001b[H\u001b[2J")
                println("The bot has forfeited")
                break
            }
            player = opponent
            opponent = (player + 1) % 2
        } else {
            when (waitForKey(reader)) {
                1 -> {
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
        }
        print("\u001b[H\u001b[2J")
        checkBoard(board)
        displayBoard(board, cursorPos)
        println(sel)
        println("Blue: $bluepoints | Red: $redpoints")
    }
    if (bluepoints > redpoints) {
        println("Blue won")
    } else if (redpoints > bluepoints) {
        println("Red won")
    } else println("Stalemate")
}




