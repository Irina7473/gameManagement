package bankerManagerSimulation
//ФАБРИКА
class Factory (current:Int=0,       //раунд
               internal var auto:Boolean=false)     //признак автоматизированной фабрики
{
    internal var autoStart = 0   // раунд старта после реконструкции
        set(value) {
            field = if (value>0) value+9
            else 0
        }
    internal var timeStart:Int //раунд старта производства
    init{
        timeStart = if(current==0) 0
        else if (auto) current+7
        else current+5
    }

    internal var power:Int //производственная мощность=переработка сырья
    init{
        power = if (auto) 2
        else 1
    }

    internal var costs:Int    //переменные затраты
    init{
        costs = if (auto) 3000  //переделать, если 1шт
        else 2000
    }
    internal var fee:Int  //постоянные затраты
    init{
        fee = if (auto) 1500
        else 1000
    }

    internal var buildingCost:Int   //стоимость фабрики
    init{
        buildingCost = if (auto) 10000
        else 5000
    }

    internal var pledge = false   //в залоге под ссуду

    //изменение типа фабрики после окончания реконструкции
    internal fun ChangeFactoryType(){
        auto =true
        autoStart = 0
        power =2
        costs=3000
        fee=1500
        buildingCost = 10000
    }
}