package bankerManagerSimulation
//ИГРОК
// установить модификаторы доступа
class Player(val id:Int, val name:String = "безымянный") {
    var bankrupt=false  //признак банкрота
    var factories = arrayListOf(Factory(), Factory())
    var material=4      //материалы на складе
    var product=2       //продукция на складе
    var cash=10000      //наличные
        set(value) {
            if(value>=0) field = value
            else Bankruptcy()  //банкротство
        }
    var loans = arrayListOf<Loan>()
    var totalLoans=0       //общая сумма ссуд
    var totalCapital=0     //капитал игрока

    //Константы
    private val materialFee=300  //плата за единицу сырья на складе
    private val productFee=500   //плата за единицу продукции на складе
    private val numberFactory=6  // max количество фабрик
    private val invest=5000     //min инвестиции в строительство
    private val reconstr=7000  //min инвестиции в реконструкцию

    //Подключение к игре для сетевой версии
    fun ConnectionTogameGame(){
        //СДЕЛАТЬ запрос на подключение для сетевой версии
    }

    // банкротство
    private fun Bankruptcy(){
        bankrupt=true
        cash=0
        factories.clear()
        material=0
        product=0
        loans.clear()
        totalLoans = 0
        println("$name, Вы обанкротились")
    }

    // расчет постоянных затрат
    fun CalcFixedCosts(current:Int){
        var sum = material*materialFee + product*productFee
        for (factory in factories) {
            //проверяю время на реконструкцию обычной фабрики и меняю ее тип
            if (!factory.auto && factory.autoStart == current) factory.ChangeFactoryType()
            if (factory.timeStart <= current)  // плачу только за действующие фабрики
            sum += factory.fee
        }
        cash -= sum
    }

    //заявка на закуп материалов
    fun RequestsMaterials():Tender {
        print("Введите количество закупа материалов: ")
        val quantity= readLine()?.toInt() ?:0
        print("Введите цену закупа материалов: " )
        val price= readLine()?.toInt() ?:0
        return Tender(this.id, quantity, price)
    }

    //заявка на производство
    fun RequestsManufacture(current:Int) {
        //проверка произв.мощностей
        var power = 0
        for (factory in factories)
            if (factory.timeStart <= current) power += factory.power
        if (power > material) power=material
        print("Введите количество продукции на производство на фабриках, не более $power: ")
        var input= readLine()?.toInt() ?:0
        if (input <= 0) return
        if (input <=  power) power=input

        //Размещение заказа на фабриках
        for (factory in factories){
            if (power>0) {
                if (factory.timeStart <= current)  //проверяю время на запуск построенной фабрики
                {
                    //на этой фабрике можно производить продукцию
                    var produced = factory.power  //max кол-во для производства на этой фабрике
                    if (power < factory.power) produced=power
                    println("Сколько продукции хотите произвести на фабрике, но не более $produced")
                    input = readLine()?.toInt()!!
                    if (input > 0) {  //размещаю производство на фабрике
                        if (input < produced) produced = input
                        var costs = factory.costs
                        if (factory.auto && produced<factory.power) costs= costs/3*2
                        cash -= produced*costs
                        if (bankrupt) return
                        material -= produced
                        product += produced
                        power -= produced  //уменьшаю кол-во к размещению на производство
                    }
                }
            }
        }
    }

    //заявка на продажу продукции
    fun RequestsProdukts():Tender {
        print("Введите количество продукции на продажу, не более $product: ")
        val quantity= readLine()?.toInt() ?:0
        print("Введите цену продажи продукции: " )
        val price= readLine()?.toInt() ?:0
        return if (quantity<=product) Tender(this.id, quantity, price)
        else Tender(this.id, product, price)
    }

    //заявка на выдачу ссуды
    fun RequestsLoan(current:Int) {
        var freeLoan = SetTotalPledge(current)/2 - totalLoans  //доступная ссуда
        if (freeLoan > 0){
            println("Вам доступны ссуды на сумму $freeLoan")
            print("Ведите общую сумму ссуд - ")
            var input = readLine()?.toInt()!!
            if (input<=0) return
            if (input < freeLoan) freeLoan=input
            println("Ведите сумму ссуды для каждой фабрики")
            for (i in factories.indices) {
                if (!factories[i].pledge && freeLoan > 0)  //фабрика не в залоге
                {
                    var axe = factories[i].buildingCost  //сумма ссуды для фабрики
                    if (axe >= freeLoan) axe=freeLoan
                    if (axe <= 0 ) return
                    print("Под фабрику номер $i можно взять ссуду на сумму до $axe - ")
                    input = readLine()?.toInt()!!
                    if (input in 1..axe) {
                        loans.add(Loan(factories[i], current))
                        factories[i].pledge=true
                        totalLoans += input
                        freeLoan -=input
                        cash += input
                    }
                    else println("Запрошена неверная сумма")
                }
            }
        }
        else println("Вам недоступны ссуды")
    }
    //определить max сумму залога - стоимость построенных фабрик
    private fun SetTotalPledge(current:Int): Int{
        var totalPledge = 0
        for (factory in factories)
            if (factory.timeStart <= current)
                totalPledge += factory.buildingCost
        return totalPledge
    }

    //инвестиции в строительство фабрик
    fun RequestsBuilding(current:Int){
        //оплата 2 части перед запуском строящихся и реконструируемых фабрик
        for (factory in factories){
            if (factory.timeStart - current == 1) cash -= factory.buildingCost/2
            if (factory.autoStart - current == 1) cash -= reconstr/2
        }
        if (bankrupt) return

        if (cash < invest){
            println("Недостаточно средств для строительства и реконструкции фабрик")
            return
        }
        var input =0
        //Строительство новых фабрик
        var freeBuild = numberFactory- factories.size  //доступно для строительства
        if (freeBuild > 0) {
            println("Вы можете построить $freeBuild фабрик на сумму не более $cash")
            print("Введите желаемое количество фабрик для постройки - ")
            input = readLine()?.toInt()!!
            if (input > 0) {
                if (input < freeBuild) freeBuild = input
                for (n in 1..freeBuild) {
                    if (cash < invest) {
                        println("Недостаточно средств для строительства фабрики")
                        return
                    }
                    print(
                        "Автоматизированная фабрика? " +
                                "(Стоимость строительства автоматизированной 10000$, обычной 5000$) " +
                                "Да-1, нет-0.  "
                    )
                    input = readLine()?.toInt()!!
                    var auto = false
                    if (input == 1) auto = true
                    val factory = Factory(current, auto)
                    if (factory.buildingCost / 2 <= cash) {
                        cash -= factory.buildingCost / 2
                        if (bankrupt) return
                        freeBuild--
                        factories.add(factory)
                    } else println("Недостаточно средств для строительства фабрики")
                }
            }
        }
        else println("Вам недоступно строительство фабрик")

        //Реконструкция фабрик
        print("Хотите автоматизировать имеющиеся фабрики? Да-1, нет-0.  ")
        input = readLine()?.toInt()!!
        if (input <= 0) return
        for (i in factories.indices) {
            if (!factories[i].auto && factories[i].timeStart<current && factories[i].autoStart==0) {
                if (cash < reconstr/2) {
                    println("Недостаточно средств для реконструкции фабрики")
                    return
                }
                print("Хотите автоматизировать фабрику номер $i? Да-1, нет - 0  ")
                input = readLine()?.toInt()!!
                if (input == 1) {
                    cash -= reconstr/2
                    if (bankrupt) return
                    factories[i].autoStart = current + 9
                }
            }
            else println("НеТ доступных для реконструкции фабрик")
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