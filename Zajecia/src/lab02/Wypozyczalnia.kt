package lab02

class Wypozyczalnia(garagesCount: Int) {

    private val garages: MutableList<Garaz> = MutableList(garagesCount) { Garaz() }

    fun park(vehicle: Pojazd): Boolean =
        garages.firstOrNull { it.zaparkuj(vehicle) } != null

    fun unparkById(id: Int): Boolean =
        garages.firstOrNull { it.wyjmijById(id) } != null

    fun status(): String =
        garages.mapIndexed { i, g -> "[$i] = ${g.status()}" }.joinToString(", ")
}