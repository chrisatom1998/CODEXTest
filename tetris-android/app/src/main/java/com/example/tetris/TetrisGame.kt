package com.example.tetris

import kotlin.random.Random

class TetrisGame(private val width: Int = 10, private val height: Int = 20) {
    private val board = Array(height) { IntArray(width) }

    var currentPiece: Tetromino? = null
        private set
    var currentX = 0
        private set
    var currentY = 0
        private set

    fun start() {
        board.forEach { row -> row.fill(0) }
        spawnPiece()
    }

    fun getBoard(): Array<IntArray> = board

    fun tick() {
        if (!moveDown()) {
            lockPiece()
            clearLines()
            spawnPiece()
        }
    }

    fun moveLeft() {
        currentPiece?.let { piece ->
            if (canMove(piece, currentX - 1, currentY)) {
                currentX--
            }
        }
    }

    fun moveRight() {
        currentPiece?.let { piece ->
            if (canMove(piece, currentX + 1, currentY)) {
                currentX++
            }
        }
    }

    fun rotate() {
        currentPiece?.let { piece ->
            val next = piece.rotate()
            if (canMove(next, currentX, currentY)) {
                currentPiece = next
            }
        }
    }

    fun moveDown(): Boolean {
        currentPiece?.let { piece ->
            if (canMove(piece, currentX, currentY + 1)) {
                currentY++
                return true
            }
        }
        return false
    }

    private fun spawnPiece() {
        val index = Random.nextInt(Tetromino.SHAPES.size)
        val piece = Tetromino(Tetromino.SHAPES[index])
        currentPiece = piece
        currentX = width / 2 - piece.matrix[0].size / 2
        currentY = 0
    }

    private fun lockPiece() {
        val piece = currentPiece ?: return
        piece.matrix.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell != 0) {
                    val bx = currentX + x
                    val by = currentY + y
                    if (by in 0 until height && bx in 0 until width) {
                        board[by][bx] = cell
                    }
                }
            }
        }
    }

    private fun clearLines() {
        var newBoard = board.filterNot { row -> row.all { it != 0 } }
        val cleared = height - newBoard.size
        repeat(cleared) {
            newBoard = listOf(IntArray(width)) + newBoard
        }
        newBoard.forEachIndexed { idx, row -> board[idx] = row }
    }

    private fun canMove(piece: Tetromino, x: Int, y: Int): Boolean {
        for (py in piece.matrix.indices) {
            for (px in piece.matrix[py].indices) {
                if (piece.matrix[py][px] != 0) {
                    val bx = x + px
                    val by = y + py
                    if (bx !in 0 until width || by !in 0..height) {
                        return false
                    }
                    if (by < height && board[by][bx] != 0) {
                        return false
                    }
                }
            }
        }
        return true
    }
}

class Tetromino(val matrix: Array<IntArray>) {
    fun rotate(): Tetromino {
        val sizeY = matrix.size
        val sizeX = matrix[0].size
        val rotated = Array(sizeX) { IntArray(sizeY) }
        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                rotated[x][sizeY - y - 1] = matrix[y][x]
            }
        }
        return Tetromino(rotated)
    }

    companion object {
        val SHAPES = listOf(
            arrayOf(
                intArrayOf(1, 1, 1, 1)
            ),
            arrayOf(
                intArrayOf(2, 2),
                intArrayOf(2, 2)
            ),
            arrayOf(
                intArrayOf(0, 3, 0),
                intArrayOf(3, 3, 3)
            ),
            arrayOf(
                intArrayOf(4, 0, 0),
                intArrayOf(4, 4, 4)
            ),
            arrayOf(
                intArrayOf(0, 0, 5),
                intArrayOf(5, 5, 5)
            ),
            arrayOf(
                intArrayOf(6, 6, 0),
                intArrayOf(0, 6, 6)
            ),
            arrayOf(
                intArrayOf(0, 7, 7),
                intArrayOf(7, 7, 0)
            )
        )
    }
}
