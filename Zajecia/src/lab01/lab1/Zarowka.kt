package lab01.lab1

class Zarowka {
    var isOn: Boolean = false
    var isWorking: Boolean = true

    fun turnOn() {
        if (czyDziala()) {
            isOn = true
        }
    }
    fun turnOff() {
        isOn = false
    }
    fun czyWlaczona(): Boolean {
        return isOn
    }
    fun czyDziala(): Boolean {
        return isWorking
    }
}