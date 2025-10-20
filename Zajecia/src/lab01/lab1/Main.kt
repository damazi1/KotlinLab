package lab01.lab1

fun main() {
    println("--------------- ZADANIE 1 ---------------")
    val z = zad1()
    z.hw()
    println("--------------- ZADANIE 2 ---------------")
    val lampka = Lampka()
    val zarowka = Zarowka()
    println("Czy lampka jest włączona? ${lampka.czyWlaczona()}")
    println("Czy żarówka jest włączona? ${lampka.isOn()}")
    println("Czy żarówka jest sprawna? ${lampka.isWorking()}")
    lampka.wlacz()
    println("Czy lampka jest włączona? ${lampka.czyWlaczona()}")
    println("Czy żarówka jest włączona? ${lampka.isOn()}")
    lampka.zwiekszIntensywnosc()
    println("Intensywność lampki: ${lampka.intensity}")
    println("Czy żarówka jest włączona? ${lampka.isOn()}")
    for (i in 1..10) {
        lampka.zwiekszIntensywnosc()
        println("Intensywność lampki: ${lampka.intensity}")
        println("Czy żarówka jest włączona? ${lampka.isOn()}")
        println("Czy żarówka jest sprawna? ${lampka.isWorking()}")
    }
    println("Próba wymiany żarówki na nową: ${lampka.wymienZarowke(Zarowka())}")
    println("Czy żarówka jest włączona? ${lampka.isOn()}")
    println("Czy żarówka jest sprawna? ${lampka.isWorking()}")
    for (i in 1..10) {
        lampka.zmniejszIntensywnosc()
        println("Intensywność lampki: ${lampka.intensity}")
    }
    lampka.wylacz()
    println("Próba wymiany żarówki na nową: ${lampka.wymienZarowke(Zarowka())}")
    println("Czy żarówka jest włączona? ${lampka.isOn()}")
    println("Czy żarówka jest sprawna? ${lampka.isWorking()}")
}
