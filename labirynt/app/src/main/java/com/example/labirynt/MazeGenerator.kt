package com.example.labirynt

import kotlin.random.Random
import kotlin.text.compareTo

class MazeGenerator {
    companion object {
        private const val DOWN = 0b1000
        private const val UP = 0b0100
        private const val RIGHT = 0b0010
        private const val LEFT = 0b0001
        private const val ENTRANCE = 0b10000
        private const val EXIT = 0b00
        private const val ONE_WAY_PROBABILITY = 0.1f // 30% szans na jednokierunkowe przejście
        fun generate(rows: Int, cols: Int): Array<IntArray> {
            var maze: Array<IntArray>
            var entrancePos: Pair<Int, Int>
            var exitPos: Pair<Int, Int>

            // Generuj labirynt dopóki nie będzie w pełni osiągalny
            do {
                maze = Array(rows) { IntArray(cols) { 0 } }
                val visited = Array(rows) { BooleanArray(cols) { false } }

                generatePath(maze, visited, 0, 0)

                entrancePos = getRandomEdgePosition(rows, cols)
                maze[entrancePos.first][entrancePos.second] =
                    maze[entrancePos.first][entrancePos.second] or ENTRANCE

                do {
                    exitPos = getRandomEdgePosition(rows, cols)
                } while (exitPos == entrancePos)

                maze[exitPos.first][exitPos.second] = EXIT

            } while (!isFullyReachable(maze, entrancePos.first, entrancePos.second))

            return maze
        }

        private fun isFullyReachable(maze: Array<IntArray>, startRow: Int, startCol: Int): Boolean {
            val rows = maze.size
            val cols = maze[0].size
            val visited = Array(rows) { BooleanArray(cols) { false } }
            val queue = ArrayDeque<Pair<Int, Int>>()

            queue.add(Pair(startRow, startCol))
            visited[startRow][startCol] = true
            var reachableCount = 1

            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                val cell = maze[row][col]

                // Sprawdź wszystkie możliwe kierunki
                if ((cell and UP) != 0 && row > 0 && !visited[row - 1][col]) {
                    visited[row - 1][col] = true
                    queue.add(Pair(row - 1, col))
                    reachableCount++
                }
                if ((cell and DOWN) != 0 && row < rows - 1 && !visited[row + 1][col]) {
                    visited[row + 1][col] = true
                    queue.add(Pair(row + 1, col))
                    reachableCount++
                }
                if ((cell and LEFT) != 0 && col > 0 && !visited[row][col - 1]) {
                    visited[row][col - 1] = true
                    queue.add(Pair(row, col - 1))
                    reachableCount++
                }
                if ((cell and RIGHT) != 0 && col < cols - 1 && !visited[row][col + 1]) {
                    visited[row][col + 1] = true
                    queue.add(Pair(row, col + 1))
                    reachableCount++
                }
            }

            // Sprawdź czy wszystkie komórki są osiągalne
            return reachableCount == rows * cols
        }


        private fun getRandomEdgePosition(rows: Int, cols: Int): Pair<Int, Int> {
            val edge = Random.nextInt(4) // 0=góra, 1=dół, 2=lewo, 3=prawo

            return when (edge) {
                0 -> Pair(0, Random.nextInt(cols)) // Górna krawędź
                1 -> Pair(rows - 1, Random.nextInt(cols)) // Dolna krawędź
                2 -> Pair(Random.nextInt(rows), 0) // Lewa krawędź
                else -> Pair(Random.nextInt(rows), cols - 1) // Prawa krawędź
            }
        }


        private fun generatePath(
            maze: Array<IntArray>,
            visited: Array<BooleanArray>,
            row: Int,
            col: Int
        ) {
            visited[row][col] = true

            val directions = listOf(UP, DOWN, LEFT, RIGHT).shuffled()

            for (direction in directions) {
                val newRow = row + when (direction) {
                    UP -> -1
                    DOWN -> 1
                    else -> 0
                }
                val newCol = col + when (direction) {
                    LEFT -> -1
                    RIGHT -> 1
                    else -> 0
                }

                if (isValid(newRow, newCol, maze.size, maze[0].size) &&
                    !visited[newRow][newCol]) {

                    // Dodaj przejście w kierunku ruchu
                    maze[row][col] = maze[row][col] or direction

                    // Z pewnym prawdopodobieństwem NIE dodawaj przejścia powrotnego
                    if (Random.nextFloat() > ONE_WAY_PROBABILITY) {
                        // Dwukierunkowe - dodaj przejście w obu kierunkach
                        maze[newRow][newCol] = maze[newRow][newCol] or opposite(direction)
                    }
                    // W przeciwnym razie pozostaw jednokierunkowe

                    generatePath(maze, visited, newRow, newCol)
                }
            }
        }


        private fun isValid(row: Int, col: Int, rows: Int, cols: Int): Boolean {
            return row in 0 until rows && col in 0 until cols
        }

        private fun opposite(direction: Int): Int {
            return when (direction) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
                else -> 0
            }
        }
    }
}