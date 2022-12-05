package BankerManagerSimulation

// установить модификаторы доступа
class Player(name:String) {
    val name=name
    var factory=2
    var autofactory=0
    var stock=4
    var product=2
    var cash=10000
    var fixedCosts=0
    var totalCapital=0

    //Константы
    //Расходы и объем на производстве
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





    fun CalcFixedCosts():Int{
        return factory*buildingFactory + autofactory*buildingAutofactory
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