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
    val rebuilding=7000
    val startFactory=5
    val startAutofactory=7
    val startRebuilding=10
    val numberFactory=6

    fun RequestsMaterials():Tender {
        print("Введите количество закупа материалов: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену закупа материалов: " )
        var price= readLine()?.toInt() ?:0
        return Tender(this.name, quantity, price)
    }
    fun RequestsManufacture() {
        print("Введите количество продукции на производство на фабриках: ")
        var quantity= readLine()?.toInt() ?:0
        //Пройдемся по фабрикам
        //Рассчитать переменные расходы
    }
    fun RequestsProdukts():Tender {
        print("Введите количество продукции на продажу: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену продажи продукции: " )
        var price= readLine()?.toInt() ?:0
        if (quantity<=product) return Tender(this.name, quantity, price)
        else return Tender(this.name, product, price)
    }
    //TO DO
    fun RequestsLoan(current:Int) {
        var freeLoan = totalPledge/2 - totalLoans  //доступная ссуда
        if (freeLoan > 0){
            println("Вам доступны ссуды на сумму $freeLoan")
            println("Ведите сумму ссуды для каждой фабрики")
            for (i in factories.indices) {
                if (factories[i].pledge == false)  //фабрика не в залоге
                {
                    var axe = factories[i].buildingCost  //сумма ссуды для фабрики
                    if (axe >= freeLoan) axe=freeLoan
                    print("Под фабрику номер $i можно взять ссуду на сумму до $axe")
                    var input = readLine()?.toInt()!!
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
    }

    fun CalcFixedCosts():Int{
        return material*materialFee + product*productFee
    }

    fun CalcCach(){
        cash -=CalcFixedCosts()
    }

    fun CalcTotalCapital():Int{
        //Сумма стоимости всех фабрик (по цене строительства), стоимости сырья (по мин текущей цене), стоимости ГП (по макс текущей цене) и наличных.
        // Минус сумма ссуд и предстоящих расходов начатому строительству
        return  cash
    }
}