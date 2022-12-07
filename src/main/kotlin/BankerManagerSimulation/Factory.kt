package BankerManagerSimulation
//ФАБРИКА
// установить модификаторы доступа
class Factory (current:Int=0, auto:Boolean=false) {
    var auto = auto  //автоматизированная
    var autoStart = 0   // период старта после реконструкции
        set(value) {
            field = value+9
        }
    var pledge = false   //в залоге под ссуду
    var buildingCost:Int   //стоимость фабрики
    init{
        if (auto) buildingCost=10000
        else buildingCost=5000
    }
    var power:Int   //производственная мощность=переработка сырья
    init{
        if (auto) power=2
        else power=1
    }
    var timeStart:Int =0 //период начала производства
    init{
        if(current==0) timeStart=0
        else if (auto) timeStart=current+7
        else timeStart=current+5
    }

    //может все-таки вторичный конструктор вместо блока инициализации - подумать
}