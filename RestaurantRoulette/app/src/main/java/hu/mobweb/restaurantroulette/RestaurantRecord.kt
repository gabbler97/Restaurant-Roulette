package hu.mobweb.restaurantroulette

class RestaurantRecord{

    var restaurantName: String = ""
    var cost: Int = 0
    var openHour: Int = 0
    var openMinute: Int = 0
    var closeHour: Int = 0
    var closeMinute: Int = 0
    var long: Double = 0.0
    var lat : Double = 0.0

    constructor(restaurantName: String, cost: Int, openHour: Int, openMinute: Int,
    closeHour: Int, closeMinute: Int, long: Double, lat: Double){
        this.restaurantName = restaurantName
        this.cost = cost
        this.openHour = openHour
        this.openMinute = openMinute
        this.closeHour = closeHour
        this.closeMinute = closeMinute
        this.long = long
        this.lat = lat
    }

}