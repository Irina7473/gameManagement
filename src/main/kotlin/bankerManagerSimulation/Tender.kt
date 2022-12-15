package bankerManagerSimulation
//Заявка на тендер
//сделать проверку на <0
class Tender(internal val id:Int,
             internal var quantity:Int,     //количество
             internal val price:Int)        //цена
{
    internal fun TenderToString(): String {
        return "quantity=$quantity, price=$price \n"
    }
}
