package bankerManagerSimulation
//ССУДА
class Loan (internal val factory:Factory, //фабрика в залоге
                     current:Int)  //раунд
{
    internal val term = current+12 //срок возврата
    internal val amount = factory.buildingCost //сумма ссуды
}