package lab01.lab1

class Lampka {
    var wlaczona: Boolean = false
    var intensity: Int = 0
    var bulb: Zarowka = Zarowka()
    fun wlacz() {
        wlaczona = true
    }

    fun wylacz() {
        wlaczona = false
    }
    fun zwiekszIntensywnosc() {
        if (intensity < 10) {
            bulb.turnOn();
            intensity++
        } else {
            bulb.isWorking = false;
        }
    }
    fun zmniejszIntensywnosc() {
        if (intensity > 0) {
            intensity--
            if (intensity == 0) {
                bulb.isOn = false;
            }
        } else {
            bulb.isOn = false;
        }
    }

    fun czyWlaczona(): Boolean {
        return wlaczona
    }
    fun wymienZarowke(nowaZarowka: Zarowka): Boolean {
        if (isOn()) return false
        bulb = nowaZarowka
        return true;
    }
    fun isOn(): Boolean {
        return bulb.isOn
    }
    fun isWorking(): Boolean {
        return bulb.isWorking
    }
}