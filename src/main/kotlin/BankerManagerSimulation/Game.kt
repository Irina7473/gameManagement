package BankerManagerSimulation
//ИГРА
// установить модификаторы доступа dtplt

class Game(number:Int, round:Int) {
    val round=round
    val numberPlayers=number   //всего игроков
    var activePlayers=numberPlayers  //кол-во игроков минус банкроты
    var players= arrayListOf<Player>()   //список игроков
    var seniorPlayer=1  //старший игрок
    //var priority= arrayOf<Int>()
    var order=3  //уровень коньюнктуры рынка
    val loanPercent = 0.01  //ссудный процент

    val marketLevels= mapOf(
        1 to MarketLevel( Math.floor(1.0*activePlayers),800,Math.floor(3.0*activePlayers),6500),
        2 to MarketLevel(Math.floor(1.5*activePlayers),650,Math.floor(2.5*activePlayers),6000),
        3 to MarketLevel(Math.floor(2.0*activePlayers),500,Math.floor(2.0*activePlayers),5500),
        4 to MarketLevel(Math.floor(2.5*activePlayers),400,Math.floor(1.5*activePlayers),5000),
        5 to MarketLevel(Math.floor(3.0*activePlayers),300,Math.floor(1.0*activePlayers),4500)
    )
    var level=marketLevels[order]  // уровень цен на рынке

    fun ConnectingPlayers (){
        //ждем игроков и подключаем их - СДЕЛАТЬ
        for (n in 1..numberPlayers) {
            //Подключение нового игрока
            print("Введите имя: ")
            var name = readLine().toString()
            if (name==null) players.add(Player(n))
            else players.add(Player(n, name))
            //priority[n-1]=n
        }
    }
    fun StartGame(){
        for (current in 1..round) {
            CalculationFixedCosts(current)  //списание постоянных издержек
            level = TransitionPriceLevel(order)  //определение обстановки на рынке
            println(level?.LevelToString())  //извещение игроков об обстановке на рынке
            MaterialsTender(current)//Тендер на продажу материалов
            Manufacture(current)    //Производство продукции и расчеты
            ProductsTender()//Тендер на закуп продукции
            InterestPayment(current)    //Выплата ссудного %
            LoanRepayment(current)    //Погашение ссуд
            GettingLoans(current)    //Получение ссуд
            Investments(current)    //инвестиции в строительство фабрик
                            //ОТЧЕТ за раунд
            activePlayers = players.size
            if (activePlayers<=1) GameOver(current)    //если конец игры, то определить победителя СДЕЛАТЬ
            InstallSeniorPlayer(current)  //определение старшего игрока на следующий круг ДОДЕЛАТЬ
        }
        GameOver(round) //СДЕЛАТЬ
    }

    //списание постоянных издержек
    fun CalculationFixedCosts(current:Int){
        for (player in players) {
            player.CalcFixedCosts(current)
            if (player.bankrupt == true)  players.remove(player) //убираю банкрота
        }
        if (players.isEmpty()) GameOver(current)
    }
    //определение обстановки на рынке
    fun TransitionPriceLevel(order:Int): MarketLevel? {
        var options: List<Int> = listOf()
        when(order){
            1 -> options = listOf(1,1,1,1,2,2,2,2,3,3,4,5)
            2 -> options = listOf(1,1,1,2,2,2,2,3,3,3,4,5)
            3 -> options = listOf(1,2,2,2,3,3,3,3,4,4,4,5)
            4 -> options = listOf(1,2,3,3,3,4,4,4,4,5,5,5)
            5 -> options = listOf(1,2,3,3,4,4,4,4,5,5,5,5)
        }
        this.order =options.random()
        return marketLevels[order]
    }
    //Тендер на продажу материалов
    fun MaterialsTender(current:Int) {
        //сбор заявок на тендер
        var tender= arrayListOf<Tender>()
        for (player in players) {
            var request=player.RequestsMaterials()
            if(request.quantity!=0 && request.price > level?.priceM ?: 0) tender.add(request)
        }
        //сортировка заявок по убыванию цены
        tender= tender.sortedByDescending { it.price } as ArrayList<Tender>
        //приоритет старшего игрока при равной цене
        tender= PreferenceSeniorPlayer(tender)
        //Размещение закупки у игроков
        tender = level?.let { Purchase(tender, it.quantityM) }!!
        //продажа материалов по результатам тендера
        for (request in tender){
            var player=players.find { it.id == request.id }
            if (player !=null){
                player.material += request.quantity
                player.cash -= request.quantity * request.price
                if (player.bankrupt == true)  players.remove(player) //убираю банкрота
            }
        }
        if (players.isEmpty()) GameOver(current)
    }

