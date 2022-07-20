package pool

import com.typesafe.scalalogging.LazyLogging

import java.text.NumberFormat

import scalafx.Includes.*
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.{LongProperty, ObjectProperty}

import Entity.given
import Measurement.*

final class Model(context: Context) extends LazyLogging:
  private val store = context.store

  val observableErrors = ObservableBuffer[Error]()

  val observablePools = ObservableBuffer[Pool]()
  val observableCleanings = ObservableBuffer[Cleaning]()
  val observableMeasurements = ObservableBuffer[Measurement]()
  val observableChemicals = ObservableBuffer[Chemical]()

  val selectedPoolId = ObjectProperty[Long](0)
  val selectedCleaningId = ObjectProperty[Long](0)
  val selectedMeasurementId = ObjectProperty[Long](0)
  val selectedChemicalId = ObjectProperty[Long](0)

  val currentTotalChlorine = ObjectProperty[Int](0)
  val averageTotalChlorine = ObjectProperty[Int](0)
  val rangeCurrentTotalChlorine = ObjectProperty[Boolean](false)
  val rangeAverageTotalChlorine = ObjectProperty[Boolean](false)

  val currentFreeChlorine = ObjectProperty[Int](0)
  val averageFreeChlorine = ObjectProperty[Int](0)
  val rangeCurrentFreeChlorine = ObjectProperty[Boolean](false)
  val rangeAverageFreeChlorine = ObjectProperty[Boolean](false)

  val currentCombinedChlorine = ObjectProperty[Double](0)
  val averageCombinedChlorine = ObjectProperty[Double](0)
  val rangeCurrentCombinedChlorine = ObjectProperty[Boolean](false)
  val rangeAverageCombinedChlorine = ObjectProperty[Boolean](false)

  val currentPh = ObjectProperty[Double](0)
  val averagePh = ObjectProperty[Double](0)
  val rangeCurrentPh = ObjectProperty[Boolean](false)
  val rangeAveragePh = ObjectProperty[Boolean](false)

  val currentCalciumHardness = ObjectProperty[Int](0)
  val averageCalciumHardness = ObjectProperty[Int](0)
  val rangeCurrentCalciumHardness = ObjectProperty[Boolean](false)
  val rangeAverageCalciumHardness = ObjectProperty[Boolean](false)

  val currentTotalAlkalinity = ObjectProperty[Int](0)
  val averageTotalAlkalinity = ObjectProperty[Int](0)
  val inRangeCurrentTotalAlkalinity = ObjectProperty[Boolean](false)
  val inRangeAverageTotalAlkalinity = ObjectProperty[Boolean](false)

  val currentCyanuricAcid = ObjectProperty[Int](0)
  val averageCyanuricAcid = ObjectProperty[Int](0)
  val inRangeCurrentCyanuricAcid = ObjectProperty[Boolean](false)
  val inRangeAverageCyanuricAcid = ObjectProperty[Boolean](false)

  val currentTotalBromine = ObjectProperty[Int](0)
  val averageTotalBromine = ObjectProperty[Int](0)
  val inRangeCurrentTotalBromine = ObjectProperty[Boolean](false)
  val inRangeAverageTotalBromine = ObjectProperty[Boolean](false)

  val currentSalt = ObjectProperty[Int](0)
  val averageSalt = ObjectProperty[Int](0)
  val inRangeCurrentSalt = ObjectProperty[Boolean](false)
  val inRangeAverageSalt = ObjectProperty[Boolean](false)

  val currentTemperature = ObjectProperty[Int](0)
  val averageTemperature = ObjectProperty[Int](0)
  val inRangeCurrentTemperature = ObjectProperty[Boolean](false)
  val inRangeAverageTemperature = ObjectProperty[Boolean](false)

  val shouldBeInFxThread = (message: String) => require(Platform.isFxApplicationThread, message)
  val shouldNotBeInFxThread = (message: String) => require(!Platform.isFxApplicationThread, message)

  selectedPoolId.onChange { (_, oldPoolId, newPoolId) =>
    shouldBeInFxThread("selected pool id onchange should be in fx thread.")
    logger.info(s"selected oool id onchange event: $oldPoolId -> $newPoolId")
    cleanings(newPoolId)
    measurements(newPoolId)
    chemicals(newPoolId)
  }

  observableMeasurements.onChange { (_, _) =>
    shouldNotBeInFxThread("via measurements, observable measurements onchange should not be in fx thread.")
    logger.info(s"observable measurements onchange event.")
    Platform.runLater( dashboard() )
  }

  pools()

  private def pools(): Unit =
    Future {
      shouldNotBeInFxThread("pools should not be in fx thread.")
      observablePools ++= store.pools()
    }.recover { case error: Throwable => onError(error, s"Loading pools data failed: ${error.getMessage}") }

  private def cleanings(poolId: Long): Unit =
    Future {
      shouldNotBeInFxThread("cleanings should not be in fx thread.")
      observableCleanings.clear()
      observableCleanings ++= store.cleanings(poolId)
    }.recover { case error: Throwable => onError(error, s"Loading cleanings data failed: ${error.getMessage}") }

  private def measurements(poolId: Long): Unit =
    Future {
      shouldNotBeInFxThread("measurements should not be in fx thread.")
      observableMeasurements.clear()
      observableMeasurements ++= store.measurements(poolId) 
    }.recover { case error: Throwable => onError(error, s"Loading measurements data failed: ${error.getMessage}") }

  private def chemicals(poolId: Long): Unit =
    Future {
      shouldNotBeInFxThread("chemicals should not be in fx thread.")
      observableChemicals.clear()
      observableChemicals ++= store.chemicals(poolId) 
    }.recover { case error: Throwable => onError(error, s"Loading chemicals data failed: ${error.getMessage}") }

  private def dashboard(): Unit =
    shouldBeInFxThread("dashboard should be in fx thread.")
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.setMaximumFractionDigits(1)
    observableMeasurements.headOption.foreach { measurement =>
      println(s"in dashboard ... measurement: $measurement")
      onCurrent(measurement, numberFormat)
      onAverage(numberFormat)
    }

  private def onCurrent(measurement: Measurement, numberFormat: NumberFormat): Unit =
    shouldBeInFxThread("oncurrent should be in fx thread.")
    currentTotalChlorine.value = measurement.totalChlorine
    currentFreeChlorine.value = measurement.freeChlorine
    currentCombinedChlorine.value = numberFormat.format( measurement.combinedChlorine ).toDouble
    currentPh.value = numberFormat.format( measurement.ph ).toDouble
    currentCalciumHardness.value = measurement.calciumHardness
    currentTotalAlkalinity.value = measurement.totalAlkalinity
    currentCyanuricAcid.value = measurement.cyanuricAcid
    currentTotalBromine.value = measurement.totalBromine
    currentSalt.value = measurement.salt
    currentTemperature.value = measurement.temperature

    rangeCurrentTotalChlorine.value = totalChlorineRange.contains(currentTotalChlorine.value)
    rangeCurrentFreeChlorine.value = freeChlorineRange.contains(currentFreeChlorine.value)
    rangeCurrentCombinedChlorine.value = combinedChlorineRange.contains(currentCombinedChlorine.value)
    rangeCurrentPh.value = phRange.contains(currentPh.value)
    rangeCurrentCalciumHardness.value = calciumHardnessRange.contains(currentCalciumHardness.value)
    inRangeCurrentTotalAlkalinity.value = totalAlkalinityRange.contains(currentTotalAlkalinity.value)
    inRangeCurrentCyanuricAcid.value = cyanuricAcidRange.contains(currentCyanuricAcid.value)
    inRangeCurrentTotalBromine.value = totalBromineRange.contains(currentTotalBromine.value)
    inRangeCurrentSalt.value = saltRange.contains(currentSalt.value)
    inRangeCurrentTemperature.value = temperatureRange.contains(currentTemperature.value)

  private def onAverage(numberFormat: NumberFormat): Unit =
    shouldBeInFxThread("onaverage should be in fx thread.")
    val count = observableMeasurements.length
    averageTotalChlorine.value = observableMeasurements.map(_.totalChlorine).sum / count
    averageFreeChlorine.value = observableMeasurements.map(_.freeChlorine).sum / count
    averageCombinedChlorine.value = numberFormat.format( observableMeasurements.map(_.combinedChlorine).sum / count ).toDouble
    averagePh.value = numberFormat.format( observableMeasurements.map(_.ph).sum / count ).toDouble
    averageCalciumHardness.value = observableMeasurements.map(_.calciumHardness).sum / count
    averageTotalAlkalinity.value = observableMeasurements.map(_.totalAlkalinity).sum / count
    averageCyanuricAcid.value = observableMeasurements.map(_.cyanuricAcid).sum / count
    averageTotalBromine.value = observableMeasurements.map(_.totalBromine).sum / count
    averageSalt.value = observableMeasurements.map(_.salt).sum / count
    averageTemperature.value = observableMeasurements.map(_.temperature).sum / count

    rangeAverageTotalChlorine.value = totalChlorineRange.contains(averageTotalChlorine.value)
    rangeAverageFreeChlorine.value = freeChlorineRange.contains(averageFreeChlorine.value)
    rangeAverageCombinedChlorine.value = combinedChlorineRange.contains(averageCombinedChlorine.value)
    rangeAveragePh.value = phRange.contains(averagePh.value)
    rangeAverageCalciumHardness.value = calciumHardnessRange.contains(averageCalciumHardness.value)
    inRangeAverageTotalAlkalinity.value = totalAlkalinityRange.contains(averageTotalAlkalinity.value)
    inRangeAverageCyanuricAcid.value = cyanuricAcidRange.contains(averageCyanuricAcid.value)
    inRangeAverageTotalBromine.value = totalBromineRange.contains(averageTotalBromine.value)
    inRangeAverageSalt.value = saltRange.contains(averageSalt.value)
    inRangeAverageTemperature.value = temperatureRange.contains(averageTemperature.value)

  def onError(message: String): Unit =
    shouldBeInFxThread("onerror message should be in fx thread.")
    observableErrors += Error(message)
    logger.error(message)

  def onError(error: Throwable, message: String): Unit =
    shouldBeInFxThread("onerror error, message should be in fx thread.")
    observableErrors += Error(message)
    logger.error(message, error)

  def add(pool: Pool): Future[Pool] =
    Future {
      shouldNotBeInFxThread("add pool should not be in fx thread.")
      val newPool = store.add(pool)
      observablePools += newPool
      observablePools.sort()
      selectedPoolId.value = newPool.id
      newPool
    }

  def update(selectedIndex: Int, pool: Pool): Future[Unit] =
    Future {
      shouldNotBeInFxThread("update pool should not be in fx thread.")
      store.update(pool)
      observablePools.update(selectedIndex, pool)
      observablePools.sort()
      selectedPoolId.value = pool.id
    }

  def add(cleaning: Cleaning): Future[Cleaning] =
    Future {
      shouldNotBeInFxThread("add cleaning should not be in fx thread.")
      val newCleaning = store.add(cleaning)
      observableCleanings += newCleaning
      observableCleanings.sort()
      selectedCleaningId.value = newCleaning.id
      newCleaning
    }

  def update(selectedIndex: Int, cleaning: Cleaning): Future[Unit] =
    Future {
      shouldNotBeInFxThread("update cleaning should not be in fx thread.")
      store.update(cleaning)
      observableCleanings.update(selectedIndex, cleaning)
      observableCleanings.sort()
      selectedCleaningId.value = cleaning.id
    }
  
  def add(measurement: Measurement): Future[Measurement] =
    Future {
      shouldNotBeInFxThread("add measurement should not be in fx thread.")
      val newMeasurement = store.add(measurement)
      observableMeasurements += newMeasurement
      observableMeasurements.sort()
      selectedMeasurementId.value = newMeasurement.id
      newMeasurement
    }

  def update(selectedIndex: Int, measurement: Measurement): Future[Unit] =
    Future {
      shouldNotBeInFxThread("update measurement should not be in fx thread.")
      store.update(measurement)
      observableMeasurements.update(selectedIndex, measurement)
      observableMeasurements.sort()
      selectedMeasurementId.value = measurement.id
    }
  
  def add(chemical: Chemical): Future[Chemical] =
    Future {
      shouldNotBeInFxThread("add chemical should not be in fx thread.")
      val newChemical = store.add(chemical)
      observableChemicals += newChemical
      observableChemicals.sort()
      selectedChemicalId.value = newChemical.id      
      newChemical
    }

  def update(selectedIndex: Int, chemical: Chemical): Future[Unit] =
    Future {
      shouldNotBeInFxThread("update chemical should not be in fx thread.")
      store.update(chemical)
      observableChemicals.update(selectedIndex, chemical)
      observableChemicals.sort()
      selectedChemicalId.value = chemical.id
    }