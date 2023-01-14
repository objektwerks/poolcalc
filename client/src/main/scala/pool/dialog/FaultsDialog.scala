package pool.dialog

import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, Dialog, TableColumn, TableView}
import scalafx.scene.layout.VBox

import pool.{Fault, Context}
import pool.Client

final class FaultsDialog(context: Context) extends Dialog[Unit]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogFaults

  val model = context.model

  val tableView = new TableView[Fault]():
    columns ++= List(
      new TableColumn[Fault, String]:
        text = context.headerOccurred
        cellValueFactory = _.value.occurredProperty
      ,
      new TableColumn[Fault, String]:
        prefWidth = 425
        text = context.headerFault
        cellValueFactory = _.value.causeProperty
    )
    items = model.observableFaults

  dialogPane().buttonTypes = List(ButtonType.Close)
  dialogPane().content = new VBox:
    prefWidth = 600
    prefHeight = 200
    spacing = 6
    children = List(tableView)