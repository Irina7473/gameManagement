package BankerManagerSimulation
//ФАБРИКА
// установить модификаторы доступа
class Factory (timestart:Int) {
    var auto = false  //автоматизированная
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
    var timestart=timestart  //период начала производства
}