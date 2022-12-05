package BankerManagerSimulation

class MarketLevel (quantityM:Double, priceM:Int, quantityFP:Double, priceFP:Int){
    val quantityM=quantityM
    val priceM=priceM
    val quantityFP=quantityFP
    val priceFP=priceFP


    fun LevelToString():String{
        return "Определение обстановки на рынке:\nколичество ГП=$quantityFP,  max цена за 1 ГП=$priceFP,\n " +
                "количество сырья=$quantityM, min цена за 1 сырья=$priceM\n"
    }
}