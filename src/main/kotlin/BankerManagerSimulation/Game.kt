package BankerManagerSimulation

import kotlin.math.roundToInt


// установить модификаторы доступа

class Game(number:Int, round:Int) {
    val round=round
    val numberPlayers=number   //всего игроков
    var activePlayers=numberPlayers  //кол-во игроков минус банкроты
    val players= arrayListOf<Player>()   //список игроков
    var seniorPlayer=1  //старший игрок
    var order=3  //обстановка на рынке

    val levels= mapOf(
        1 to Level( Math.floor(1.0*activePlayers),800,Math.floor(3.0*activePlayers),6500),
        2 to Level(Math.floor(1.5*activePlayers),650,Math.floor(2.5*activePlayers),6000),
        3 to Level(Math.floor(2.0*activePlayers),500,(2.0*activePlayers),5500),
        4 to Level(Math.floor(2.5*activePlayers),400,Math.floor(1.5*activePlayers),5000),
        5 to Level(Math.floor(3.0*activePlayers),300,Math.floor(1.0*activePlayers),4500)
    )
    var level=levels[3]  // уровень цен на рынке

    fun StartGame(){

        for (n in 0..numberPlayers-1) {
            print("Введите имя: ")
            var name = readLine().toString()
            players.add(Player(name))
        }
        for (n in 1..round) {
            CalculationFixedCosts()  //списание постоянных издержек
            TransitionPriceLevel(order)  // определение обстановки на рынке
            println(level?.LevelToString())  //извещение игроков об обстановке на рыынке
            //Заявки на сырье и материалы

            //Производство продукции
            //Продажа продукции
            //Выплата ссудного %
            //Погашение ссуд
            //Получение ссуд
            //Завки на строительство


            //убрать банкротов
            seniorPlayer=InstallSeniorPlayer()
            //конец игры или переход к следующему раунду
            //если конец игры, то определить победителя
        }
        println("Конец игры. Победил игрок $")
    }

    fun CalculationFixedCosts(){
        for (player in players) {
            player.CalcCach()
        }
    }
    fun TransitionPriceLevel(order:Int){
        var options: List<Int> = listOf()
        when(order){
            1 -> options = listOf(1,1,1,1,2,2,2,2,3,3,4,5)
            2 -> options = listOf(1,1,1,2,2,2,2,3,3,3,4,5)
            3 -> options = listOf(1,2,2,2,3,3,3,3,4,4,4,5)
            4 -> options = listOf(1,2,3,3,3,4,4,4,4,5,5,5)
            5 -> options = listOf(1,2,3,3,4,4,4,4,5,5,5,5)
        }
        level=levels[options.random()]
    }



    private fun InstallSeniorPlayer():Int{
        return round*activePlayers+1
    }

}