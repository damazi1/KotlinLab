package lab02

import Wypozyczalnia

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

fun main() {
    val pojazdy = parsePojazdy(xml_pojazdy)
    val wypozyczalnia = Wypozyczalnia(5)
    val report = pojazdy.map { p ->
        val idx = (0 until wypozyczalnia.getGaragesCount()).firstOrNull { i -> wypozyczalnia.get(i) == null }
        if (idx != null && wypozyczalnia.park(p, idx)) {
            when (p) {
                is Samochod -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa: ${p.rodzajPaliwa.label}) -> parked at #$idx"
                is Motorowka -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa: ${p.rodzajPaliwa.label}) -> parked at #$idx"
                else -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} -> parked at #$idx"
            }
        } else {
            when (p) {
                is Samochod -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa: ${p.rodzajPaliwa.label}) -> no space"
                is Motorowka -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa: ${p.rodzajPaliwa.label}) -> no space"
                else -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} -> no space"
            }
        }
    }

    println(report.joinToString("\n"))
    println("\nGarage status:")
    println(wypozyczalnia.status())
    wypozyczalnia.remove(3);
    println(wypozyczalnia.status())
    // now this will print "already parked" if the vehicle is still parked elsewhere
    val last = pojazdy.get(3);
    var parkedAt = wypozyczalnia.findGarageIndex(last)
    if (parkedAt != null) {
        println("${last.id}. ${last::class.simpleName}: ${last.nazwa} -> already parked at #$parkedAt")
    } else {
        println(wypozyczalnia.tryParkOrReport(last,2))
        println(wypozyczalnia.status())
    }

//    println(pojazdy.joinToString("\n") { p ->
//        when (p) {
//            is Samochod -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa: ${p.rodzajPaliwa.label})"
//            is Motorowka -> "${p.id}. ${p::class.simpleName}: ${p.nazwa} (rodzaj paliwa ${p.rodzajPaliwa.label})"
//            else -> "${p.id}. ${p::class.simpleName}: ${p.nazwa}"
//        }
//    })

}
