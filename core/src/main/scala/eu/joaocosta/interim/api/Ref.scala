package eu.joaocosta.interim.api

import scala.annotation.targetName
import scala.deriving.Mirror

/** A mutable reference to a variable.
  *
  * When a function receives a Ref as an argument, it will probably mutate it.
  */
final case class Ref[T](private var value: T):
  /** Returns the value of this Ref.
    */
  def get: T = value

  /** Assigns a value to this Ref.
    */
  @targetName("set")
  def :=(newValue: T): this.type =
    value = newValue
    this

  /** Modifies the value pf this Ref.
    * Shorthand for `ref := f(ref.value)`
    */
  def modify(f: T => T): this.type =
    value = f(value)
    this

object Ref:

  /** Creates a Ref that can be used inside a block and returns that value.
    *
    * Useful to set temporary mutable variables.
    */
  def withRef[T](initialValue: T)(block: Ref[T] => Unit): T =
    val ref = Ref(initialValue)
    block(ref)
    ref.value

  /** Destructures an object into a tuple of Refs that can be used inside the block.
    *  In the end, a new object is returned with the updated values
    *
    * Useful to set temporary mutable variables.
    */
  def withRefs[T <: Product](initialValue: T)(using mirror: Mirror.ProductOf[T])(
      block: Tuple.Map[mirror.MirroredElemTypes, Ref] => Unit
  ): T =
    val tuple: mirror.MirroredElemTypes      = Tuple.fromProductTyped(initialValue)
    val refTuple: Tuple.Map[tuple.type, Ref] = tuple.map([T] => (x: T) => Ref(x))
    block(refTuple.asInstanceOf)
    type UnRef[T] = T match { case Ref[a] => a }
    val updatedTuple: mirror.MirroredElemTypes =
      refTuple.map([T] => (x: T) => x.asInstanceOf[Ref[_]].value.asInstanceOf[UnRef[T]]).asInstanceOf
    mirror.fromTuple(updatedTuple)

  /** Wraps this value into a Ref and passes it to a block, returning the final value of the ref.
    *
    * Useful to set temporary mutable variables.
    */
  extension [T](x: T) def asRef(block: Ref[T] => Unit): T = withRef(x)(block)

  /** Destructures this value into multiple Refs and passes it to a block, returning the final value of the ref.
    *
    * Useful to set temporary mutable variables.
    */
  extension [T <: Product](x: T)
    def asRefs(using mirror: Mirror.ProductOf[T])(block: Tuple.Map[mirror.MirroredElemTypes, Ref] => Unit): T =
      withRefs(x)(block)
