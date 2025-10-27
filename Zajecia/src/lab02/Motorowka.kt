package lab02

class Motorowka(override var nazwa: String, rodzajPaliwaId: Int) : Pojazd() {
    var rodzajPaliwa: RodzajPaliwa = RodzajPaliwa.fromId(rodzajPaliwaId)
}