package BankerManagerSimulation


class Game(number:Int) {
    val round=0
    val numberPlayers=number
    var activePlayers=numberPlayers
    var seniorPlayer=InstallSeniorPlayer()

    private val levels= mapOf(
        1 to Level(1.0*activePlayers,800,3.0*activePlayers,6500),
        2 to Level(1.5*activePlayers,650,2.5*activePlayers,6000),
        3 to Level(2.0*activePlayers,500,2.0*activePlayers,5500),
        4 to Level(2.5*activePlayers,400,1.5*activePlayers,5000),
        5 to Level(3.0*activePlayers,300,1.0*activePlayers,4500)
    )

    private fun TransitionPriceLevel(order:Int):Int{
        var options: List<Int> = listOf()
        when(order){
            1 -> options = listOf(1,1,1,1,2,2,2,2,3,3,4,5)
            2 -> options = listOf(1,1,1,2,2,2,2,3,3,3,4,5)
            3 -> options = listOf(1,2,2,2,3,3,3,3,4,4,4,5)
            4 -> options = listOf(1,2,3,3,3,4,4,4,4,5,5,5)
            5 -> options = listOf(1,2,3,3,4,4,4,4,5,5,5,5)
        }
        return options.random()
    }

    private fun InstallSeniorPlayer():Int{
        return round*numberPlayers+1
    }

}