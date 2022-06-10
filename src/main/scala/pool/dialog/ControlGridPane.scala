package pool.dialog

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, Region}
import scalafx.scene.text.TextAlignment;

class ControlGridPane(controls: List[(String, Region)]) extends GridPane:
  hgap = 6
  vgap = 6
  padding = Insets(top = 6, right = 6, bottom = 6, left = 6)
  var row = 0
  for ((label, control) <- controls)
    val gridLabel = new Label {
      textAlignment = TextAlignment.Left
      text = label 
    }
    add(gridLabel, columnIndex = 0, rowIndex = row)
    add(control, columnIndex = 1, rowIndex = row)
    row = row + 1