package ohnosequences.pointless.impl

import scala.reflect.ClassTag

object property extends ohnosequences.pointless.anyProperty {

  // wire deps
  type representable = ohnosequences.pointless.impl.representable.type
  // import ohnosequences.pointless.impl.representable._

  type AnyProperty = PropertyImpl

  trait PropertyImpl extends representable.RepresentableImpl {

    val label: String
    val classTag: ClassTag[Raw]
  }

  class Property[V](val label: String)(implicit val classTag: ClassTag[V]) extends PropertyImpl {

    type Raw = V
  }

  implicit def propertyOps[P <: AnyProperty](p: P): PropertyOps[P] = PropertyOps[P](p)
  case class   PropertyOps[P <: AnyProperty](p: P)
    extends AnyPropertyOps[P](p)(representable.representableOps) {

    def label = property.label
    def classTag: ClassTag[_ <: P#Raw] = property.classTag
  }

}