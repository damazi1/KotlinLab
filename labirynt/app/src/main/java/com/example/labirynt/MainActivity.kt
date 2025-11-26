package com.example.labirynt

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.labirynt.databinding.ActivityMainBinding
import kotlin.and
import kotlin.inc
import kotlin.inv
import kotlin.text.get

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val maze = arrayOf(
        intArrayOf(10,8,10,9),
        intArrayOf(28,33,0,76),
        intArrayOf(12,10,9,13),
        intArrayOf(6,5,6,5)
    )
//private var maze: Array<IntArray> = MazeGenerator.generate(8,8)


    companion object {
        const val DOOR = 0b1000000 // 64 - drzwi (nieużywane w tej implementacji)
        const val KEY = 0b100000 // 32 - klucz (nieużywane w tej implementacji)
        const val ENTRANCE = 0b10000 // 16 - wejście
        const val DOWN = 0b1000   // 8 - ruch w dół
        const val UP = 0b0100     // 4 - ruch w górę
        const val RIGHT = 0b0010  // 2 - ruch w prawo
        const val LEFT = 0b0001   // 1 - ruch w lewo
        const val EXIT = 0b00     // 0 - wyjście
    }
    private var playerRow = 0
    private var playerCol = 0
    private var stepCount = 0
    private var hasKey = false // Stan klucza gracza
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findPlayerStartPosition()
        updateButtons()
        binding.pauseButton.setOnClickListener {
            showPauseFragment()
        }
        binding.UpButton.setOnClickListener { movePlayer(UP) }
        binding.DownButton.setOnClickListener { movePlayer(DOWN) }
        binding.LeftButton.setOnClickListener { movePlayer(LEFT) }
        binding.RightButton.setOnClickListener { movePlayer(RIGHT) }
    }

    private fun findPlayerStartPosition() {

        for (row in maze.indices) {
            for (col in maze[row].indices) {
                // Sprawdź czy 4. bit jest ustawiony
                if ((maze[row][col] and ENTRANCE) != 0) {
                    playerRow = row
                    playerCol = col
                    updateMazeView()
                    return
                }
            }
        }
    }
    private fun updateMazeView() {
        binding.mazeView.maze = maze
        binding.mazeView.playerRow = playerRow
        binding.mazeView.playerCol = playerCol
        binding.mazeView.invalidate()
    }

    private fun updateButtons() {
        val currentCell = maze[playerRow][playerCol]

        binding.UpButton.isEnabled = (currentCell and UP) != 0
        binding.DownButton.isEnabled = (currentCell and DOWN) != 0
        binding.LeftButton.isEnabled = (currentCell and LEFT) != 0
        binding.RightButton.isEnabled = (currentCell and RIGHT) != 0
    }

    private fun movePlayer(direction: Int) {
        val currentCell = maze[playerRow][playerCol]

        // Sprawdź czy ruch w danym kierunku jest możliwy
        if ((currentCell and direction) == 0) {
            return
        }

        // Oblicz nową pozycję
        val newRow = when (direction) {
            UP -> playerRow - 1
            DOWN -> playerRow + 1
            else -> playerRow
        }
        val newCol = when (direction) {
            LEFT -> playerCol - 1
            RIGHT -> playerCol + 1
            else -> playerCol
        }

        val newCell = maze[newRow][newCol]

        // Sprawdź czy w nowej komórce są drzwi
        if ((newCell and DOOR) != 0) {
            if (!hasKey) {
                // Brak klucza - nie można przejść
                // Możesz dodać Toast lub animację
                android.widget.Toast.makeText(
                    this,
                    "Potrzebujesz klucza!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return
            } else {
                // Mamy klucz - usuń drzwi
                maze[newRow][newCol] = newCell and DOOR.inv()
                hasKey = false // Zużyj klucz
            }
        }

        // Wykonaj ruch
        playerRow = newRow
        playerCol = newCol
        stepCount++

        // Sprawdź czy w nowej komórce jest klucz
        if ((maze[playerRow][playerCol] and KEY) != 0) {
            hasKey = true
            // Usuń klucz z labiryntu
            maze[playerRow][playerCol] = maze[playerRow][playerCol] and KEY.inv()

            android.widget.Toast.makeText(
                this,
                "Znalazłeś klucz!",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        updateButtons()
        updateMazeView()

        if (newCell == EXIT) {
            // Animacja wygranej
            binding.mazeView.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(0.5f)
                .setDuration(500)
                .withEndAction {
                    binding.mazeView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(500)
                        .start()
                }
                .start()

            // Opóźnienie 5 sekund przed przejściem do ekranu końcowego
            binding.root.postDelayed({
                val intent = android.content.Intent(this, Finish::class.java)
                intent.putExtra("STEP_COUNT", stepCount) // Przekaż liczbę kroków
                startActivity(intent)
                finish()
            }, 2000)

            return
        }

    }

    private fun showPauseFragment() {
        binding.pauseFragmentContainer.visibility = View.VISIBLE
        val pauseFragment = PauseFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.pauseFragmentContainer, pauseFragment)
            .commit()
    }
}