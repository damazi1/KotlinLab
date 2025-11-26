package com.example.labirynt

import java.util.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.random.Random

class MazeGenerator {
    companion object {
        private const val KEY = 0b100000
        private const val DOOR = 0b1000000
        private const val DOWN = 0b1000
        private const val UP = 0b0100
        private const val RIGHT = 0b0010
        private const val LEFT = 0b0001
        private const val ENTRANCE = 0b10000
        private const val EXIT = 0b00


        fun generate(rows: Int, cols: Int, maxAttempts: Int = 10): Array<IntArray> {
            var maze: Array<IntArray>
            var attempts = 0
            do {
                maze = generateAttempt(rows, cols)
                attempts++
                if (attempts >= maxAttempts) {
                    println("Ostrzeżenie: Przekroczono maksymalną liczbę prób generowania labiryntu")
                    break
                }
            } while (!isTraversable(maze, rows, cols))

            println("Wygenerowano poprawny labirynt po $attempts próbach")
            return maze
        }

        /**
         * Funkcja próbuje wygenerować labirynt z wejściem, kluczem, drzwiami i wyjściem
         * W przypadku niepowodzenia (np. brak przechodniości) należy wywołać ją ponownie
         * @param rows Liczba wierszy
         * @param cols Liczba kolumn
         * @return Macierz reprezentująca labirynt
         */
        private fun generateAttempt(rows: Int, cols: Int): Array<IntArray> {
            val maze = Array(rows) { IntArray(cols) }
            val visited = Array(rows) { BooleanArray(cols) { false } }

            generatePath(maze, visited, 0, 0)

            val allDeadEnds = findAllDeadEnds(maze, rows, cols)

            // Ślepe zaułki na krawędziach labiryntu
            val edgeDeadEnds = allDeadEnds.filter { (r, c) ->
                r == 0 || r == rows - 1 || c == 0 || c == cols - 1
            }

            // Ustalenie pozycji wejścia i klucza
            val entrance = if (edgeDeadEnds.isNotEmpty()) edgeDeadEnds.random() else Pair(0, 0)
            maze[entrance.first][entrance.second] = maze[entrance.first][entrance.second] or ENTRANCE

            val keyCandidates = allDeadEnds.filter { it != entrance }
            val keyPos = if (keyCandidates.isNotEmpty()) keyCandidates.random() else Pair(rows-1, cols-1)
            maze[keyPos.first][keyPos.second] = maze[keyPos.first][keyPos.second] or KEY

            val parents = bfsParents(maze, entrance, rows, cols)

            val pathToKey = reconstructPath(parents, keyPos)
            val exitCandidates = allDeadEnds.filter { it != entrance && it != keyPos }.shuffled()

            var exitPos: Pair<Int, Int>? = null
            var doorPos: Pair<Int, Int>? = null

            for (candidate in exitCandidates) {
                val pathToExit = reconstructPath(parents, candidate)
                val divergencePoint = findDivergencePoint(pathToKey, pathToExit)
                val pathAfterDivergence = getPathSegmentAfter(pathToExit, divergencePoint)

                // Jeżeli został znaleziony fragment ścieżki na którym można ustawić
                // drzwi, tak aby dało się dojść do wyjścia to ustawiamy i wychodzimy z pętli
                if (pathAfterDivergence.isNotEmpty()) {
                    exitPos = candidate
                    doorPos = pathAfterDivergence.first()
                    break
                }
            }

            // Dodajemy wyjście i drzwi do macierzy jeśli udało się je znaleźć
            if (exitPos != null && doorPos != null) {
                maze[exitPos.first][exitPos.second] = EXIT
                maze[doorPos.first][doorPos.second] = maze[doorPos.first][doorPos.second] or DOOR
            }

            return maze
        }

        /**
         * Sprawdza czy labirynt jest przechodni (wejście -> klucz -> drzwi -> wyjście)
         * @param maze Macierz reprezentująca labirynt
         * @param rows Liczba wierszy
         * @param cols Liczba kolumn
         * @return true jeśli labirynt jest przechodni, false w przeciwnym razie
         */
        private fun isTraversable(maze: Array<IntArray>, rows: Int, cols: Int): Boolean {
            // Ustalenie pozycji kluczowych elementów
            var entrance: Pair<Int, Int>? = null
            var keyPos: Pair<Int, Int>? = null
            var doorPos: Pair<Int, Int>? = null
            var exitPos: Pair<Int, Int>? = null

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val cell = maze[r][c]
                    if ((cell and ENTRANCE) != 0) entrance = Pair(r, c)
                    if ((cell and KEY) != 0) keyPos = Pair(r, c)
                    if ((cell and DOOR) != 0) doorPos = Pair(r, c)
                    if (cell == EXIT) exitPos = Pair(r, c)
                }
            }

