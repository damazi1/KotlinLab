package lab02

class Motorowka(override var nazwa: String, rodzajPaliwaId: Int) : Pojazd(), Spaliny {
    var rodzajPaliwa: RodzajPaliwa = RodzajPaliwa.fromId(rodzajPaliwaId)
    var pojemnoscBaku: Int = 25
    var aktualnyStanBaku: Int = 0
    override fun tank(ilosc: Int, rodzaj: RodzajPaliwa): Boolean {
        if (this.rodzajPaliwa != rodzaj) {
            return false
        }
        if (aktualnyStanBaku + ilosc > pojemnoscBaku) {
            return false
        }
        aktualnyStanBaku += ilosc
        return true
    }
    override fun toString(): String {
        return "Motorowka(id=$id, nazwa='$nazwa', rodzaj paliwa=${rodzajPaliwa.label}, Stan baku = $aktualnyStanBaku/$pojemnoscBaku)"
    }
}