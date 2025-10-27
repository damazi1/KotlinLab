package lab02

class Rower (override var nazwa: String): Pojazd() {
    override fun toString(): String {
        return "Rower(id=$id, nazwa='$nazwa')"
    }
}