package BankerManagerSimulation
//ССУДА
// установить модификаторы доступа
class Loan (factory:Factory, round:Int){
    //val percent = 0.01  //процент
    val term = round+12 //срок возврата
    val factory = factory //фабрика в залоге
    val amount = factory.buildingCost //сумма ссуды
}