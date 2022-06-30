package pool.chart

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.{LineChart, XYChart}
import scalafx.scene.control.{Tab, TabPane}

import pool.{Cleaning, Context}

final case class CleaningXY(xDate: LocalDateTime, yCount: Int)

class CleaningsChart(context: Context) extends TabPane:
  val cleanings = context.model.observableCleanings.reverse
  val formatter = DateTimeFormatter.ofPattern("M.dd")
  val minDate = cleanings.map(c => c.cleaned).min.format(formatter).toDouble
  val maxDate = cleanings.map(c => c.cleaned).max.format(formatter).toDouble

  val tab = new Tab {
    closable = false
    text = context.chartCleanings
    content = buildChart()
  }

  padding = Insets(6)
  tabs = List(tab)

  private def cleaningsToInt(cleaning: Cleaning): Int =
    var count = 0
    if cleaning.brush then count += 1
    if cleaning.net then count += 1
    if cleaning.skimmerBasket then count += 1
    if cleaning.pumpBasket then count += 1
    if cleaning.pumpFilter then count += 1
    if cleaning.vacuum then count += 1
    count

  def buildChart(): LineChart[Number, Number] =
    val filtered = cleanings.map(c => CleaningXY(c.cleaned, cleaningsToInt(c)))
    val (chart, series) = LineChartBuilder.build(context = context,
                                                 xLabel = context.chartMonthDay,
                                                 xMinDate = minDate,
                                                 xMaxDate = maxDate,
                                                 yLabel = context.chartYCleanings,
                                                 yLowerBound = 1,
                                                 yUpperBound = 6,
                                                 yTickUnit = 1,
                                                 yValues = filtered.map(cxy => cxy.yCount))
    filtered foreach { cxy =>
      series.data() += XYChart.Data[Number, Number](cxy.xDate.format(formatter).toDouble, cxy.yCount)
    }
    chart.data = series
    LineChartBuilder.addTooltip(chart)
    chart