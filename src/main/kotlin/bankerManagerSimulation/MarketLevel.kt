package bankerManagerSimulation
import kotlin.math.floor

//Коньюнктура рынка
class MarketLevel (internal var quantityM:Double,   //кол-во материалов на рынке к продаже
                   internal val priceM:Int,         //минимальная цена материалов
                   internal var quantityFP:Double,  //кол-во продукции для рынка к закупу
                   internal val priceFP:Int) {     //максимальная цена продукции

    //Конструктор копирования
    internal constructor(level: MarketLevel, num:Int) :
            this(level.quantityM, level.priceM, level.quantityFP, level.priceFP)
    {
        this.quantityM = floor(level.quantityM * num)
        this.quantityFP = floor(level.quantityFP * num)
    }

    fun LevelToString(): String {
        return "Определение обстановки на рынке:\n" +
                "количество сырья=$quantityM, min цена за 1 сырья=$priceM\n" +
                "количество ГП=$quantityFP,  max цена за 1 ГП=$priceFP,\n "
    }
}
