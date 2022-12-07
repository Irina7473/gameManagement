package BankerManagerSimulation
//ФАБРИКА
// установить модификаторы доступа
class Factory (current:Int=0, auto:Boolean=false) {
    var auto = auto  //автоматизированная
    var autoStart = 0   // период старта после реконструкции
        set(value) {
            if (value>0) field = value+9
            else field=0
        }
    var timeStart:Int //период начала производства
    init{
        if(current==0) timeStart=0
        else if (auto) timeStart=current+7
        else timeStart=current+5
    }

    var power:Int   //производственная мощность=переработка сырья
    init{
        if (auto) power=2
        else power=1
    }

    var costs:Int    //переменные затраты
    init{
        if (auto) costs=3000  //переделать, если 1шт
        else costs=2000
    }
    var fee:Int  //постоянные затраты
    init{
        if (auto) fee=1500
        else fee=1000
    }

    var buildingCost:Int   //стоимость фабрики
    init{
        if (auto) buildingCost=10000
        else buildingCost=5000
    }

    var pledge = false   //в залоге под ссуду


    //может все-таки вторичный конструктор вместо блока инициализации - подумать

    //изменение типа фабрики после окончания реконструкции
    fun ChangeFactoryType(){
        auto =true
        autoStart = 0
        power =2
        costs=3000
        fee=1500
        buildingCost = 10000
    }
}