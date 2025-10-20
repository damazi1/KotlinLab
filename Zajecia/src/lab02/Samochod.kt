package lab02

class Samochod(override var nazwa: String, var rodzajPaliwa: Int) : Pojazd() {
    override fun toString(): String {
        return "Samochod(id=$id, nazwa='$nazwa')"
    }
}