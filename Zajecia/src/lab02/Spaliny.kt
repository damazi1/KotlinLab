package lab02

interface Spaliny {
    val rodzajPaliwa: RodzajPaliwa

    fun tank(ilosc: Int, rodzaj: RodzajPaliwa): Boolean
}