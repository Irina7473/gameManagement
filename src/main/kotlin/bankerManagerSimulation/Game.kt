package bankerManagerSimulation
//ИГРА
// установить модификаторы доступа

class Game(number:Int,
           val round:Int) //всего раундов
{
    private var players= mutableListOf<Player>()   //список игроков
    val numberPlayers=number   //всего игроков
    private var seniorPlayer=1  //старший игрок
    private var order=3  //уровень коньюнктуры рынка
    private val loanPercent = 0.01  //ссудный процент

    // не убираются банкрот для рассчета рынка
    private val marketLevels= mapOf(
        1 to MarketLevel( 1.0,800,3.0,6500),
        2 to MarketLevel(1.5,650,2.5,6000),
        3 to MarketLevel(2.0,500,2.0,5500),
        4 to MarketLevel(2.5,400,1.5,5000),
        5 to MarketLevel(3.0,300,1.0,4500)
    )
    private lateinit var level:MarketLevel

    fun ConnectingPlayers (){
        //набираем игроков
        for (n in 1..numberPlayers) {
            //Подключение нового игрока
            print("Введите имя: ")
            var name = readLine().toString()
            if (name==null) name="безымянный"+n
            if (players.any{ it.name == name }) {
                println("Игрок с таким именем существует, Вам присвоено имя ${name + n}")
                name += n
            }
            players.add(Player(n, name))
        }
    }
    fun StartGame(){
        for (current in 1..round) {
            println("Раунд $current")
            CalculationFixedCosts(current)  //списание постоянных издержек
            level=TransitionPriceLevel(order, players.size)  //определение обстановки на рынке
            println(level.LevelToString())  //извещение игроков об обстановке на рынке
            MaterialsTender(current)//Тендер на продажу материалов
            Manufacture(current)    //Производство продукции и расчеты
            ProductsTender(current)//Тендер на закуп продукции
            InterestPayment(current)    //Выплата ссудного %
            LoanRepayment(current)    //Погашение ссуд
            GettingLoans(current)    //Получение ссуд
            Investments(current)    //инвестиции в строительство фабрик
            FullReport(current)    //ОТЧЕТ за раунд
            if (players.size<=1) GameOver(current)    //если конец игры, то определить победителя СДЕЛАТЬ
            InstallSeniorPlayer(current)  //определение старшего игрока на следующий круг ДОДЕЛАТЬ
        }
        GameOver(round) //СДЕЛАТЬ
    }

    //списание постоянных издержек
    private fun CalculationFixedCosts(current:Int){
        for (player in players) player.CalcFixedCosts(current)
        println("Списаны постоянные издержки")
        BankruptCheck(current)
    }

    //определение обстановки на рынке
    private fun TransitionPriceLevel(order:Int, activePlayers:Int) : MarketLevel{
        var options: List<Int> = listOf()
        when(order){
            1 -> options = listOf(1,1,1,1,2,2,2,2,3,3,4,5)
            2 -> options = listOf(1,1,1,2,2,2,2,3,3,3,4,5)
            3 -> options = listOf(1,2,2,2,3,3,3,3,4,4,4,5)
            4 -> options = listOf(1,2,3,3,3,4,4,4,4,5,5,5)
            5 -> options = listOf(1,2,3,3,4,4,4,4,5,5,5,5)
        }
        this.order =options.random()
        return MarketLevel(marketLevels[this.order]!!, activePlayers)
    }

    //Тендер на продажу материалов
    private fun MaterialsTender(current:Int) {
        println("На рынке можно купить ${level.quantityM} единиц материалов " +
                "по цене не менее ${level.priceM}")
        //сбор заявок на тендер
        var tender= mutableListOf<Tender>()
        for (player in players) {
            println("Игрок ${player.name} сделайте заявку на закуп материалов")
            val request=player.RequestsMaterials()
            if(request.quantity!=0 && request.price >= level.priceM ?: 0) tender.add(request)
        }
        if (tender.isEmpty()) {
            println("Тендер не состоялся")
            return
        }
        println("Заявки игроков на тендере материалов")
        println("name   quantity    price")
        for (request in tender){
             print("Игрок ${players.find{ it.id == request.id }?.name} : ")
             request.TenderToString()
        }

        //сортировка заявок по убыванию цены
        //tender = tender.sortedWith(compareByDescending{ it.price }) as MutableList<Tender>
        tender = tender.sortedByDescending { it.price } as MutableList<Tender>

        //приоритет старшего игрока при равной цене
        tender= PreferenceSeniorPlayer(tender)

        //Размещение закупки у игроков
        tender = level?.let { Purchase(tender, it.quantityM) }!!

        //продажа материалов по результатам тендера
        for (request in tender){
            val player=players.find { it.id == request.id }
            if (player !=null){
                player.material += request.quantity
                player.cash -= request.quantity * request.price
            }
        }
        println("Состоялся тендер на материалы")
        BankruptCheck(current)
    }

    //Тендер на закуп продукции
    private fun ProductsTender(current:Int) {
        println("На рынке можно продать ${level.quantityFP} единиц продукции " +
                "по цене не более ${level.priceFP}")
        //сбор заявок на тендер
        var tender= mutableListOf<Tender>()
        for (player in players) {
            println("Игрок ${player.name} сделайте заявку на продажу продукции")
            val request=player.RequestsProdukts()
            if(request.quantity!=0 && request.price <= level?.priceFP ?: 0) tender.add(request)
        }
        if (tender.isEmpty()) {
            println("Тендер не состоялся")
            return
        }
        println("Заявки игроков на продажу продукции")
        println("name   quantity    price")
        for (request in tender){
            println("${players.find{ it.id == request.id }?.name} - " +
                    "${request.quantity} - ${request.price}")
        }
        //сортировка заявок по возрастанию цены
        tender = tender.sortedBy { it.price } as MutableList<Tender>

        //приоритет старшего игрока при равной цене
        tender= PreferenceSeniorPlayer(tender)

        //Размещение закупки у игроков
        tender = level?.let { Purchase(tender, it.quantityFP) }!!

        //покупка продукции по результатам тендера
        for (request in tender){
            val player=players.find { it.id == request.id }
            if (player !=null){
                player.product -= request.quantity
                player.cash += request.quantity * request.price
            }
        }
        println("Состоялся тендер на продукцию")
        BankruptCheck(current)
    }

    //приоритет старшего игрока при равной цене
    private fun PreferenceSeniorPlayer(tender:MutableList<Tender>):MutableList<Tender>{
        for(i in 0..tender.size-2) {
            if (tender[i].price == tender[i + 1].price) {
                if (players.indexOfFirst { it.id == tender[i].id } >
                    players.indexOfFirst { it.id == tender[i + 1].id })
                {
                    val temp = tender[i]
                    tender[i] = tender[i + 1]
                    tender[i + 1] = temp
                }
            }
        }
        return tender
    }
    //Размещение закупки у игроков
    private fun Purchase(tender:MutableList<Tender>, quantityD:Double): MutableList<Tender> {
        var quantity=quantityD.toInt()
        println("Результат тендера. Размещено $quantity единиц")
        println("name   quantity    price")
        for (request in tender) {
            if(request.quantity>quantity) request.quantity= quantity
            quantity -= request.quantity
            println("${players.find{ it.id == request.id }?.name} - " +
                    "${request.quantity} - ${request.price}")
        }
        return tender
    }

    //Производство продукции и расчеты
    private fun Manufacture(current:Int){
        for (player in players) {
            println("Игрок ${player.name} сделайте заявку на производство")
            player.RequestsManufacture(current)
        }
        println("Произведена продукция")
        BankruptCheck(current)
    }

    //Выплата ссудного %
    private fun InterestPayment(current:Int){
        for (player in players)
            if (player.totalLoans > 0) player.cash -= (player.totalLoans * loanPercent).toInt()
        println("Произведена выплата ссудного %")
        BankruptCheck(current)
    }
    //Погашение ссуд
    private fun LoanRepayment(current:Int){
        for (player in players)
            if (player.totalLoans > 0) {
                for (loan in player.loans)
                    if (loan.term == current) {
                        player.totalLoans -= loan.amount
                        loan.factory.pledge = false
                        player.loans.remove(loan)
                        player.cash -= loan.amount
                    }
            }
        println("Произведен возврат ссуд")
        BankruptCheck(current)
    }
    //Получение ссуд
    private fun GettingLoans(current:Int){
        for (player in players) {
            println("Игрок ${player.name} сделайте заявку на ссуды")
            player.RequestsLoan(current)
        }
        println("Произведена выдача ссуд")
        BankruptCheck(current)
    }

    //инвестиции в строительство фабрик
    private fun Investments(current:Int){
        for (player in players) {
            println("Игрок ${player.name} сделайте заявку на инвестирование")
            player.RequestsBuilding(current)
        }
        println("Произведены инвестиции")
        BankruptCheck(current)
    }

    //проверка банкротов
    private fun BankruptCheck(current:Int){
        val size=players.size-1
        for ( i in 0..size) {
            if (players.get(i).bankrupt == true)  {
                println("Игрок ${players.get(i).name} - банкрот")
                players.remove(players.get(i))  //убираю банкрота
            }
        }
        if (players.isEmpty()) GameOver(current)
        Report(current)
    }
    //определение старшего игрока на следующий круг
    private fun InstallSeniorPlayer(current:Int){
        val temp = players[0]
        for (i in 0..players.size-2) players[i]=players[i+1]
        players[players.size-1]=temp
        seniorPlayer = current%players.size+1
        println("Старший игрок в следующем раунде ${players[0].name}")
    }

    //Отчет
    private fun Report(current:Int){
        println("Отчет по игрокам раунд $current")
        println("id-name-cash-material-product-totalFactory")
        for (player in players) println("${player.id} - ${player.name} - " +
                "${player.cash} - ${player.material} - ${player.product} - ${player.factories.size}")
    }
    private fun FullReport(current:Int) {
        println("Отчет по игрокам раунд $current")
        println("id-name-totalCapital-cash-material-product-totalLoans-totalFactory-workFactory-workAutoFactory-bildFactory")
        for (player in players) {
            val totalFactory = player.factories.size
            var workFactory = 0
            var workAutoFactory = 0
            var bildFactory = 0
            for (factory in player.factories) {
                if (factory.timeStart <= current) {
                    workFactory++
                    if (factory.auto == true) workAutoFactory++
                }
                bildFactory = totalFactory - workFactory
            }

            player.CalcTotalCapital(current, level.priceM, level.priceFP)
            println("${player.id} - ${player.name} - ${player.totalCapital} - ${player.cash} - ${player.material} - ${player.product} - " +
                    "${player.totalLoans} - ${totalFactory} - ${workFactory} - ${workAutoFactory} - ${bildFactory}")
        }
    }

    private fun GameOver(current:Int){
        //Подсчет капиталов оставшихся игроков
        for (player in players) player.CalcTotalCapital(current, level.priceM, level.priceFP)
        //Определение победителя
        players = players.sortedByDescending { it.totalCapital } as MutableList<Player>
        val firstId = players.first().id
        val firstName = players.first().name
        println("Игра закончена. Победил игрок номер $firstId - $firstName")
        println("Капиталы игроков")
        for (player in players) println("${player.id} - ${player.name} - ${player.totalCapital}")
    }

}