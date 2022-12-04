package BankerManagerSimulation

class Level (quantityFP:Double, priceFP:Int, quantityM:Double, priceM:Int){
    val quantityFP=quantityFP
    val priceFP=priceFP
    val quantityM=quantityM
    val priceM=priceM

    fun LevelToString():String{
        return "Новый уровень $quantityFP $priceFP $ quantityM $priceM"
    }
}