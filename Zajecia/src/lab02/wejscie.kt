package lab02

val xml_pojazdy = """<pojazdy>
    <samochod>
        <nazwa>
        Honda Civic
        </nazwa>
        <rodzajPaliwa>
        3
        </rodzajPaliwa>
    </samochod>
</pojazdy>
    <motorowka>
        <nazwa>
        Super motorowka
        </nazwa>
        <rodzajPaliwa>
        0
        </rodzajPaliwa>
    </motorowka>
    <samochod>
        <nazwa>
        Mercedes CLK
        </nazwa>
        <rodzajPaliwa>
        5
        </rodzajPaliwa>
    </samochod>
    <rower>
        <nazwa>
        Giant
        </nazwa>
    </rower>
    <hulajnoga>
        <nazwa>
        Najfajniejsza hulajnoga
        </nazwa>
    </hulajnoga>
    """
fun parsePojazdy(xml: String): List<Pojazd> {
    val itemRegex = Regex("<(samochod|motorowka|rower|hulajnoga)>(.*?)</\\1>", RegexOption.DOT_MATCHES_ALL)
    val nameRegex = Regex("<nazwa>\\s*(.*?)\\s*</nazwa>", RegexOption.DOT_MATCHES_ALL)
    val fuelRegex = Regex("<rodzajPaliwa>\\s*(\\d+)\\s*</rodzajPaliwa>", RegexOption.DOT_MATCHES_ALL)

    return itemRegex.findAll(xml).map { m ->
        val tag = m.groupValues[1]
        val content = m.groupValues[2]
        val name = nameRegex.find(content)?.groupValues?.get(1)?.trim() ?: "unknown"
        val fuel = fuelRegex.find(content)?.groupValues?.get(1)?.toIntOrNull() ?: -1

        when (tag) {
            "samochod" -> Samochod(name, fuel)
            "motorowka" -> Motorowka(name, fuel)
            "rower" -> Rower(name)
            "hulajnoga" -> Hulajnoga(name)
            else -> error("unsupported tag: $tag")
        }
    }.toList()
}

private fun Pojazd.formatLine(): String = when (this) {
    is Samochod -> "${id}. ${this::class.simpleName}: ${nazwa}  (${rodzajPaliwa.label}  Stan baku = $aktualnyStanBaku/$pojemnoscBaku)"
    is Motorowka -> "${id}. ${this::class.simpleName}: ${nazwa} (${rodzajPaliwa.label}  Stan baku = $aktualnyStanBaku/$pojemnoscBaku)"
    else -> "${id}. ${this::class.simpleName}: ${nazwa}"
}

fun List<Pojazd>.posortowanePoNazwie(): List<Pojazd> =
    this.sortedBy { it.nazwa.trim().lowercase() }

fun main() {
    val pojazdy = parsePojazdy(xml_pojazdy)
    println(pojazdy)

    val wypozyczalnia = Wypozyczalnia(5)
    val report = pojazdy.map { it.formatLine() }
    println(report.joinToString("\n"))

    println("\nPosortowane po nazwie:")
    val posortowane = pojazdy.posortowanePoNazwie()
    println(posortowane.joinToString("\n") { it.formatLine() })

    println("\nWyporzyczalnie:")
    println(wypozyczalnia.status())
    pojazdy.map { p ->
        if (p is Parkowalny) {
            wypozyczalnia.park(p)
        } else {
            false
        }
    }
    println(wypozyczalnia.status())
    wypozyczalnia.unparkById(1)
    println(wypozyczalnia.status())
    wypozyczalnia.park(pojazdy[0])
    println(wypozyczalnia.status())

    println("\nTankowanie:")
    if (pojazdy[1] is Spaliny) {
        val spaliny = pojazdy[1] as Spaliny
        val success = spaliny.tank(20, RodzajPaliwa.Diesel)
        println("Tankowanie 20L DIESEL dla pojazdu id=${pojazdy[1].id} (${pojazdy[1].nazwa}): $success")
    }

    println(pojazdy[1])
}