            // Czy kluczowe elementy istnieją
            if (entrance == null || keyPos == null || doorPos == null || exitPos == null) {
                return false
            }

            // Dotarcie od wejścia do klucza jest niemożliwe
            if (!canReach(maze, entrance, keyPos, rows, cols, ignoreDoor = true)) {
                return false
            }

            // Dotarcie od klucza do drzwi jest niemożliwe
            if (!canReach(maze, keyPos, doorPos, rows, cols, ignoreDoor = true)) {
                return false
            }

            // Dotarcie od drzwi do wyjścia jest niemożliwe
            if (!canReach(maze, doorPos, exitPos, rows, cols, ignoreDoor = false)) {
                return false
            }

            return true
        }

        /**
         * BFS do sprawdzania czy można dotrzeć z punktu startowego do docelowego
         * @param maze Macierz reprezentująca labirynt
         * @param start Punkt startowy (wiersz, kolumna)
         * @param target Punkt docelowy (wiersz, kolumna)
         * @param rows Liczba wierszy
         * @param cols Liczba kolumn
         * @param ignoreDoor Czy ignorować drzwi podczas sprawdzania przejścia
         * @return true jeśli można dotrzeć do celu, false w przeciwnym razie
         */
        private fun canReach(
            maze: Array<IntArray>,
            start: Pair<Int, Int>,
            target: Pair<Int, Int>,
            rows: Int,
            cols: Int,
            ignoreDoor: Boolean
        ): Boolean {
            val queue = ArrayDeque<Pair<Int, Int>>()
            val visited = mutableSetOf<Pair<Int, Int>>()

            queue.add(start)
            visited.add(start)

            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()

                if (curr == target) {
                    return true
                }

                val (r, c) = curr

                val directions = listOf(
                    Triple(UP, r - 1, c),
                    Triple(DOWN, r + 1, c),
                    Triple(LEFT, r, c - 1),
                    Triple(RIGHT, r, c + 1)
                )

                for ((dirBit, nr, nc) in directions) {
                    if (isValid(nr, nc, rows, cols)) {
                        if ((maze[r][c] and dirBit) != 0) {
                            val neighbor = Pair(nr, nc)

                            // Jeśli nie ignorujemy drzwi i trafimy na drzwi, nie przechodzimy
                            if (!ignoreDoor && (maze[nr][nc] and DOOR) != 0 && neighbor != target) {
                                continue
                            }

                            if (neighbor !in visited) {
                                visited.add(neighbor)
                                queue.add(neighbor)
                            }
                        }
                    }
                }
            }

            return false
        }

        // --- Metody pomocnicze ---

        /**
         * BFS do tworzenia mapy rodziców dla każdej komórki w labiryncie
         * Pozwala odtworzyć ścieżkę od punktu startowego do dowolnego innego punktu
         * @param maze Macierz reprezentująca labirynt
         * @param start Punkt startowy (wejście)
         * @param rows Liczba wierszy
         * @param cols Liczba kolumn
         * @return Mapa rodziców dla każdej komórki
         */
        private fun bfsParents(
            maze: Array<IntArray>,
            start: Pair<Int, Int>,
            rows: Int,
            cols: Int
        ): Map<Pair<Int, Int>, Pair<Int, Int>> {
            val parents = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
            val queue = ArrayDeque<Pair<Int, Int>>()
            val visited = mutableSetOf<Pair<Int, Int>>()

            queue.add(start)
            visited.add(start)

            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()
                val (r, c) = curr

                // Sprawdź sąsiadów zgodnie z otwartymi ścianami
                val directions = listOf(
                    Triple(UP, r - 1, c),
                    Triple(DOWN, r + 1, c),
                    Triple(LEFT, r, c - 1),
                    Triple(RIGHT, r, c + 1)
                )

                for ((dirBit, nr, nc) in directions) {
                    if (isValid(nr, nc, rows, cols)) {
                        // Czy jest przejście (bit ściany ustawiony w komórce źródłowej)
                        if ((maze[r][c] and dirBit) != 0) {
                            val neighbor = Pair(nr, nc)
                            if (neighbor !in visited) {
                                visited.add(neighbor)
                                parents[neighbor] = curr
                                queue.add(neighbor)
                            }
                        }
                    }
                }
            }
            return parents
        }

        /**
         * Odtwarzanie ścieżki od punktu startowego do punktu docelowego za pomocą mapy rodziców
         * @param parents Mapa rodziców dla każdej komórki
         * @param target Punkt docelowy (wiersz, kolumna)
         * @return Lista współrzędnych reprezentujących ścieżkę od startu do celu
         */
        private fun reconstructPath(
            parents: Map<Pair<Int, Int>, Pair<Int, Int>>,
            target: Pair<Int, Int>
        ): List<Pair<Int, Int>> {
            val path = ArrayList<Pair<Int, Int>>()
            var curr: Pair<Int, Int>? = target
            while (curr != null) {
                path.add(curr)
                curr = parents[curr]
            }
            return path.reversed() // Od startu do celu
        }

        /**
         * Znajdowanie punktu rozbieżności między dwiema ścieżkami
         * Ostatnia wspólna komórka na obu ścieżkach
         * @param pathA Pierwsza ścieżka (do klucza)
         * @param pathB Druga ścieżka (do wyjścia)
         * @return Punkt rozbieżności (wiersz, kolumna)
         */
        private fun findDivergencePoint(pathA: List<Pair<Int, Int>>, pathB: List<Pair<Int, Int>>): Pair<Int, Int> {
            var lastCommon = pathA[0]
            val limit = minOf(pathA.size, pathB.size)
            for (i in 0 until limit) {
                if (pathA[i] == pathB[i]) {
                    lastCommon = pathA[i]
                } else {
                    break
                }
            }
            return lastCommon
        }

        /**
         * Pobiera ścieżkę do wyjścia zaczynajacą się po punkcie rozbieżności
         * @param fullPath Pełna ścieżka (do wyjścia)
         * @param afterPoint Punkt rozbieżności (wiersz, kolumna)
         * @return Podścieżka zaczynająca się po punkcie rozbieżności
         */
        private fun getPathSegmentAfter(fullPath: List<Pair<Int, Int>>, afterPoint: Pair<Int, Int>): List<Pair<Int, Int>> {
            val idx = fullPath.indexOf(afterPoint)
            if (idx == -1 || idx == fullPath.size - 1) return emptyList()
            return fullPath.subList(idx + 1, fullPath.size)
        }

        /**
         * Znajdowanie wszystkich ślepych zaułków w labiryncie
         * @param maze Macierz reprezentująca labirynt
         * @param rows Liczba wierszy
         * @param cols Liczba kolumn
         * @return Lista par współrzędnych (wiersz, kolumna) ślepych zaułków
         */
        private fun findAllDeadEnds(maze: Array<IntArray>, rows: Int, cols: Int): List<Pair<Int, Int>> {
            val deadEnds = mutableListOf<Pair<Int, Int>>()
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (isDeadEnd(maze[r][c])) {
                        deadEnds.add(Pair(r, c))
                    }
                }
            }
            return deadEnds
        }

        /**
         * Sprawdza czy dana komórka jest ślepym zaułkiem
         * @param cell Wartość komórki labiryntu
         * @return true jeśli pozostał tylko jeden bit, false w przeciwnym razie
         */
        private fun isDeadEnd(cell: Int): Boolean {
            val paths = cell and 0b1111
            return Integer.bitCount(paths) == 1
        }

        /**
         * Generowanie labiryntu za pomocą rekurencyjnego backtrackingu (Algorytm DFS)
         * @param maze Macierz reprezentująca labirynt
         * @param visited Macierz odwiedzonych komórek
         * @param row Bieżący wiersz
         * @param col Bieżąca kolumna
         */
        private fun generatePath(maze: Array<IntArray>, visited: Array<BooleanArray>, row: Int, col: Int) {
            visited[row][col] = true
            val directions = listOf(UP, DOWN, LEFT, RIGHT).shuffled()

            for (direction in directions) {
                val (newRow, newCol) = when (direction) {
                    UP -> row - 1 to col
                    DOWN -> row + 1 to col
                    LEFT -> row to col - 1
                    RIGHT -> row to col + 1
                    else -> row to col
                }

                if (isValid(newRow, newCol, maze.size, maze[0].size) && !visited[newRow][newCol]) {
                    maze[row][col] = maze[row][col] or direction
                    maze[newRow][newCol] = maze[newRow][newCol] or opposite(direction)
                    generatePath(maze, visited, newRow, newCol)
                }
            }
        }

        /**
         * Zwraca przeciwny kierunek
         * @param direction Kierunek wejściowy
         * @return Przeciwny kierunek
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
         * Walidacja współrzędnych
         * @param r Wiersz
         * @param c Kolumna
         * @param rows Liczba wierszy w macierzy
         * @param cols Liczba kolumn w macierzy
         * @return true jeśli współrzędne są z zakresu, false w przeciwnym razie
         */
        private fun isValid(r: Int, c: Int, rows: Int, cols: Int) = r in 0 until rows && c in 0 until cols
    }
}