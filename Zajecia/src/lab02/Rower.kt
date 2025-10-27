package lab02

class Rower (override var nazwa: String): Pojazd(), Parkowalny {
    override fun toString(): String {
        return "Rower(id=$id, nazwa='$nazwa')"
    }

    override fun park(garaz: Garaz): Boolean {
        return garaz.zaparkuj(this)
    }

    override fun unpark(garaz: Garaz): Boolean {
        return garaz.wyjmijById(this.id)
    }
}