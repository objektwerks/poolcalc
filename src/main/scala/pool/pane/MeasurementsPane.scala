package pool.pane

import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, SelectionMode, TableColumn, TableView}
import scalafx.scene.layout.{HBox, VBox}

import pool.{Measurement, Context}
import pool.dialog.MeasurementDialog

class MeasurementsPane(context: Context) extends VBox with AddEditToolbar(context):
  spacing = 6
  padding = Insets(6)

  val model = context.model

  val label = new Label {
    text = context.labelMeasurements
  }

  val tableView = new TableView[Measurement]() {
    columns ++= List(
      new TableColumn[Measurement, String] {
        text = context.tableFreeChlorine
        cellValueFactory = _.value.freeChlorineProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableCombinedChlorine
        cellValueFactory = _.value.combinedChlorineProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableTotalChlorine
        cellValueFactory = _.value.totalChlorineProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tablePh
        cellValueFactory = _.value.phProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableCalciumHardness
        cellValueFactory = _.value.calciumHardnessProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableTotalAlkalinity
        cellValueFactory = _.value.totalAlkalinityProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableCyanuricAcid
        cellValueFactory = _.value.cyanuricAcidProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableTotalBromine
        cellValueFactory = _.value.totalBromineProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableTemperature
        cellValueFactory = _.value.temperatureProperty
      },
      new TableColumn[Measurement, String] {
        text = context.tableMeasured
        cellValueFactory = _.value.measuredProperty
      }
    )
    items = model.measurements(model.selectedPoolId.value).fold( _ => ObservableBuffer[Measurement](), measurements => measurements)
  }

  children = List(label, tableView, toolbar)

