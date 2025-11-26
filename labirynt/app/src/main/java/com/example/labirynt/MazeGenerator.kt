package com.example.labirynt

import kotlin.random.Random

class MazeGenerator {
    companion object {
        private const val DOWN = 0b1000
        private const val UP = 0b0100
        private const val RIGHT = 0b0010
        private const val LEFT = 0b0001
        private const val ENTRANCE = 0b10000
        private const val EXIT = 0b00

       fun generate(rows: Int, cols: Int): Array<IntArray> {
            val maze = Array(rows) { IntArray(cols) }
            val visited = Array(rows) { BooleanArray(cols) { false } }

            // Wygeneruj spójny labirynt
            generatePath(maze, visited, 0, 0)

            // Znajdź wszystkie ślepe zaułki na krawędziach
            val deadEnds = findEdgeDeadEnds(maze, rows, cols)

            // Wybierz losowe wejście
            val entrancePos = deadEnds.random()
            maze[entrancePos.first][entrancePos.second] =
                maze[entrancePos.first][entrancePos.second] or ENTRANCE

            // Wybierz losowe wyjście (inne niż wejście)
            var exitPos: Pair<Int, Int>
            do {
                exitPos = deadEnds.random()
            } while (exitPos == entrancePos)

            // Wyzeruj komórkę wyjścia
            maze[exitPos.first][exitPos.second] = EXIT

            return maze
        }

        /**
         * Znajduje wszystkie ślepe zaułki (dead ends) na krawędziach labiryntu.
         * Ślepy zaułek = komórka z tylko jednym przejściem.
         */
        private fun findEdgeDeadEnds(
            maze: Array<IntArray>,
            rows: Int,
            cols: Int
        ): List<Pair<Int, Int>> {
            val deadEnds = mutableListOf<Pair<Int, Int>>()

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    // Sprawdź czy komórka jest na krawędzi
                    val isEdge = row == 0 || row == rows - 1 || col == 0 || col == cols - 1

                    if (isEdge && isDeadEnd(maze[row][col])) {
                        deadEnds.add(Pair(row, col))
                    }
                }
            }

            return deadEnds
        }

        /**
         * Sprawdza czy komórka jest ślepym zaułkiem (ma tylko jedno przejście).
         */
        private fun isDeadEnd(cell: Int): Boolean {
            val paths = cell and 0b1111 // Maskuj tylko kierunki (bez ENTRANCE)
            // Sprawdź ile bitów jest ustawionych (ile przejść)
            return paths.countOneBits() == 1
        }

        /**
         *   Zwraca przeciwny kierunek (Do tworzenia tras)
         */
        private fun opposite(direction: Int): Int {
            return when (direction) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
                else -> 0
            }
        }
        /**
         * Rekurencyjna funkcja do generowania ścieżek w labiryncie
         * Wykorzystuje algorytm DFS (Algorytm przeszukiwania w grafu w głąb)
         * z losowym wyborem kierunków (Tutaj za losowość odpowiada funkcja shuffled())
         */
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
                // Sprawdza czy pozycja przechodzi walidacjię (czyli mieści się w zakresie od 0 do n)
                // oraz czy nie była odwiedzona
                // Jeżeli obia warunki są spełnione, to tworzy ścieżkę między komórkami i ponownie wywołuje samą siebie
                // tym razem z nowymi współrzędnymi
                if (isValid(newRow, newCol, maze.size, maze[0].size) &&
                    !visited[newRow][newCol]) {

                    maze[row][col] = maze[row][col] or direction
                    maze[newRow][newCol] = maze[newRow][newCol] or opposite(direction)

                    generatePath(maze, visited, newRow, newCol)
                }
            }
        }

        private fun isValid(row: Int, col: Int, rows: Int, cols: Int): Boolean {
            return row in 0 until rows && col in 0 until cols
        }
    }
}