package lab02

sealed interface RodzajPaliwa {
    val label: String

    object Diesel   : RodzajPaliwa { override val label = "Diesel" }
    object Benzyna  : RodzajPaliwa { override val label = "Benzyna" }
    object LPG      : RodzajPaliwa { override val label = "LPG" }
    object CNG      : RodzajPaliwa { override val label = "CNG" }
    object Nieznane : RodzajPaliwa { override val label = "Nieznane" }

    companion object {

        fun fromId(id: Int): RodzajPaliwa = when (decodeMask(id)) {
            4 -> Diesel
            5 -> Benzyna
            7 -> LPG
            1 -> CNG
            else -> Nieznane
        }

        fun decodeMask(mask: Int): Int {
            return mask xor 4
        }
    }
}