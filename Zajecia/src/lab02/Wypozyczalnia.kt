import lab02.Pojazd
import lab02.Garaz
import kotlin.compareTo

class Wypozyczalnia(private val garagesCount: Int) {

    private val garaze: MutableList<Garaz> = MutableList(garagesCount) { Garaz() }

    fun getGaragesCount(): Int = garagesCount

    fun get(index: Int): Pojazd? =
        if (index in 0 until garagesCount) garaze[index].pojazd else null

    private fun isParked(car: Pojazd): Boolean =
        garaze.any { g ->
            val p = g.pojazd
            p !== null && (p === car || (try { p.id == car.id } catch (_: Throwable) { false }))
        }

    fun park(car: Pojazd, index: Int): Boolean {
        if (index !in 0 until garagesCount) return false
        if (isParked(car)) return false
        val g = garaze[index]
        if (g.pojazd != null) return false
        g.zaparkuj(car)
        return true
    }

    fun remove(index: Int): Pojazd? {
        if (index !in 0 until garagesCount) return null
        return garaze[index].wyjmij()
    }

    fun status(): String =
        garaze.mapIndexed { i, g -> "[$i] = ${g.pojazd?.toString() ?: "empty"}" }.joinToString(", ")

    fun findGarageIndex(car: Pojazd): Int? {
        val idx = garaze.indexOfFirst { g ->
            val p = g.pojazd
            p !== null && (p === car || (try { p.id == car.id } catch (_: Throwable) { false }))
        }
        return if (idx >= 0) idx else null
    }
    fun tryParkOrReport(car: Pojazd, index: Int): String {
        // if already parked, return where
        val parkedAt = findGarageIndex(car)
        if (parkedAt != null) {
            return "Vehicle ${car.id} is already parked at #$parkedAt"
        }

        // find first empty slot and try to park using existing logic
        return if (index != null && park(car, index)) {
            "Vehicle ${car.id} parked at #$index"
        } else {
            "No space available to park vehicle ${car.toString()}"
        }
    }
}