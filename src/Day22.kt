import java.io.File
enum class InstructionType { MOVE, TURN }
enum class FACING { RIGHT, DOWN, LEFT, UP }

fun main() {
    data class Tile (
        var character: String?,
        val row: Int,
        val col: Int
    )

    data class Position (
        var direction: FACING,
        var row: Int,
        var col: Int
    )

    data class Instruction (
        val instructionType: InstructionType, //move, turn
        val instructionValue: String //a number or a direction
    )

    class Matrix {
        private var arrayMap: MutableList<MutableList<Tile>> = mutableListOf()
        private lateinit var currentPosition: Position

        fun printPassword() {
            println(
                ((1000 * (currentPosition.row + 1)) + (4 * (currentPosition.col + 1)) +
                        when (currentPosition.direction) {
                            FACING.RIGHT -> 0
                            FACING.DOWN -> 1
                            FACING.LEFT -> 2
                            FACING.UP -> 3
                        }
                ).toString()
            )
        }

        fun addRow(row: MutableList<Tile>) {
            arrayMap.add(row)
        }

        fun setStartPosition() {
            currentPosition = Position(FACING.RIGHT, 0 , arrayMap[0].indexOfFirst { s -> s.character == "." })
        }

        fun runInstruction(instr: Instruction) {
            when (instr.instructionType) {
                InstructionType.MOVE -> move(instr)
                InstructionType.TURN -> turn(instr)
            }
        }

        fun move(instr: Instruction) {
            var moves = instr.instructionValue.toInt()
            while (moves > 0) {
                var nextTile: Tile
                var col: MutableList<Tile>
                when (currentPosition.direction) {
                    FACING.RIGHT -> nextTile = if ((currentPosition.col + 1) == arrayMap[currentPosition.row].size || arrayMap[currentPosition.row][currentPosition.col + 1].character == " ") {
                        arrayMap[currentPosition.row].first { t -> t.character != " " }
                    } else {
                        arrayMap[currentPosition.row][currentPosition.col + 1]
                    }

                    FACING.DOWN -> {
                        col = getColumn(currentPosition.col)
                        nextTile = if ((currentPosition.row + 1) == col.size || arrayMap[currentPosition.row + 1][currentPosition.col].character == " ") {
                            col.first { t -> t.character != " " }
                        } else {
                            arrayMap[currentPosition.row + 1][currentPosition.col]
                        }
                    }

                    FACING.LEFT -> nextTile = if ((currentPosition.col - 1) == -1  || arrayMap[currentPosition.row][currentPosition.col - 1].character == " ") {
                        arrayMap[currentPosition.row].last { t -> t.character != " " }
                    } else {
                        arrayMap[currentPosition.row][currentPosition.col - 1]
                    }

                    FACING.UP -> {
                        col = getColumn(currentPosition.col)
                        nextTile = if ((currentPosition.row - 1) == -1 || arrayMap[currentPosition.row - 1][currentPosition.col].character == " ") {
                            col.last { t -> t.character != " " }
                        } else {
                            arrayMap[currentPosition.row - 1][currentPosition.col]
                        }
                    }
                }

                if (nextTile.character == ".") {
                    currentPosition.row = nextTile.row
                    currentPosition.col = nextTile.col
                } else {
                    break
                }

                moves--
            }
        }

        fun getColumn(index: Int): MutableList<Tile> {
            val col: MutableList<Tile> = mutableListOf()
            arrayMap.forEach { row ->
                if (index < row.size) {
                    col.add(row[index])
                }
            }

            return col
        }

        fun turn(instr: Instruction) {
            if (instr.instructionValue == "R") { //90 degrees clockwise (R)
                when (currentPosition.direction) {
                    FACING.RIGHT -> currentPosition.direction = FACING.DOWN
                    FACING.DOWN -> currentPosition.direction = FACING.LEFT
                    FACING.LEFT -> currentPosition.direction = FACING.UP
                    FACING.UP -> currentPosition.direction = FACING.RIGHT
                }
            } else if (instr.instructionValue == "L") { //90 degrees counterclockwise (L)
                when (currentPosition.direction) {
                    FACING.RIGHT -> currentPosition.direction = FACING.UP
                    FACING.DOWN -> currentPosition.direction = FACING.RIGHT
                    FACING.LEFT -> currentPosition.direction = FACING.DOWN
                    FACING.UP -> currentPosition.direction = FACING.LEFT
                }
            }
        }

        fun fillBlankColumns(s: String, maxRowSize: Int) {
            var newMap: MutableList<MutableList<Tile>> = mutableListOf()
            arrayMap.forEachIndexed { index, row ->
                val last = row.last()
                while (row.size < maxRowSize) {
                    row.add(Tile(s, index, last.col+1))
                }
                newMap.add(row)
            }
            arrayMap = newMap
        }
    }

    fun loadInstructions(s: String): MutableList<Instruction> {
        var lastLine = s
        val instructions: MutableList<Instruction> = mutableListOf()
        var previousCharacters = ""
        var thisCharacter = ""
        while (lastLine.isNotEmpty()) {
            thisCharacter = lastLine.first().toString()
            if (thisCharacter == "R" || thisCharacter == "L") {
                if (previousCharacters.isNotEmpty()) {
                    instructions.add(Instruction(InstructionType.MOVE, previousCharacters))
                    previousCharacters = ""
                }

                instructions.add(Instruction(InstructionType.TURN, thisCharacter))
            } else {
                previousCharacters += thisCharacter
            }

            lastLine = lastLine.substring(1)
        }

        if (previousCharacters.isNotEmpty()) {
            instructions.add(Instruction(InstructionType.MOVE, previousCharacters))
        }

        return instructions
    }

    var lastLine = ""
    val fileName =
        "src/Day22_input.txt"
//        "src/Day22_sample.txt"

    val m = Matrix()
    var row = 0
    var col = 0
    var maxRowSize = 0

    File(fileName).forEachLine {line ->
        val list = mutableListOf<Tile>()
        if (line.isNotBlank()) {
            var x = line
            while (x.isNotEmpty() && !x.first().toString().none { it !in 'A'..'Z' && it !in 'a'..'z' && it !in '0'..'9'}) {
                list.add(Tile(x.first().toString(), row, col))
                x = x.substring(1)
                col++
            }

            if (list.size > 0) {
                m.addRow(list)
            }

            if (list.size > maxRowSize) {
                maxRowSize = list.size
            }
        }

        lastLine = line
        col = 0
        row++
    }

    m.fillBlankColumns(" ", maxRowSize)
    val instructions = loadInstructions(lastLine)
    m.setStartPosition()

    instructions.forEach {
        m.runInstruction(it)
    }

    m.printPassword()
}
