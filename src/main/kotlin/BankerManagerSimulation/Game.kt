package BankerManagerSimulation
//ИГРА
// установить модификаторы доступа dtplt

class Game(number:Int, round:Int) {
    val round=round
    val numberPlayers=number   //всего игроков
    var activePlayers=numberPlayers  //кол-во игроков минус банкроты
    val players= arrayListOf<Player>()   //список игроков
    var seniorPlayer=1  //старший игрок
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

    fun StartGame(){

        for (n in 1..numberPlayers-1) {
            //Подключение нового игрока
            print("Введите имя: ")
            var name = readLine().toString()
            players.add(Player(name))
        }
        for (current in 1..round) {
            CalculationFixedCosts()  //списание постоянных издержек
            level = TransitionPriceLevel(order)  //определение обстановки на рынке
            println(level?.LevelToString())  //извещение игроков об обстановке на рынке
            var tenderM=MaterialsTender()//Тендер на продажу материалов
                //Произвести расчеты по закупу материалов СДЕЛАТЬ
            Manufacture(current)    //Производство продукции и расчеты
            var tenderP=ProductsTender()//Тендер на закуп продукции
                //Произвести расчеты по продажам продукции СДЕЛАТЬ
            InterestPayment()    //Выплата ссудного %
            LoanRepayment(current)    //Погашение ссуд
            GettingLoans(current)    //Получение ссуд
            Investments(current)    //инвестиции в строительство фабрик
                //убрать банкротов СДЕЛАТЬ
                seniorPlayer=InstallSeniorPlayer(current)  //ДОДЕЛАТЬ
                //конец игры или переход к следующему раунду СДЕЛАТЬ
                //если конец игры, то определить победителя СДЕЛАТЬ
        }
        println("Конец игры. Победил игрок $")
    }

    //списание постоянных издержек
    fun CalculationFixedCosts(){
        for (player in players) {
            player.CalcFixedCosts()
        }
        //учесть банкротство
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
    //Тендер на закуп продукции
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
        }
    }

    //Выплата ссудного %
    fun InterestPayment(){
        for (player in players) {
            if (player.totalLoans > 0) {
                player.cash -= (player.totalLoans * loanPercent).toInt()
                // Проверка на банкротство
            }
        }
    }
    //Погашение ссуд
    fun LoanRepayment(current:Int){
        for (player in players)
            if (player.totalLoans > 0) {
                for (loan in player.loans)
                    if (loan.term == current) {
                        //проверка на банкротство
                        player.cash -= loan.amount
                        player.totalLoans -= loan.amount
                        loan.factory.pledge = false
                        player.loans.remove(loan)
                    }
            }
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
        }
    }

    //проверить и доработать
    private fun InstallSeniorPlayer(current:Int):Int{
        return current*activePlayers+1
    }

}