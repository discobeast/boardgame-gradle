/**
 * =====================================================================
 * Programming Project for NCEA Level 2, Standard 91896
 * ---------------------------------------------------------------------
 * Project Name:   Chain reaction
 * Project Author: Jesse vanwyk
 * GitHub Repo:    GITHUB REPO URL HERE
 * ---------------------------------------------------------------------
 * Notes:
 * Includes AI opponent
 * Uses arrow keys for control
 * =====================================================================
 */

/**
 * Description
 * Arguments:
 * Returns:
 */
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.lang.Thread.sleep
import java.util.*

private var bluepoints = 0
private var redpoints = 0

//Runtime settings
private const val POINTS_TO_WIN = 10
private const val BOT_TEAM = 1

/**
 * Switch two given indexes in a list
 * Arguments: Index (Int), Index2(Int) - Indexes in the list to swap
 * Returns: N/A
 */
fun <E> MutableList<E>.switch(index: Int, index2: Int) {
    if (index2 == -1 || index2 >= this.size) return //Check if indexes are valid
    Collections.swap(this, index, index2)
}

/**
 * Finds chains of directly adjacent values
 * Arguments:
 *  -Size (Int) - Minimum size of list to return
 *  -Ignored values (List<E>) - List of values to skip over when checking
 * Returns: Chains<MutableMap<Start index (Int),Size (Int)>
 */
fun <E> MutableList<E>.findAdjacent(size: Int = 2, ignoredValues: List<E> = listOf()): MutableMap<Int, Int> {
    val foundChains = mutableMapOf<Int, Int>()
    this.forEachIndexed { index, value ->
        if (value in ignoredValues) return@forEachIndexed //Skip checks if the current index is already in a chain or is an ignored value
        foundChains.forEach { (chainStartIndex, chainSize) ->
            if (index in (chainStartIndex..<chainSize + chainStartIndex) || value in ignoredValues) {
                return@forEachIndexed
            }
        }
        var num = 1
        var matches = 1 //Matches start at 1 so a chain of 2 is 1+1=2 instead of 0+1=1
        while (num > 0) {
            num--
            val shiftedIndex = index + matches - 1 //Because matches start at one we remove one from matches here to start the index offset at 0
            if (shiftedIndex != this.size - 1 && this[shiftedIndex + 1] == this[shiftedIndex]) {
                num++
                matches++
            }
        }
        if (matches >= size) foundChains[index] = matches
    }
    return foundChains
}

/**
 * Finds 3 value patterns in a list
 * Arguments:
 *  -Pattern (List<Any?>) - A list of 3 values to act as the desired pattern to find with capability for lists of values and wildcards
 *      e.g. list(List(1,2),3,4) being the same as pattern (1 or 2),3,4
 *      e.g. List(Nan,3,4) being the same as pattern (Anything),3,4
 *      e.g. List(1,3,4) being the same as pattern 1,3,4
 *  -Offset (Int) - return offset for found pattern index
 * Returns: List of indexes offset by specified offset (Default to center of pattern)
 */
