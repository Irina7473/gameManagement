import BankerManagerSimulation.Factory
import BankerManagerSimulation.Game

fun main() {
    println("Деловая игра Менеджмент")
    print("Введите количество игроков: ")
    val num= readLine()!!.toInt()
    print("Введите количество раундов: ")
    val round= readLine()!!.toInt()
    var game=Game(num,round)
    game.ConnectingPlayers()
    game.StartGame()

}

// ДОДЕЛАТЬ
/*
*  взаимодействие игроков и банкира

   Любой игрок в любой момент может узнать состояние дел любого другого игрока —
   его капитал, наличные, взятые ссуды, все, что касается готовой продукции, имеющихся и строящихся фабрик.
   Во время торгов игроки ничего не знают о заявках, сделанных другими, но, как только банк собрал все заявки,
   они обнародуются и количество купленных или проданных каждым игроком единиц становится известно всем.

Задача состоит из двух частей.
*Первая — программу-банкир.
* Эта программа должна полностью контролировать игру: устанавливать цены, закупать продукцию и продавать сырье, проводить торги, вести учет и т. д.
* Эта программа должна в соответствующие моменты опрашивать игроков и добиваться соблюдения ими всех правил.
* Банкир защищает от несанкционированного доступа всю информацию, как свою, так и чужую, касающуюся учета и состояния дел отдельных игроков.
* Программа-банкир периодически (например, ежемесячно) выдает сводный финансовый отчет, понятный и эстетически приемлемый.

Вторая часть задачи — написать программы поведения игроков.
* Каждая программа-игрок должна быть в состоянии отвечать на любые запросы программы-банкира по ходу игры.
* Если для моделирования используется диалоговая система, реализуйте одну из программ-игроков таким образом,
* чтобы она просто передавала свои запросы игроку-человеку, находящемуся за терминалом.
* Такая программа должна уметь отвечать на запросы человека о состоянии игры.

После того как будет написано несколько программ-игроков, их надо объединить с программой-банкиром, чтобы получилась полная игровая система.
 */