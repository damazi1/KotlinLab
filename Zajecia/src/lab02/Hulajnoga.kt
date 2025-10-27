package lab02

class Hulajnoga(override var nazwa: String) : Pojazd() {
    override fun toString(): String {
        return "Hulajnoga(id=$id, nazwa='$nazwa')"
    }
}