    //Тендер на закуп продукции
    fun ProductsTender() {
        //сбор заявок на тендер
        var tender= arrayListOf<Tender>()
        for (player in players) {
            var request=player.RequestsProdukts()
            if(request.quantity!=0 && request.price < level?.priceFP ?: 0) tender.add(request)
        }
        //сортировка заявок по возрастанию цены
        tender= tender.sortedBy { it.price } as ArrayList<Tender>
        //приоритет старшего игрока при равной цене
        tender= PreferenceSeniorPlayer(tender)
        //Размещение закупки у игроков
        tender = level?.let { Purchase(tender, it.quantityFP) }!!
        //покупка продукции по результатам тендера
        for (request in tender){
            var player=players.find { it.id == request.id }
            if (player !=null){
                player.product -= request.quantity
                player.cash += request.quantity * request.price
            }
        }
    }
    //приоритет старшего игрока при равной цене
    private fun PreferenceSeniorPlayer(tender:ArrayList<Tender>):ArrayList<Tender>{
        for(i in 0..tender.size-2){
            if(tender[i].price==tender[i+1].price){
                if(players.indexOfFirst { it.id ==tender[i].id } > players.indexOfFirst { it.id ==tender[i+1].id })
                {
                    var temp=tender[i]
                    tender[i]=tender[i+1]
                    tender[i+1]=temp
                }
            }
        }
        return tender
    }
    //Размещение закупки у игроков
    fun Purchase(tender:ArrayList<Tender>, quantity:Double): ArrayList<Tender> {
        var quantity=quantity.toInt()
        for (requests in tender) {
            if(requests.quantity>quantity) requests.quantity= quantity
            else quantity -= requests.quantity
        }
        return tender
    }

    //Производство продукции и расчеты
    fun Manufacture(current:Int){
        for (player in players) {
            player.RequestsManufacture(current)
            if (player.bankrupt == true)  players.remove(player) //убираю банкрота
        }
        if (players.isEmpty()) GameOver(current)
    }

    //Выплата ссудного %
    fun InterestPayment(current:Int){
        for (player in players) {
            if (player.totalLoans > 0) {
                player.cash -= (player.totalLoans * loanPercent).toInt()
                if (player.bankrupt == true)  players.remove(player) //убираю банкрота
            }
        }
        if (players.isEmpty()) GameOver(current)
    }
    //Погашение ссуд
    fun LoanRepayment(current:Int){
        for (player in players)
            if (player.totalLoans > 0) {
                for (loan in player.loans)
                    if (loan.term == current) {
                        //проверка на банкротство
                        if (player.bankrupt == true) break
                        player.totalLoans -= loan.amount
                        loan.factory.pledge = false
                        player.loans.remove(loan)
                        player.cash -= loan.amount
                    }
                if (player.bankrupt == true)  players.remove(player) //убираю банкрота
            }
        if (players.isEmpty()) GameOver(current)
    }
    //Получение ссуд
    fun GettingLoans(current:Int){
        for (player in players) {
            player.RequestsLoan(current)
        }
    }
    //инвестиции в строительство фабрик
    fun Investments(current:Int){
        for (player in players) {
            player.RequestsBuilding(current)
            if (player.bankrupt == true)  players.remove(player) //убираю банкрота
        }
        if (players.isEmpty()) GameOver(current)
    }

    //определение старшего игрока на следующий круг ДОДЕЛАТЬ
    private fun InstallSeniorPlayer(current:Int){
        var temp = players[0]
        for (i in 0..players.size-2) players[i]=players[i+1]
        players[players.size-1]=temp
        seniorPlayer = current%activePlayers+1
    }

    //Отчет СДЕЛАТЬ
    fun Report(){

    }

    fun GameOver(current:Int){
        //Подсчет капиталов оставшихся игроков
        for (player in players) player.CalcTotalCapital(current, level!!.priceM, level!!.priceFP)
        //Определение победителя
        players= players.sortedByDescending { it.totalCapital } as ArrayList<Player>
        //отчет за игру  СДЕЛАТЬ
        val firstId = players.first().id
        val firstName = players.first().name
        println("Игра закончена. Победил игрок номер $firstId - $firstName")
    }

}