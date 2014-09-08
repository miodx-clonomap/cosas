/*
## Popping an element from a set

It's like `Lookup`, but it removes the element

*/

package ohnosequences.pointless.ops.record

import ohnosequences.pointless._
import AnyFn._, AnyType._, AnyTypeSet._, AnyRecord._
import ops.typeSet._

@annotation.implicitNotFound(msg = "Can't get property ${P} of the record ${R}")
trait Get[R <: AnyRecord, P <: AnyProperty] 
  extends Fn1[RawOf[R]] with Out[ValueOf[P]]

object Get {

  implicit def getter[R <: AnyRecord, P <: AnyProperty]
    (implicit 
      lookup: RawOf[R] Lookup ValueOf[P]
    ):  Get[R, P] = 
    new Get[R, P] { def apply(recRaw: RawOf[R]): Out = lookup(recRaw) }

}
