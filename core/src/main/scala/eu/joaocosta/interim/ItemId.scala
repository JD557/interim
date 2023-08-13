package eu.joaocosta.interim

import scala.annotation.targetName

/** Identifier of an item. Should be unique for each item.
  *
  * Can be either a Int, a String, or a sequence of that (which is especially useful for composite components).
  */
type ItemId = (Int | String) | List[(Int | String)]

/** Helper method to convert an ItemId into a List
  */
extension (itemId: ItemId)
  def toIdList: List[(Int | String)] =
    itemId match {
      case int: Int                   => List(int)
      case str: String                => List(str)
      case list: List[(Int | String)] => list
    }

/** Operator to add a child to an item id. Useful for composite components.
  */
extension (parentId: ItemId)
  @targetName("addChild")
  def |>(childId: ItemId): ItemId =
    parentId.toIdList ++ childId.toIdList
