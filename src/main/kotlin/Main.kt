import BankerManagerSimulation.Game

fun main() {
    println("Hello World!")

    var game=Game()
    var order=3
    for(n in 1..10) {
        order = game.TransitionPriceLevel(order)
        println(order)
        var level = game.levels.get(order)
        if (level != null) {
            println(level.LevelToString())
        }
    }
}