fun <E> MutableList<E>.findPattern(pattern: List<Any?>, offset: Int = 0): List<Int> {
    val matches = mutableListOf<Int>()
    this.forEachIndexed { index, _ ->
        if (index > 0 && index < this.size - 1) { //Because the pattern is 3 long we have to make sure we are at least +-1 index off the start and end indexes
            val slice = this.slice(index - 1..index + 1) as List<*> //We split the list into groups of 3's to compare with
            val isMatch = pattern.zip(slice).all { (p, s) ->  //We then splice the slice and the pattern together so pattern [A,B,C] and slice [D,E,F] become [A,D],[B,E],[C,F] so we can compare easier
                when (p) {
                    null -> true           // wildcard, matches anything
                    is List<*> -> p.contains(s)  // multi-match,s must be in the list
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

/**
 * Waits for a valid keyboard input from the user
 * Arguments: reader (NonBlockingReader) - initalized terminal keypress reader
 * Returns: 1-4 depending on the arrow key selected, -1 if error occurs
 */
fun waitForKey(reader: NonBlockingReader): Int {
    var char: Int = -1
    var numVals = 1
    var cursorKey = false

    while (numVals > 0) {
        val charCode = reader.read() //This pauses execution until we get a keypress from the user
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
            65 -> 1 //Up arrow
            66 -> 2 //Down arrow
            67 -> 3 //Right arrow
            68 -> 4 //Left arrow
            else -> -1
        }
    } else {
        char
    }
}

/**
 * Checks every index on the board to remove invalid counters as well as handles the scoring and removing of created chains
 * Arguments: Board (MutableList<Int>) - The board array
 * Returns: N/A
 */
private fun checkBoard(board: MutableList<Int>) {
    board.forEachIndexed { index, value -> //Just loops through every index (Apart from the start and end indexes) and checks that they are not surrounded
        if (index == 0 || index == board.size - 1 || value == -1) { //Skip check if value is -1 or at the beginning/end of the list
            return@forEachIndexed
        }
        val opponentValue =
            (board[index] + 1) % 2 //Calculate opponent based on value and then check if the index is neighbored on both sides by the value
        if (board[index - 1] == opponentValue && board[index + 1] == opponentValue) {
            board[index] = -1 //Index is neighbored on both sides by opponents so remove counter
        }
    }
    board.findAdjacent(3, listOf(-1)).forEach { (chain, size) -> //Go through chains and score points
        when (board[chain]) { //the value at the beginning index of the chain is the value of the team it belongs to
            1 -> bluepoints += size
            0 -> redpoints += size
        }
        for (index in 0 until size) { //Remove chain when we are finished with it
            board[index + chain] = -1
        }
    }

}

/**
 * Board display function
 * Arguments:
 *  -Board (MutableList<Int>) - The board array
 *  -CursorPos (Int) - Location of the cursor on the board array
 * Returns: N/A
 */
private fun displayBoard(board: MutableList<Int>, cursorPos: Int) {
    println("┌────┐".repeat(board.size))
    board.forEachIndexed { index, value ->
        var displayedCharacter = "  "
        when (value) {
            1 -> displayedCharacter = "##".blue()
            0 -> displayedCharacter = "##".red()
        }
        if (index == cursorPos) {
            displayedCharacter = displayedCharacter.bgGrey()
        }
        print("│ $displayedCharacter │")


    }
    println()
    println("└────┘".repeat(board.size))
}

/**
 * Main user interface of the game
 * Arguments:
 *  -Board (MutableList<Int>) - The board array
 *  -CursorPos (Int) - Location of the cursor on the board array
 * Returns: N/A
 */
private fun userInterface(board: MutableList<Int>, cursorPos: Int, player: Int) {
    print("\u001b[H\u001b[2J")
    checkBoard(board)
    displayBoard(board, cursorPos)
    println("Blue: $bluepoints | Red: $redpoints")
    println("${if (player == 0) "Red" else "Blue"}'s turn.")
}

/**
 * Basic function to display ruleset for the game
 * Arguments: Reader (NonBlockingReader) - Terminal input
 * Returns: N/A
 */
private fun displayRules(){
    println("""
        Chain reaction is a 2 player game.
        The players will take alternating turns placing counters on the board.
        If a players counter is directly adjacent to their opponents counters on both sides it is removed from the board.
        Likewise you cannot place a counter between 2 opponent counters.
        The goal of the game is to make unbroken chains of 3+ counters.
        These chains will "explode" and you will gain points depending on the length of the chain.
        A player cannot skip their turn.
        In the event that the current player cannot make a valid move, the player with the most points wins or stalemate is called if both players are equal.
        First player to 10 points wins.
        
        Press enter when you are ready to continue.
    """.trimIndent())
    readlnOrNull() //Pauses execution till the user presses enter
}

/**
 * Utility function to act as a Y/N choice
 * Arguments: N/A
 * Returns: True or False depending on whether user selected Y or N
 */
private fun yesOrNo() : Boolean{
    var userInput: String? = null
    while (userInput == null) {
        print(": ")
        userInput = readlnOrNull()?.lowercase()?.trim()
        if (userInput.isNullOrEmpty() || !listOf("y","n").contains(userInput)) {
            userInput = null
        }
    }
    return userInput == "y"
}

/**
 * Title card of the game
 * Arguments: N/A
 * Returns: N/A
 */
private fun titleCard() {
    print("\u001b[H\u001b[2J")
    print("Welcome to")
    sleep(200)
    print(".")
    sleep(200)
    print(".")
    sleep(200)
    println(".")
    sleep(200)
    println("_________ .__           .__                                       __  .__               ")
    sleep(50)
    println("\\_   ___ \\|  |__ _____  |__| ____   _______   ____ _____    _____/  |_|__| ____   ____  ")
    sleep(50)
    println("/    \\  \\/|  |  \\\\__  \\ |  |/    \\  \\_  __ \\_/ __ \\\\__  \\ _/ ___\\   __\\  |/  _ \\ /    \\ ")
    sleep(50)
    println("\\     \\___|   Y  \\/ __ \\|  |   |  \\  |  | \\/\\  ___/ / __ \\\\  \\___|  | |  (  <_> )   |  \\")
    sleep(50)
    println(" \\______  /___|  (____  /__|___|  /  |__|    \\___  >____  /\\___  >__| |__|\\____/|___|  /")
    sleep(50)
    println("        \\/     \\/     \\/        \\/               \\/     \\/     \\/                    \\/ ")
    sleep(1000)
}

/**
 * Utility function to check if an index on the board is a valid position to place a counter
 * Arguments:
 *  -Board (MutableList<Int>) - The board array
 *  -Index (Int) - Board index to evaluate
 *  -Opponent (Int) - The opponent of the player you are trying to check the index for
 * Returns: Boolean (The specified index is a valid position to place a counter True/False)
 */
private fun checkValidPosition(index: Int, board: MutableList<Int>, opponent: Int): Boolean {
    // Revised: First check if point is either (free on either side and valid spot) or (0,board.size - 1) then check if value is not -1
    // (cursorPos == 0 || cursorPos == board.size - 1)
    // (cursorPos > 0 && cursorPos < board.size-1)
    // (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)
    // (board[cursorPos] == -1)
    // (cursorPos > 0 && cursorPos < board.size-1) && (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent)
    // (((cursorPos == 0 || cursorPos == board.size - 1)||((cursorPos > 0 && cursorPos < board.size-1) && (board[cursorPos - 1] != opponent || board[cursorPos + 1] != opponent))) && board[cursorPos] == -1)

    //If statement that checks if the given index is either boundary or not surrounded by opponents and if the index is not already occupied
    //In hindsight making it a single if statement was not a great idea. However, im too afraid to change it (Not even I know how it works)
    return (((index == 0 || index == board.size - 1) || ((index > 0 && index < board.size - 1) && (board[index - 1] != opponent || board[index + 1] != opponent))) && board[index] == -1)
}

/**
 * Function that contains all the logic for the AI opponent
 * Arguments: Board(MutableList<Int>) - The board array
 * Returns: An index on the board or -1 if there is no valid position
 */
private fun bestNextMove(board: MutableList<Int>): Int {
    val opponent = (BOT_TEAM + 1) % 2
    val possiblePoints = mutableListOf<Int>()
    val possiblePointsWeighted = mutableMapOf<Int, Int>()
    //with 1 as bot counter and 0 as opponent counter
    /*In order of priority:
        -Remove counter patterns: -1 0 1, 1 0 -1
        -Score point patterns: 1 1 -1, 1 -1 1, -1 1 1
        -Block chain patterns: 0 0 -1, -1 0 0
        -Continue chain patterns: 0 -1 -1, -1 -1 0
        -Find a safe spot patterns: -1 -1 -1
        -Make a random move
     */

//    println("Looking for somewhere to remove an opponents counter")
    board.findPattern(listOf(-1, opponent, BOT_TEAM), -1).forEach {
        //Makes sure the bot won't try to place inside an area bordered by the opponent
        if (it <= 0 || board[it - 1] != opponent) {
            possiblePoints.add(it)
        }
    }
    board.findPattern(listOf(BOT_TEAM, opponent, -1), 1).forEach {
        //Makes sure the bot won't try to place inside an area bordered by the opponent
        if (it >= board.size - 1 || board[it + 1] != opponent) {
            possiblePoints.add(it)
        }
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }

//    println("Looking for somewhere to score")
    board.findPattern(listOf(BOT_TEAM, BOT_TEAM, -1), 1).forEach {
        possiblePoints.add(it)
    }
    board.findPattern(listOf(BOT_TEAM, -1, BOT_TEAM)).forEach {
        possiblePoints.add(it)
    }
    board.findPattern(listOf(-1, BOT_TEAM, BOT_TEAM), -1).forEach {
        possiblePoints.add(it)
    }
    possiblePoints.forEachIndexed { _, i -> //Checks each possible move and gives it a weight depending on how many points it would score
        val boardCopy = board.toMutableList()
        boardCopy[i] = BOT_TEAM
        boardCopy.findAdjacent(ignoredValues = listOf(-1, opponent)).forEach { (key, value) -> //If the point is inside a chain, assign weight based on size
            if (i in (key..key + value)) {
                possiblePointsWeighted[i] = value
            }
        }
    }
    if (possiblePointsWeighted.isNotEmpty()) {
        return possiblePointsWeighted.maxBy { it.value }.key
    }

//    println("Looking for somewhere to block an opponent chain")
    board.findPattern(listOf(-1, opponent, opponent), -1).forEach {
        if (it <= 0 || board[it - 1] != opponent) {
            possiblePoints.add(it)
        }
    }
    board.findPattern(listOf(opponent, opponent, -1), 1).forEach {
        if (it >= board.size - 1 || board[it + 1] != opponent) {
            possiblePoints.add(it)
        }
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }

//    println("Looking for somewhere to continue a chain")
    board.findPattern(listOf(BOT_TEAM, -1, -1), 1).forEach {
        possiblePoints.add(it)
    }
    board.findPattern(listOf(-1, -1, BOT_TEAM), -1).forEach {
        possiblePoints.add(it)
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }

//    println("Looking for a safe spot")
    board.findPattern(listOf(-1, -1, -1)).forEach {
        possiblePoints.add(it)
    }
    if (possiblePoints.isNotEmpty()) {
        return possiblePoints.random()
    }

//    println("Attempting to pick a random location")
    val valid = mutableListOf<Int>()
    (0..<board.size).forEach {
        if (checkValidPosition(it, board, opponent)) valid.add(it)
    }
    if (valid.isNotEmpty()) {
        return valid.random()
    }
    return -1 //forfeit
}

/**
 * Main loop
 * Arguments:N/A
 * Returns:N/A
 */
fun main() {
    val board = MutableList(12) { -1 }
    val sel = MutableList(12) { " " }
    sel[0] = "^"
    var player = (0..1).random()
    var opponent = (player + 1) % 2
    var cursorPos = 0
    titleCard()
    println("Have you played Chain reaction before? (Y/N)")
    if (!yesOrNo()) displayRules()
    println("Would you like to play against a bot? (Y/N)")
    val botEnabled = yesOrNo()
    val terminal = TerminalBuilder.builder().build() //These are initialized afterward because these remove the ability to type in terminal
    terminal.enterRawMode() // Disables line buffering
    val reader = terminal.reader()
    userInterface(board, cursorPos, player)
    while (true) {
        if (bluepoints >= POINTS_TO_WIN || redpoints >= POINTS_TO_WIN) {
            break
        }
        var valid = 0
        (0..<board.size).forEach {
            if (checkValidPosition(it, board, opponent)) valid++
        }
        if (valid <= 0) break

        // if BOT is true then check if it's the bots turn else always return true
        if (botEnabled && player == BOT_TEAM) {
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
        userInterface(board, cursorPos, player)
    }
    if (bluepoints > redpoints) {
        println("Blue won")
    } else if (redpoints > bluepoints) {
        println("Red won")
    } else println("Stalemate")
}
