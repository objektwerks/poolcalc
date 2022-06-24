package pool.pane.dashboard

import pool.Context

class FreeChlorinePane(context: Context) extends DashboardTitledPane(context):
  text = context.headerFreeChlorine
  currentValue.text <== context.model.currentFreeChlorine.asString
  currentAverage.text <== context.model.averageFreeChlorine.asString