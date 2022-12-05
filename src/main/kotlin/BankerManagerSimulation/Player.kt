package BankerManagerSimulation

// установить модификаторы доступа
class Player(name:String) {
    val name=name

    var factory=2
    var autofactory=0
    var material=4
    var product=2
    var cash=10000
    var fixedCosts=0
    var totalCapital=0

    //Константы
    //Переменные расходы
    val factoryPower=1
    val factoryCosts=2000
    val autofactoryPower=2
    val autofactoryCosts=3000
    //Постоянные издержки
    val materialFee=300
    val productFee=500
    val factoryFee=1000
    val autofactoryFee=1500
    //Капитальные затраты
    val buildingFactory=5000
    val buildingAutofactory=10000
    val rebuilding=7000
    val startFactory=5
    val startAutofactory=7
    val startRebuilding=10
    val numberFactory=6
    //Ссуды
    val loanPercent=1
    val loanTerm=12
    val factoryLoan=5000
    val autofactoryLoan=10000

    fun RequestsMaterials():Tender {
        print("Введите количество закупа материалов: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену закупа материалов: " )
        var price= readLine()?.toInt() ?:0
        return Tender(this.name, quantity, price)
    }
    fun RequestsManufacture() {
        print("Введите количество продукции на производство на обычных фабриках: ")
        var quantityF= readLine()?.toInt() ?:0
        print("Введите количество продукции на производство на автоматизированых фабриках: " )
        var quantityAF= readLine()?.toInt() ?:0
        //Рассчитать переменные расходы
    }
    fun RequestsProdukts():Tender {
        print("Введите количество продукции на продажу: ")
        var quantity= readLine()?.toInt() ?:0
        print("Введите цену продажи продукции: " )
        var price= readLine()?.toInt() ?:0
        return Tender(this.name, quantity, price)
    }

    fun CalcFixedCosts():Int{
        return factory*factoryFee + autofactory*autofactoryFee + material*materialFee + product*productFee
    }

    fun CalcCach(){
        cash -=CalcFixedCosts()
    }

    fun CalcTotalCapital():Int{
        //Сумма стоимости всех фабрик (по цене строительства), стоимости сырья (по мин текущей цене), стоимости ГП (по макс текущей цене) и наличных.
        // Минус сумма ссуд и предстоящих расходов начатому строительству
        return factory*factoryFee + autofactory*autofactoryFee + cash
    }
}