package pool.pane.dashboard

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{Border, Priority, VBox}
import scalafx.scene.paint.Paint
import scalafx.scene.text.TextAlignment;

import pool.Context
import pool.dialog.ControlGridPane

abstract class DashboardTitledPane(context: Context) extends VBox:
  spacing = 6
  padding = Insets(6)
  hgrow = Priority.Always
  vgrow = Priority.Always
  border = Border.stroke(Paint.valueOf("lightgray"))

  val title = new Label {
    textAlignment = TextAlignment.Center
  }

  val currentValue = new Label {
    textAlignment = TextAlignment.Center
    text = "0"
  }
  
  val currentAverage = new Label {
    textAlignment = TextAlignment.Center
    text = "0"
  }

  val controls = List[(String, Label)](
    context.labelCurrent -> currentValue,
    context.labelAverage -> currentAverage
  )
  
  children = List(title, ControlGridPane(controls))