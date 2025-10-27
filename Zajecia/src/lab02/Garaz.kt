package lab02

class Garaz {
    private var pojazd: Pojazd? = null

    fun zaparkuj(p: Pojazd): Boolean {
        if (pojazd == null) {
            pojazd = p
            return true
        }
        return false
    }

    fun wyjmijById(id: Int): Boolean {
        if (pojazd?.id == id) {
            pojazd = null
            return true
        }
        return false
    }

    fun status(): String =
        pojazd?.let { "${it::class.simpleName}: ${it.nazwa}" } ?: "miejsce puste"
}