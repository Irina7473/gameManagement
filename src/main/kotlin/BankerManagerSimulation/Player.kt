package BankerManagerSimulation

class Player {
    var factory=2
    var autofactory=0
    var stock=4
    var product=2
    var cash=10000

    fun FixedCosts():Int{
        return stock*300 + product*500 + factory*1000 + autofactory*1500
    }

}