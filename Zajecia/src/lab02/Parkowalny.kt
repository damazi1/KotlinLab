package lab02

interface Parkowalny {
    fun park(garaz: Garaz): Boolean
    fun unpark(garaz: Garaz): Boolean
}