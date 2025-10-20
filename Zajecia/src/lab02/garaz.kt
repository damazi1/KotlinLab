package lab02

class garaz{
    var pojazd: Pojazd? = null
    fun zaparkuj(p: Pojazd) {
        this.pojazd = p
    }

    fun wyjmij(): Pojazd? {
        val tmp = pojazd
        pojazd = null
        return tmp
    }
}