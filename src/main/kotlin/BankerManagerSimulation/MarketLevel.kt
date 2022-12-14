package BankerManagerSimulation
//Коньюнктура рынка
// установить модификаторы доступа
class MarketLevel (quantityM:Double, priceM:Int, quantityFP:Double, priceFP:Int){
    var quantityM=quantityM   //кол-во материалов на рынке к продаже
    val priceM=priceM       //минимальная цена материалов
    var quantityFP=quantityFP       //кол-во продукции для рынка к закупу
    val priceFP=priceFP     //максимальная цена продукции

    fun LevelToString():String{
        return "Определение обстановки на рынке:\nколичество ГП=$quantityFP,  max цена за 1 ГП=$priceFP,\n " +
                "количество сырья=$quantityM, min цена за 1 сырья=$priceM\n"
    }
}