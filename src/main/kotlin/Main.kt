import BankerManagerSimulation.Game

fun main() {
    println("Деловая игра Менеджмент")
    //Начало игры. Заявки игроков, подключение, определение количества игроков
    print("Введите количество игроков: ")
    val num= readLine()!!.toInt()
    print("Введите количество раундов: ")
    val round= readLine()!!.toInt()
    var game=Game(num,round)
    game.StartGame()  //запуск игры


}