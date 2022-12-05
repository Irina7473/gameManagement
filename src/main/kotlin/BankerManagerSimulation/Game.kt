package BankerManagerSimulation


// установить модификаторы доступа

class Game(number:Int, round:Int) {
    val round=round
    val numberPlayers=number   //всего игроков
    var activePlayers=numberPlayers  //кол-во игроков минус банкроты
    val players= arrayListOf<Player>()   //список игроков
    var seniorPlayer=1  //старший игрок
    var order=3  //обстановка на рынке

    val marketLevels= mapOf(
        1 to MarketLevel( Math.floor(1.0*activePlayers),800,Math.floor(3.0*activePlayers),6500),
        2 to MarketLevel(Math.floor(1.5*activePlayers),650,Math.floor(2.5*activePlayers),6000),
        3 to MarketLevel(Math.floor(2.0*activePlayers),500,Math.floor(2.0*activePlayers),5500),
        4 to MarketLevel(Math.floor(2.5*activePlayers),400,Math.floor(1.5*activePlayers),5000),
        5 to MarketLevel(Math.floor(3.0*activePlayers),300,Math.floor(1.0*activePlayers),4500)
    )
    var level=marketLevels[3]  // уровень цен на рынке

    fun StartGame(){

        for (n in 0..numberPlayers-1) {
            print("Введите имя: ")
            var name = readLine().toString()
            players.add(Player(name))
        }
        for (n in 1..round) {
            CalculationFixedCosts()  //списание постоянных издержек
            TransitionPriceLevel(order)  // определение обстановки на рынке
            println(level?.LevelToString())  //извещение игроков об обстановке на рынке
            var tenderM=MaterialsTender()//Тендер на сырье и материалы
                //Произвести расчеты по закупу
                //Производство продукции и расчеты
            var tenderP=ProductsTender()//Продажа продукции
                //Произвести расчеты по продажам
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
        level=marketLevels[options.random()]
    }

    fun MaterialsTender(): ArrayList<Tender>? {
        var tender= arrayListOf<Tender>()
        for (player in players) {
            var requests=player.RequestsMaterials()
            if(requests.quantity!=0 && requests.price > level?.priceM ?: 0) tender.add(requests)
        }
        tender= tender.sortedByDescending { it.price } as ArrayList<Tender>
        //Добавить сюда приоритет старшего игрока
        return level?.let { Purchase(tender, it.quantityM) }
    }

    fun ProductsTender(): ArrayList<Tender>? {
        var tender= arrayListOf<Tender>()
        for (player in players) {
            var requests=player.RequestsProdukts()
            if(requests.quantity!=0 && requests.price < level?.priceFP ?: 0) tender.add(requests)
        }
        tender= tender.sortedBy { it.price } as ArrayList<Tender>
        //Добавить сюда приоритет старшего игрока
        return level?.let { Purchase(tender, it.quantityFP) }
    }
    fun Purchase(tender:ArrayList<Tender>, quantity:Double): ArrayList<Tender> {
        var quantity=quantity.toInt()
        for (requests in tender) {
            if(requests.quantity>quantity) requests.quantity= quantity
            else quantity -= requests.quantity
        }
        return tender
    }

    private fun InstallSeniorPlayer():Int{
        return round*activePlayers+1
    }

}