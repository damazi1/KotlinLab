// kotlin
    package lab02

    sealed interface RodzajPaliwa {
        val mask: Int
        val label: String

        object Nieznane : RodzajPaliwa { override val mask = 0;           override val label = "Nieznane" }
        object Benzyna  : RodzajPaliwa { override val mask = 1 shl 1;      override val label = "Benzyna" }
        object Diesel   : RodzajPaliwa { override val mask = 1 shl 2;      override val label = "Diesel" }
        object LPG      : RodzajPaliwa { override val mask = 1 shl 3;      override val label = "LPG" }
        object CNG      : RodzajPaliwa { override val mask = 1 shl 4;      override val label = "CNG" }

        companion object {
            private val ALL = listOf(Benzyna, Diesel, LPG, CNG)

            fun fromId(id: Int): RodzajPaliwa = when (id) {
                1 -> Benzyna
                2 -> Diesel
                3 -> LPG
                4 -> CNG
                else -> Nieznane
            }

        }
    }