package BankerManagerSimulation
//Заявка на тендер
// установить модификаторы доступа
class Tender(id:Int,quantity:Int, price:Int): Comparable<Tender>{
    val id=id        //имя игрока
    var quantity=quantity    //желаемое кол-во
    val price=price     //жлаемая цена

    override fun compareTo(other: Tender): Int {
        return if (this.price != other.price) this.price - other.price
        else 0
    }
}