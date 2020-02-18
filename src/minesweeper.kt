package minesweeper

import java.util.Scanner
import kotlin.random.Random

fun main() {
    val scanner = Scanner(System.`in`)
    var field = Array(9) {Array<Cells>(9) { Cells(isOpen = false, isMine = false, isMarked = false, minesAround = 0) } }
    var loose = false
    var isFieldExist = false
    println("How many mines do you want on the field?")
    val minesNeeded = scanner.nextInt()

    printField(field, loose)
    loop@ while (!checkWin(field, minesNeeded) && !loose) {
        print("Set/unset mines marks or claim a cell as free: ")
        val y = scanner.nextInt() - 1
        val x = scanner.nextInt() - 1
        val action = scanner.next()
        when (action) {
            "mine" -> {
                if (field[x][y].isOpen) {
                    println("Cell already open")
                    continue@loop
                } else {
                    field[x][y].isMarked = !field[x][y].isMarked
                }
            }
            "free" -> {
                if (!isFieldExist) {
                    field = craateFild(field, minesNeeded, x, y)
                    isFieldExist = true
                }
                when {
                    field[x][y].isOpen -> {
                        println("Cell already open")
                        continue@loop
                    }
                    field[x][y].isMine -> {
                        loose = true
                        continue@loop
                    }
                    else -> field = openCells(field, x, y)
                }
            }
        }
        printField(field, loose)
    }
    if (!loose) println("Congratulations! You found all mines!") else {
        printField(field, loose)
        println("You stepped on a mine and failed!")
    }
}

data class Cells (var isOpen: Boolean, var isMine: Boolean, var isMarked: Boolean, var minesAround: Int)

fun openCells(field: Array<Array<Cells>>, x: Int, y:Int): Array<Array<Cells>> {
    field[x][y].isOpen = true
    field[x][y].isMarked = false
    if (field[x][y].minesAround == 0) {
        for (i in (x - 1)..(x + 1)) {
            for (j in (y - 1)..(y + 1)) {
                try {
                    if (field[i][j].minesAround == 0 && !field[i][j].isOpen) openCells(field, i, j)
                    else {
                        field[i][j].isOpen = true
                        field[i][j].isMarked = false
                    }
                } catch (e: ArrayIndexOutOfBoundsException) { }
            }
        }
    }
    return field
}

fun craateFild(field: Array<Array<Cells>>, minesNeeded: Int, firstX: Int, firstY: Int): Array<Array<Cells>> {
    var mines = 0
    while (minesNeeded > mines) {
        val x = Random.nextInt(0, 9)
        val y = Random.nextInt(0, 9)
        if (!(x == firstX && y == firstY)) {
            if (!field[x][y].isMine) {
                field[x][y].isMine = true
                mines++
                for (i in (x - 1)..(x + 1)) {
                    for (j in (y - 1)..(y + 1)) {
                        try {
                            field[i][j].minesAround++
                        } catch (e: ArrayIndexOutOfBoundsException) { }
                    }
                }
            }
        }

    }
    return field
}

fun checkWin(field: Array<Array<Cells>>, minesPlaced: Int):Boolean {
    var minesFound = 0
    var closedCells = 0
    var cellsMarked = 0
    for (i in field.indices) {
        for (j in field[i].indices) {
            if (field[i][j].isMine && field[i][j].isMarked) minesFound++
            if (!field[i][j].isOpen) closedCells++
            if (field[i][j].isMarked) cellsMarked++
        }
    }
    return (minesFound == minesPlaced && cellsMarked == minesFound) || closedCells == minesPlaced
}

fun printField(field: Array<Array<Cells>>, loose: Boolean) {
    println(" │123456789│\n" +
            "—│—————————│")
    for (i in field.indices) {
        print("${i + 1}|")
        for (j in field[0].indices) {
            print(when {
                loose -> {
                    when {
                        field[i][j].isMine -> "X"
                        field[i][j].minesAround == 0 -> "/"
                        else -> "${field[i][j].minesAround}"
                    }
                }
                field[i][j].isMarked -> "*"
                field[i][j].isOpen -> if (field[i][j].minesAround == 0) "/" else "${field[i][j].minesAround}"
                else -> "."
            })
        }
        println("|")
    }
    println("—│—————————│")
}
