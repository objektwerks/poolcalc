package pool.pane

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, SelectionMode, TableColumn, TableView}
import scalafx.scene.layout.{HBox, VBox}

import pool.{Context, Pool, unitOfMeasure}
import pool.dialog.PoolDialog

class PoolsPane(context: Context) extends VBox:
  spacing = 6
  padding = Insets(6)

  val model = context.model

  val label = new Label {
    text = context.labelPools
  }

  val tableView = new TableView[Pool]() {
    columns ++= List(
      new TableColumn[Pool, String] {
        text = context.tableName
        cellValueFactory = _.value.nameProperty
      },
      new TableColumn[Pool, String] {
        text = context.tableBuilt
        cellValueFactory = _.value.builtProperty
      },
      new TableColumn[Pool, String] {
        text = context.tableVolume
        cellValueFactory = _.value.volumeProperty
      },
      new TableColumn[Pool, String] {
        text = context.tableUnit
        cellValueFactory = _.value.unitProperty
      }
    )
    items = model.pools().fold( _ => ObservableBuffer[Pool](), pools => pools)
  }

  val addButton = new Button {
    text = context.paneAdd
  }

  val editButton = new Button {
    text = context.paneEdit
    disable = true
  }

  val toolBar = new HBox {
    spacing = 6
    children = List(addButton, editButton)
  }

  children = List(label, tableView, toolBar)

  tableView.selectionModel().selectionModeProperty.value = SelectionMode.Single
  
  tableView.selectionModel().selectedItemProperty().addListener { (_, _, selectedPool) =>
    // model.update executes a remove and add on items. the remove passes a null selectedPool!
    if selectedPool != null then
      model.selectedPoolId.value = selectedPool.id
      editButton.disable = false
  }

  addButton.onAction = { _ => add() }

  editButton.onAction = { _ => update() }

  def add(): Unit =
    PoolDialog(context, Pool()).showAndWait() match
      case Some(pool: Pool) => model.add(pool).fold(_ => (), pool => tableView.selectionModel().select(pool))
      case _ =>

  def update(): Unit =
    val selectedIndex = tableView.selectionModel().getSelectedIndex
    val pool = tableView.selectionModel().getSelectedItem.pool
    PoolDialog(context, pool).showAndWait() match
      case Some(pool: Pool) =>
        model.update(selectedIndex, pool)
        tableView.selectionModel().select(selectedIndex)
      case _ =>