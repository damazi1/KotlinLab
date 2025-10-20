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
</pojazdy>"""
/*
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
 */
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
            "motorowka" -> motorowka(name, fuel)
            "rower" -> rower(name)
            "hulajnoga" -> hulajnoga(name)
            else -> throw IllegalStateException("unsupported tag: $tag")
        }
    }.toList()
}

fun main() {
    val lista = parsePojazdy(xml_pojazdy) // uses existing `xml_pojazdy`
    println(lista.joinToString("\n") { p ->
        when (p) {
            is RodzajPaliwa -> "${p::class.simpleName}: ${p.nazwa} (paliwa=${RodzajePaliwa.toReadable(p.RodzajPaliwa)})"
            else -> "${p::class.simpleName}: ${p.nazwa}"
        }
    })
    val samochod = Samochod("Nazwa samochodu", 1)
    println(samochod.toString())
}
