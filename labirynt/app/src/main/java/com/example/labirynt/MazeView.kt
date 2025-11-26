package com.example.labirynt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.and
import kotlin.math.min
import androidx.core.graphics.toColorInt

class MazeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val playerPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val keyPaint = Paint().apply {
        color = "#D5B60A".toColorInt()
        style = Paint.Style.FILL
    }

    private val doorPaint = Paint().apply {
        color = Color.rgb(139, 69, 19) // Brązowy kolor
        style = Paint.Style.FILL
    }


    var maze: Array<IntArray> = emptyArray()
    var playerRow: Int = 0
    var playerCol: Int = 0

    companion object {
        const val KEY = 0b100000
        const val DOOR = 0b1000000
        const val DOWN = 0b1000
        const val UP = 0b0100
        const val RIGHT = 0b0010
        const val LEFT = 0b0001
    }



    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        if (maze.isEmpty()) return

        // 1. Tło jest ścianą (czarne)
        canvas.drawColor(Color.BLACK)

        // Ustawienia pędzla dla "podłogi"
        val floorPaint = Paint().apply {
            color = Color.WHITE // Lub jasnoszary
            style = Paint.Style.FILL
        }

        // Wyjście wyróżnione
        val exitPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        val rows = maze.size
        val cols = maze[0].size
        val cellSize = min(width / cols.toFloat(), height / rows.toFloat())
        val offsetX = (width - cellSize * cols) / 2
        val offsetY = (height - cellSize * rows) / 2

        // Margines, żeby ściany były grube (np. 20% komórki to ściana)
        val wallThickness = cellSize / 7.5f

        for (row in maze.indices) {
            for (col in maze[row].indices) {
                val cell = maze[row][col]
                val left = offsetX + col * cellSize
                val top = offsetY + row * cellSize
                val cx = left + cellSize / 2
                val cy = top + cellSize / 2

                if (cell == 0) {
                    canvas.drawRect(left, top + cellSize, left + cellSize, top , exitPaint)
                } else {
                    // Zawsze rysujemy środek komórki (skrzyżowanie)
                    canvas.drawRect(
                        cx - wallThickness, cy - wallThickness,
                        cx + wallThickness, cy + wallThickness,
                        floorPaint
                    )

                    // Rysujemy odnogi tam gdzie są bity
                    if ((cell and UP) != 0) {
                        canvas.drawRect(cx - wallThickness, top, cx + wallThickness, cy, floorPaint)
                    }
                    if ((cell and DOWN) != 0) {
                        canvas.drawRect(cx - wallThickness, cy, cx + wallThickness, top + cellSize, floorPaint)
                    }
                    if ((cell and LEFT) != 0) {
                        canvas.drawRect(left, cy - wallThickness, cx, cy + wallThickness, floorPaint)
                    }
                    if ((cell and RIGHT) != 0) {
                        canvas.drawRect(cx, cy - wallThickness, left + cellSize, cy + wallThickness, floorPaint)
                    }
                    // Rysuj klucz (jeśli bit KEY jest ustawiony)
                    if ((cell and KEY) != 0) {
                        canvas.drawCircle(cx + cellSize / 6, cy, cellSize / 6, keyPaint)
                        canvas.drawRect(cx - cellSize / 6, cy - cellSize / 12, cx + cellSize / 6, cy + cellSize / 12, keyPaint)
                    }

                    // Rysuj drzwi (jeśli bit DOOR jest ustawiony)
                    if ((cell and DOOR) != 0) {
                        canvas.drawRect(cx - wallThickness, cy - cellSize / 4,
                            cx + wallThickness, cy + cellSize / 4, doorPaint)
                    }

                }

                // Gracz
                if (row == playerRow && col == playerCol) {
                    canvas.drawCircle(cx, cy, cellSize / 4, playerPaint)
                }
            }
        }
    }
}