package pool.dashboard

import pool.Context
import pool.Measurement

class TotalChlorinePane(context: Context) extends DashboardTitledPane(context):
  text = context.headerTotalChlorine
  range.text = context.dashboardTotalChlorineRange
  good.text = context.dashboardTotalChlorineGood
  ideal.text = context.dashboardTotalChlorineIdeal
  current.text <== context.model.currentTotalChlorine.asString
  average.text <== context.model.averageTotalChlorine.asString

  context.model.inRangeAverageTotalChlorine.onChange { (_, _, inRange) =>
    if inRange then println("total chlorine in range") else println("total chlorine out of range")
  }