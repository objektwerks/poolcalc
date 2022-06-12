package pool

import java.time.{LocalDate, LocalTime, LocalDateTime}
import java.time.format.DateTimeFormatter

import scalafx.beans.property.{BooleanProperty, DoubleProperty, IntegerProperty, StringProperty}

enum unitOfMeasure:
  case gl, kg, g, l, ml, lbs, oz

sealed trait Entity:
  val id: Long

object Entity:
  def newDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
  def newDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def newTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
  def format(localDateTime: LocalDateTime): LocalDateTime = LocalDateTime.parse( localDateTime.format(newDateTimeFormatter) )
  def format(localDate: LocalDate): LocalDate = LocalDate.parse( localDate.format(newDateFormatter) )
  def format(localTime: LocalTime): LocalTime = LocalTime.parse( localTime.format(newTimeFormatter) )

  given poolOrdering: Ordering[Pool] = Ordering.by[Pool, Long](p => p.built).reverse
  given cleaningOrdering: Ordering[Cleaning] = Ordering.by[Cleaning, Long](c => c.cleaned.toLocalDate.toEpochDay).reverse
  given measurementOrdering: Ordering[Measurement] = Ordering.by[Measurement, Long](m => m.measured.toLocalDate.toEpochDay).reverse
  given chemicalOrdering: Ordering[Chemical] = Ordering.by[Chemical, Long](c => c.added.toLocalDate.toEpochDay).reverse

  def isNotInt(text: String): Boolean = !text.matches("\\d+")
  def isNotDouble(text: String): Boolean = !text.matches("\\d{0,7}([\\.]\\d{0,4})?")

final case class Pool(id: Long = 0,
                      name: String = "", 
                      built: Int = 0, 
                      volume: Int = 0,
                      unit: unitOfMeasure = unitOfMeasure.gl) extends Entity:
  val nameProperty = new StringProperty(this, "name", name)
  val builtProperty = new StringProperty(this, "built", built.toString)
  val volumeProperty = new StringProperty(this, "volume", volume.toString)
  val unitProperty = new StringProperty(this, "unit", unit.toString)
  val pool = this

final case class Cleaning(id: Long = 0,
                          poolId: Long,
                          brush: Boolean = false,
                          net: Boolean = false,
                          skimmerBasket: Boolean = false,
                          pumpBasket: Boolean = false,
                          pumpFilter: Boolean = false,
                          vacuum: Boolean = false,
                          cleaned: LocalDateTime = LocalDateTime.now) extends Entity:
  val brushProperty = new StringProperty(this, "brush", brush.toString)
  val netProperty = new StringProperty(this, "net", net.toString)
  val skimmerBasketProperty = new StringProperty(this, "skimmerBasket", skimmerBasket.toString)
  val pumpBasketProperty = new StringProperty(this, "pumpBasket", pumpBasket.toString)
  val pumpFilterProperty = new StringProperty(this, "pumpFilter", pumpFilter.toString)
  val vacuumProperty = new StringProperty(this, "vacuum", vacuum.toString)
  val cleanedProperty = new StringProperty(this, "cleaned", Entity.format(cleaned).toString)
  val cleaning = this

final case class Measurement(id: Long = 0,
                             poolId: Long,
                             freeChlorine: Int = 3,
                             combinedChlorine: Double = 0.0,
                             totalChlorine: Int = 3,
                             ph: Double = 7.4,
                             calciumHardness: Int = 375,
                             totalAlkalinity: Int = 100,
                             cyanuricAcid: Int = 50,
                             totalBromine: Int = 5,
                             temperature: Int = 85,
                             measured: LocalDateTime = LocalDateTime.now) extends Entity:
  val freeChlorineProperty = new StringProperty(this, "freeChlorine", freeChlorine.toString)
  val combinedChlorineProperty = new StringProperty(this, "combinedChlorine", ph.toString)
  val totalChlorineProperty = new StringProperty(this, "totalChlorine", totalChlorine.toString)
  val phProperty = new StringProperty(this, "ph", ph.toString)
  val calciumHardnessProperty = new StringProperty(this, "calciumHardness", calciumHardness.toString)
  val totalAlkalinityProperty = new StringProperty(this, "totalAlkalinity", totalAlkalinity.toString)
  val cyanuricAcidProperty = new StringProperty(this, "cyanuricAcid", cyanuricAcid.toString)
  val totalBromineProperty = new StringProperty(this, "totalBromine", totalBromine.toString)
  val temperatureProperty = new StringProperty(this, "temperature", temperature.toString)
  val measuredProperty = new StringProperty(this, "measured", Entity.format(measured).toString)
  val measurement = this

enum typeOfChemical:
  case liquidChlorine, trichlor, dichlor, calciumHypochlorite, stabilizer, algaecide, muriaticAcid

final case class Chemical(id: Long = 0,
                          poolId: Long,
                          typeof: typeOfChemical = typeOfChemical.liquidChlorine,
                          amount: Double = 1.0, 
                          unit: unitOfMeasure = unitOfMeasure.gl,
                          added: LocalDateTime = LocalDateTime.now) extends Entity:
  val typeofProperty = new StringProperty(this, "typeof", typeof.toString)
  val amountProperty = new StringProperty(this, "amount", amount.toString)
  val unitProperty = new StringProperty(this, "unit", unit.toString)
  val addedProperty = new StringProperty(this, "added", Entity.format(added).toString)
  val chemical = this