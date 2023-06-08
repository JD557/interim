package eu.joaocosta.interim.api

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

object Components extends Components

trait Components:

  type Component[+T] = (inputState: InputState, uiState: UiState) ?=> T

  final def button(
      id: ItemId,
      area: Rect,
      label: Option[(String, Int)] = None,
      skin: ButtonSkin = ButtonSkin.Default()
  ): Component[Boolean] =
    val buttonArea = skin.buttonArea(area)
    val itemStatus = UiState.registerItem(id, buttonArea)
    skin.renderButton(area, label, itemStatus)
    itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false

  final def checkbox(id: ItemId, area: Rect, skin: CheckboxSkin = CheckboxSkin.Default())(
      value: Boolean
  ): Component[Boolean] =
    val checkboxArea = skin.checkboxArea(area)
    val itemStatus   = UiState.registerItem(id, checkboxArea)
    skin.renderCheckbox(area, value, itemStatus)
    if (itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false) !value
    else value

  final def slider(id: ItemId, area: Rect, min: Int, max: Int, skin: SliderSkin = SliderSkin.Default())(
      value: Int
  ): Component[Int] =
    val sliderArea   = skin.sliderArea(area)
    val sliderSize   = skin.sliderSize
    val range        = max - min
    val itemStatus   = UiState.registerItem(id, sliderArea)
    val clampedValue = math.max(min, math.min(value, max))
    skin.renderSlider(area, min, clampedValue, max, itemStatus)
    if (itemStatus.active)
      if (area.w > area.h)
        val mousePos = summon[InputState].mouseX - sliderArea.x - sliderSize / 2
        val maxPos   = sliderArea.w - sliderSize
        math.max(min, math.min(min + (mousePos * range) / maxPos, max))
      else
        val mousePos = summon[InputState].mouseY - sliderArea.y - sliderSize / 2
        val maxPos   = sliderArea.h - sliderSize
        math.max(min, math.min((mousePos * range) / maxPos, max))
    else value

  final def textInput(id: ItemId, area: Rect, skin: TextInputSkin = TextInputSkin.Default())(
      value: String
  ): Component[String] =
    val textInputArea = skin.textInputArea(area)
    val itemStatus    = UiState.registerItem(id, textInputArea)
    skin.renderTextInput(area, value, itemStatus)
    if (itemStatus.keyboardFocus) summon[InputState].appendKeyboardInput(value)
    else value
