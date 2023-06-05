package eu.joaocosta.guila

type ItemId = (Int | String) | List[(Int | String)]

object ItemId:
  extension (itemId: ItemId)
    def toIdList: List[(Int | String)] =
      itemId match {
        case int: Int                   => List(int)
        case str: String                => List(str)
        case list: List[(Int | String)] => list
      }

  extension (parentId: ItemId)
    def |>(childId: ItemId): ItemId =
      parentId.toIdList ++ childId.toIdList
