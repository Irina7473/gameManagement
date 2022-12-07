package BankerManagerSimulation
//ИГРОК
// установить модификаторы доступа
class Player(name:String) {
    val name=name

    var factories = arrayListOf<Factory>(Factory(1), Factory(1))
    var material=4
    var product=2
    var cash=10000
        set(value) {
            if(value>0) field = value
            else field = 0  //добавить банкротство
        }
    var loans = arrayListOf<Loan>()
    var totalLoans=0
    var totalPledge=10000
    var fixedCosts=0
    var totalCapital=0

    //Константы
    //Переменные расходы
    val factoryCosts=2000
    val autofactoryCosts=3000
    //Постоянные издержки
    val materialFee=300
    val productFee=500
    val factoryFee=1000
    val autofactoryFee=1500
    //Капитальные затраты
    val numberFactory=6
    val invest=5000
    val reconstr=7000

    // расчет постоянных затрат
    fun CalcFixedCosts(){
        material*materialFee + product*productFee
        //учесть банкротство
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
    fun RequestsManufacture() {
        print("Введите количество продукции на производство на фабриках: ")
        var quantity= readLine()?.toInt() ?:0
        //Пройдемся по фабрикам
        //Рассчитать переменные расходы
    }
    //заявка на продажу продукции
    fun RequestsProdukts():Tender {
        print("Введите количество продукции на продажу: ")
        var quantity= readLine()?.toInt() ?:0
        //проверить наличие, не спрашивать совсем-брать из наличия
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
    // /*Обычная фабрика стоит 5000 долл. и начинает давать продукцию на 5-й месяц после начала строительства;
    // /*автоматизированная фабрика стоит 10 000 долл и дает продукцию на 7-й месяц после начала строительства.
    // Обычную фабрику можно автоматизировать за 7000 долл., реконструкция продолжается 9 месяцев, все это время фабрика может работать как обычная.
    // /*Половину стоимости фабрики надо платить в начале строительства, вторую половину — за месяц до начала выпуска продукции в этой же фазе цикла.
    // /*Общее число имеющихся и строящихся фабрик у каждого игрока не должно превышать шести.
        var freeBuild = numberFactory- factories.size  //доступно для строительства
        var freeCash = cash
        if (freeBuild > 0 && freeCash >= invest) {
            println("Вы можете построить $freeBuild фабрик на сумму не более $freeCash")
            println("Ведите желаемое количество фабрик для постройки")
            var input = readLine()?.toInt()!!
            if (input<=0) return
            for (n in 1..freeBuild) {
                if (freeCash < invest) {
                    println("Недостаточно средств для строительства фабрики")
                    return
                }
                println("Автоматизированная фабрика? (Стоимость строительства автоматизированной 10000$, обычной 5000$) Да-1, нет - 0")
                input = readLine()?.toInt()!!
                var auto = false
                if (input == 1) auto = true
                val factory = Factory(current, auto)
                if (factory.buildingCost /2 <= freeCash) {
                    freeCash -= factory.buildingCost /2   //не забыть списать 2 часть перед запуском
                    freeBuild--
                    factories.add(factory)
                }
                else println("Недостаточно средств для строительства фабрики")
            }
            //Реконструкция фабрик
            println("Хотите автоматизировать имеющиеся фабрики? Да-1, нет - 0")
            input = readLine()?.toInt()!!
            if (input<=0) return
            for (i in factories.indices) {
                if (factories[i].auto == false) {
                    if (freeCash < reconstr) {
                        println("Недостаточно средств для реконструкции фабрики")
                        return
                    }
                    print("Хотите автоматизировать фабрику номер $i? Да-1, нет - 0")
                    input = readLine()?.toInt()!!
                    if (input == 1){
                        freeCash -= reconstr /2  //не забыть списать 2 часть перед запуском
                        factories[i].autoStart = current+9
                    }
                }
            }
        }
        else println("Вам недоступно строительство фабрик")
    }

    //определение капитала игрока
    fun CalcTotalCapital():Int{
        //Сумма стоимости всех фабрик (по цене строительства), стоимости сырья (по мин текущей цене), стоимости ГП (по макс текущей цене) и наличных.
        // Минус сумма ссуд и предстоящих расходов начатому строительству
        return  cash
    }
}