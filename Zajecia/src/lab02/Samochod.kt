package lab02

class Samochod(override var nazwa: String, rodzajPaliwaId: Int) : Pojazd() {
    var rodzajPaliwa: RodzajPaliwa = RodzajPaliwa.fromId(rodzajPaliwaId)

    override fun toString(): String {
        return "Samochod(id=$id, nazwa='$nazwa', rodzaj paliwa=${rodzajPaliwa.label})"
    }
}