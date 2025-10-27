package lab02

class Samochod(override var nazwa: String, rodzajPaliwaId: Int) : Pojazd(), Parkowalny, Spaliny {
    var rodzajPaliwa: RodzajPaliwa = RodzajPaliwa.fromId(rodzajPaliwaId)
    var pojemnoscBaku: Int = 50
    var aktualnyStanBaku: Int = 0
    override fun tank(ilosc: Int, rodzaj: RodzajPaliwa): Boolean {
        if (this.rodzajPaliwa.label != rodzaj.label) {
            return false
        }
        if (aktualnyStanBaku + ilosc > pojemnoscBaku) {
            return false
        }
        aktualnyStanBaku += ilosc
        return true
    }

    override fun toString(): String {
        return "Samochod(id=$id, nazwa='$nazwa', rodzaj paliwa=${rodzajPaliwa.label}, Stan baku = $aktualnyStanBaku/$pojemnoscBaku)"
    }

    override fun park(garaz: Garaz): Boolean {
        return garaz.zaparkuj(this)
    }

    override fun unpark(garaz: Garaz): Boolean {
        return garaz.wyjmijById(this.id)
    }
}