package lab02

    abstract class Pojazd {
        val id: Int = nextId()
        abstract var nazwa: String
        companion object {
            private var counter: Int = 0
            fun nextId(): Int {
                counter += 1
                return counter
            }
        }
    }