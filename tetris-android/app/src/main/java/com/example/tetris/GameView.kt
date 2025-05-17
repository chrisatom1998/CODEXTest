package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val game = TetrisGame()
    private val paint = Paint()

    private var cellSize = 0f

    init {
        paint.isAntiAlias = true
        game.start()
        postDelayed(::gameLoop, 500)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cellSize = (w / game.getBoard()[0].size).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawPiece(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        val board = game.getBoard()
        for (y in board.indices) {
            for (x in board[y].indices) {
                val value = board[y][x]
                if (value != 0) {
                    paint.color = getColorFor(value)
                    canvas.drawRect(
                        x * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize,
                        paint
                    )
                }
            }
        }
    }

    private fun drawPiece(canvas: Canvas) {
        val piece = game.currentPiece ?: return
        val px = game.currentX
        val py = game.currentY
        piece.matrix.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell != 0) {
                    paint.color = getColorFor(cell)
                    canvas.drawRect(
                        (px + x) * cellSize,
                        (py + y) * cellSize,
                        (px + x + 1) * cellSize,
                        (py + y + 1) * cellSize,
                        paint
                    )
                }
            }
        }
    }

    private fun getColorFor(value: Int): Int {
        return when (value) {
            1 -> Color.CYAN
            2 -> Color.YELLOW
            3 -> Color.MAGENTA
            4 -> Color.GREEN
            5 -> Color.RED
            6 -> Color.BLUE
            7 -> Color.LTGRAY
            else -> Color.WHITE
        }
    }

    private fun gameLoop() {
        game.tick()
        invalidate()
        postDelayed(::gameLoop, 500)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val third = width / 3f
            when {
                event.x < third -> game.moveLeft()
                event.x > 2 * third -> game.moveRight()
                else -> game.rotate()
            }
            invalidate()
        }
        return true
    }
}
