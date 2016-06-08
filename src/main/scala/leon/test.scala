package leon

import scala.io.Source
import scala.collection.mutable._
import java.text.SimpleDateFormat  
import java.util._
import java.io._

case class CityInfo(
    Station:  String,
    Lat:  Float,
    Lon:  Float,
    Temperature_Upper:  Float,
    Temperature_Lower:  Float,
    Pressure_Upper:  Float,
    Pressure_Lower:  Float,
    Humidity_Upper:  Float,
    Humidity_Lower:  Float
    ){
  //Validate data range
  def validateDataRange() = {
    if(Lat > 90.0 || Lat < -90.0) throw new Exception("Incorrect Latitude!") 
    if(Lon > 180.0 || Lon < -180.0) throw new Exception("Incorrect Longitude!")
    if(Temperature_Upper > 60.0 || Temperature_Upper < -20.0) throw new Exception("Incorrect Temperature_Upper!")
    if(Temperature_Lower > 60.0 || Temperature_Lower < -20.0) throw new Exception("Incorrect Temperature_Lower!")
    if(Temperature_Lower > Temperature_Upper) throw new Exception("Temperature_Lower > Temperature_Upper")
    if(Pressure_Upper > 1050.0 || Pressure_Upper < 980.0) throw new Exception("Incorrect Pressure_Upper!")
    if(Pressure_Lower > 1050.0 || Pressure_Lower < 980.0) throw new Exception("Incorrect Pressure_Lower!")
    if(Humidity_Upper > 100.0 || Humidity_Upper < 0.0) throw new Exception("Incorrect Humidity_Upper!")
    if(Humidity_Lower > 100.0 || Humidity_Lower < 0.0) throw new Exception("Incorrect Humidity_Lower!")
 }
}

case class DateTime(
    strDT:  String,
    dt:  Date
){
  def getAustralianHour():Int = {
    val dtFormat1 = new SimpleDateFormat("HH")
    dtFormat1.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"))
    val strHour = dtFormat1.format(dt).toInt
    
    if(strHour==0){
      24
    }
    else{
      strHour
    }
  }
}



object test {
  def generateDT():ArrayBuffer[DateTime] = {
    val dtNow = Calendar.getInstance()    
    
    val arrDT = new ArrayBuffer[DateTime]
    
    for(i <- (1 to 7*24)){
      //Increment by 1 hour
      dtNow.add(Calendar.HOUR,1)
      val dtNext = dtNow.getTime
      val dtFormat1 = new SimpleDateFormat("yyyy-MM-dd")
      val dtFormat2 = new SimpleDateFormat("HH:00:00")
      dtFormat1.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"))
      dtFormat2.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"))
      val strDate = dtFormat1.format(dtNext)
      val strHour = dtFormat2.format(dtNext)
      val strDT = strDate+"T"+strHour+"Z"

      arrDT += DateTime(strDT,dtNext)
    }
    
    arrDT
  }
  
  def generateTemperature(temperature_Upper:Float,temperature_Lower:Float, hour:Int):Float={
    val temperature = ((new util.Random).nextFloat * (temperature_Upper - temperature_Lower)/2 * math.sin((2*math.Pi/24*(hour-8))) + (temperature_Upper + temperature_Lower)/2 )
    
    temperature.toFloat
}

  def generateHumidity(humidity_Upper:Float,humidity_Lower:Float):Float={
    (humidity_Lower + (new util.Random).nextFloat * (humidity_Upper - humidity_Lower))
}

  def generatePressure(pressure_Upper:Float,pressure_Lower:Float):Float={
    (pressure_Lower + (new util.Random).nextFloat * (pressure_Upper - pressure_Lower))
}

  
  
  def main(args:Array[String]):Unit ={
    println("Start emitting data:")
    
    var filename = "./CityInfo.csv" //"./CityInfo.csv"
    if(args.length >= 1){
      filename = args(0)
    }
    
    //Read lines from "CityInfo.csv"
    val lines = Source.fromFile(filename).getLines
    
    //Skip header line
    var header= lines.take(1).next

    val cities = new ArrayBuffer[CityInfo]    
    for(line <- lines) {
      val cols = line.split(",")
      val city = CityInfo(cols(0),cols(1).toFloat,cols(2).toFloat,cols(3).toFloat,cols(4).toFloat,cols(5).toFloat,cols(6).toFloat,cols(7).toFloat,cols(8).toFloat)
      city.validateDataRange()
      
      cities+=city
    }
    
    
    //Generate datetime series for the following 7 days x 24 Hours
    val arrDT = generateDT()
    
    //Generate lines for output
    val linesOutput = new ArrayBuffer[String] 
    for(city <- cities;
        dt <- arrDT
        ){
      
      val station = city.Station
      val latitude = city.Lat.formatted("%.2f")
      val longitude = city.Lon.formatted("%.2f")
      val datetime = dt.strDT
      val pressure = generatePressure(city.Pressure_Upper,city.Pressure_Lower).formatted("%.1f")
      val humidity = generateHumidity(city.Humidity_Upper,city.Humidity_Lower).formatted("%.0f")
      val temperature1 = generateTemperature(city.Temperature_Upper, city.Temperature_Lower, dt.getAustralianHour())      
      var temperature = ""
      if(temperature1 > 0){
        temperature = "+"+temperature1.formatted("%.1f")
      }
      else{
        temperature = temperature1.formatted("%.1f")
      }
      
      var conditions = ""
      if(humidity.toFloat > 90.0){
        if(temperature1 <= 0.0){
          conditions = "Snowy"
        }
        else{
          conditions = "Rainy"
        }
      }
      else if(humidity.toFloat >= 40.0 && humidity.toFloat <= 90.0){
        conditions = "Cloudy"
      } 
      else if(humidity.toFloat < 40.0){
        val hour = dt.getAustralianHour()
        if(hour < 6 || hour >= 18){
          conditions = "Fair"
        }
        else{
          conditions = "Sunny"
        }
      }
      else{}
      
      //Concat the line for output
      linesOutput+=station+"|"+latitude+","+longitude+"|"+datetime+"|"+conditions+"|"+temperature+"|"+pressure+"|"+humidity
      
    }
    
    linesOutput.map(println)
    
    val out = new PrintWriter("./testData.csv")
    linesOutput.map(out.println)
    out.close()
    
    println("Test data generated!")
    
    //end
  }
}