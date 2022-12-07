package BankerManagerSimulation
//ИГРОК
// установить модификаторы доступа
class Player(name:String) {
    val name=name
    var bankrupt=false
    var factories = arrayListOf<Factory>(Factory(), Factory())
    var material=4
    var product=2
    var cash=10000
        set(value) {
            if(value>=0) field = value
            else Bankruptcy()  //учесть банкротство
        }
    var loans = arrayListOf<Loan>()
    var totalLoans=0
    var totalPledge=10000
    var totalCapital=0

    //Константы
    //Постоянные издержки
    val materialFee=300
    val productFee=500
    //Капитальные затраты
    val numberFactory=6
    val invest=5000
    val reconstr=7000

    //Подключение к игре
    fun ConnectionTogameGame(){
        //СДЕЛАТЬ
    }

    fun Bankruptcy(){
        bankrupt=true
        cash=0
        factories.clear()
        material=0
        product=0
        loans.clear()
        totalLoans = 0
        totalPledge=0
        println("Вы обанкротились")
    }

    // расчет постоянных затрат
    fun CalcFixedCosts(current:Int){
        var sum = material*materialFee + product*productFee
        for (factory in factories) {
            //проверяю время на реконструкцию обычной фабрики и меняю ее тип
            if (factory.auto == false && factory.autoStart == current) factory.ChangeFactoryType()
            if (factory.timeStart <= current)  // плачу только за действующие фабрики
            sum += factory.fee
        }
        cash -= sum
    }

    //заявка на закуп материалов
    fun RequestsMaterials():Tender {
        print("Введите количество закупа материалов: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену закупа материалов: " )
        var price= readLine()?.toInt() ?:0
        return Tender(this.name, quantity, price)
    }
    //заявка на производство
    fun RequestsManufacture(current:Int) {
        print("Введите количество продукции на производство на фабриках, не более $material: ")
        var input= readLine()?.toInt() ?:0
        if (input <= 0) return
        var quantity=material   //max кол-во для производства
        if (input <=  material) quantity=input

        //Размещение заказа на фабриках
        for (factory in factories){
            if (quantity>0) {
                if (factory.timeStart <= current)  //проверяю время на запуск построенной фабрики
                {
                    //на этой фабрике можно производить продукцию
                    var produced = factory.power  //max кол-во для производства на этой фабрике
                    if (quantity < factory.power) produced=quantity
                    println("Сколько продукции хотите произвести на фабрике, но не более $produced")
                    input = readLine()?.toInt()!!
                    if (input > 0) {  //размещаю производство на фабрике
                        if (input < produced) produced = input
                        var costs = factory.costs
                        if (factory.auto==true && produced<factory.power) costs= costs/3*2
                        cash -= produced*costs
                        if (bankrupt==true) return
                        material -= produced
                        product += produced
                        quantity -= produced  //уменьшаю кол-во к размещению на производство
                    }
                }
            }
        }
        //Рассчитать переменные расходы
    }
    //заявка на продажу продукции
    fun RequestsProdukts():Tender {
        print("Введите количество продукции на продажу, не более $product: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену продажи продукции: " )
        var price= readLine()?.toInt() ?:0
        if (quantity<=product) return Tender(this.name, quantity, price)
        else return Tender(this.name, product, price)
    }
    //заявка на выдачу ссуды
    fun RequestsLoan(current:Int) {
        var freeLoan = totalPledge/2 - totalLoans  //доступная ссуда
        if (freeLoan > 0){
            println("Вам доступны ссуды на сумму $freeLoan")
            println("Ведите общую сумму ссуд")
            var input = readLine()?.toInt()!!
            if (input<=0) return
            println("Ведите сумму ссуды для каждой фабрики")
            for (i in factories.indices) {
                if (factories[i].pledge == false)  //фабрика не в залоге
                {
                    var axe = factories[i].buildingCost  //сумма ссуды для фабрики
                    if (axe >= freeLoan) axe=freeLoan
                    print("Под фабрику номер $i можно взять ссуду на сумму до $axe")
                    input = readLine()?.toInt()!!
                    if (input <=axe && input >0) {
                        loans.add(Loan(factories[i], current))
                        factories[i].pledge=true
                        totalLoans += input
                        freeLoan -=input
                        cash += input
                    }
                    else print("Запрошена неверная сумма")
                }
            }
        }
        else println("Вам недоступны ссуды")
    }
    //инвестиции в строительство фабрик
    fun RequestsBuilding(current:Int){
        //оплата 2 части перед запуском строящихся и реконструируемых фабрик
        for (factory in factories){
            if (factory.timeStart - current == 1) cash -= factory.buildingCost/2
            if (factory.autoStart - current == 1) cash -= reconstr/2
        }
        if (bankrupt==true) return

        if (cash < invest){
            println("Недостаточно средств для строительства и реконструкции фабрик")
            return
        }
        var input =0
        //Строительство новых фабрик
        var freeBuild = numberFactory- factories.size  //доступно для строительства
        if (freeBuild > 0) {
            println("Вы можете построить $freeBuild фабрик на сумму не более $cash")
            println("Введите желаемое количество фабрик для постройки")
            input = readLine()?.toInt()!!
            if (input <= 0) return
            for (n in 1..freeBuild) {
                if (cash < invest) {
                    println("Недостаточно средств для строительства фабрики")
                    return
                }
                println("Автоматизированная фабрика? (Стоимость строительства автоматизированной 10000$, обычной 5000$) Да-1, нет - 0")
                input = readLine()?.toInt()!!
                var auto = false
                if (input == 1) auto = true
                val factory = Factory(current, auto)
                if (factory.buildingCost/2 <= cash) {
                    cash -= factory.buildingCost/2
                    if (bankrupt==true) return
                    freeBuild--
                    factories.add(factory)
                } else println("Недостаточно средств для строительства фабрики")
            }
        }
        else println("Вам недоступно строительство фабрик")

        //Реконструкция фабрик
        println("Хотите автоматизировать имеющиеся фабрики? Да-1, нет - 0")
        input = readLine()?.toInt()!!
        if (input <= 0) return
        for (i in factories.indices) {
            if (factories[i].auto == false) {
                if (cash < reconstr/2) {
                    println("Недостаточно средств для реконструкции фабрики")
                    return
                }
                print("Хотите автоматизировать фабрику номер $i? Да-1, нет - 0")
                input = readLine()?.toInt()!!
                if (input == 1) {
                    cash -= reconstr/2
                    if (bankrupt==true) return
                    factories[i].autoStart = current + 9
                }
            }
        }
    }

    //определение капитала игрока
    fun CalcTotalCapital(current:Int, priceM:Int, priceFP:Int){
        totalCapital += cash        //сумма наличных
        totalCapital += material*priceM  // стоимости сырья (по мин текущей цене)
        totalCapital += product*priceFP  // стоимости ГП (по макс текущей цене)
        for (factory in factories) {
            //Сумма стоимости всех фабрик (по цене строительства),
            totalCapital += factory.buildingCost
            // Минус сумма предстоящих расходов начатому строительству
            if (factory.timeStart > current) totalCapital -= factory.buildingCost/2
        }
        totalCapital -= totalLoans      // Минус сумма ссуд
    }
}