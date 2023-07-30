package eu.joaocosta.interim.api

import scala.deriving.Mirror

/** A mutable reference to a variable.
  *
  * When a function receives a Ref as an argument, it will probably mutate it.
  */
final case class Ref[T](var value: T):

  /** Assigns a value to this Ref.
    * Shorthand for `ref.value = x`
    */
  def :=(newValue: T): this.type =
    value = newValue
    this

object Ref:

  /** Gets a value from a Ref or from a plain value.
    */
  inline def get[T](x: T | Ref[T]): T = inline x match
    case value: T    => value
    case ref: Ref[T] => ref.value

  /** Sets a value from a Ref or from a plain value.
    *
    * The new value is returned. Refs will be mutated while immutable values will not.
    */
  inline def set[T](x: T | Ref[T], v: T): T = modify(x, _ => v)

  /** Modifies a value from a Ref or from a plain value.
    *
    * The new value is returned. Refs will be mutated while immutable values will not.
    */
  inline def modify[T](x: T | Ref[T], f: T => T): T = inline x match
    case value: T =>
      f(value)
    case ref: Ref[T] =>
      ref.value = f(ref.value)
      ref.value

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
