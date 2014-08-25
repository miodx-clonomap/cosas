package ohnosequences.pointless

import AnyTaggedType._, AnyTypeSet._
import scala.reflect.ClassTag

trait AnyProperty extends AnyTaggedType {

  val label: String
  val classTag: ClassTag[Raw]
}

/* Properties should be defined as case objects: `case object name extends Property[String]` */
class Property[V](implicit val classTag: ClassTag[V]) extends AnyProperty {

  val label = this.toString

  type Raw = V
}

class PropertyOps[P <: AnyProperty](val p: P) extends TaggedTypeOps(p) { self =>

  def is(value: RawOf[P]): Tagged[P] = self =>> value
}

object AnyProperty {

  implicit def propertyOps[P <: AnyProperty](p: P): PropertyOps[P] = new PropertyOps[P](p)

  implicit def hasPropertiesOps[T](t: T): HasPropertiesOps[T] = new HasPropertiesOps[T](t)

}

// TODO: separate as ops

/* Evidence that an arbitrary type `Smth` has property `P` */
@annotation.implicitNotFound(msg = "Can't prove that ${Smth} has property ${P}")
sealed class HasProperty[Smth, P <: AnyProperty]
/* or a set of properties `Ps` */
@annotation.implicitNotFound(msg = "Can't prove that ${Smth} has properties ${Ps}")
sealed class HasProperties[Smth, Ps <: AnyTypeSet.Of[AnyProperty]] //(implicit setBound: Ps isBoundedBy AnyProperty) 


object HasProperty {
  /* (T HasProperties Ps) & (P ∈ Ps) => (T HasProperty P) */
  implicit def fromSetToAProperty[T, Ps <: AnyTypeSet.Of[AnyProperty], P <: AnyProperty](implicit
      ps: T HasProperties Ps, ep: P ∈ Ps
    ):   T HasProperty P =
    new (T HasProperty P)
}

object HasProperties extends HasProperties_2 {
  /* (T HasProperties Ps) & (Qs ⊂ Ps) => (T HasProperties Qs) */

  implicit def fromSetToASubset[T, Ps <: AnyTypeSet.Of[AnyProperty], Qs <: AnyTypeSet.SubsetOf[Ps]]
    (implicit ne: Ps ~:!~ Qs, ps: T HasProperties Ps):
         T HasProperties Qs =
    new (T HasProperties Qs)
}

trait HasProperties_2 {
  implicit def fromSetToReordered[T, Ps <: AnyTypeSet.Of[AnyProperty], Qs <: AnyTypeSet.Of[AnyProperty]]
    (implicit ps: T HasProperties Ps, eq: Ps ~:~ Qs):
         T HasProperties Qs =
    new (T HasProperties Qs)
}

class HasPropertiesOps[T](t: T) {

  /* Handy way of creating an implicit evidence saying that this vertex type has that property */
  // def has[P <: AnyProperty](p: P): T HasProperty P = new (T HasProperty P)
  def has[Ps <: AnyTypeSet.Of[AnyProperty]](ps: Ps): T HasProperties Ps = new (T HasProperties Ps)

  // def get[P <: AnyProperty](p: P)
  //   (implicit get: T Get P): Tagged[P] = get(recEntry)

}


// TODO: restore this
// /* 
//   For a given arbitrary type `Smth`, filters any property set, 
//   leaving only those which have the `Smth HasProperty _` evidence
// */
// trait FilterProps[Smth, Ps <: TypeSet] extends DepFn1[Ps] {
//   type Out <: TypeSet
// }

// object FilterProps extends FilterProps2 {
//   // the case when there is this evidence (leaving the head)
//   implicit def consFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
//     (implicit
//       h: Smth HasProperty H,
//       t: Aux[Smth, T, OutT]
//     ): Aux[Smth, H :~: T, H :~: OutT] =
//       new FilterProps[Smth, H :~: T] { type Out = H :~: OutT
//         def apply(s: H :~: T): Out = s.head :~: t(s.tail)
//       }
// }

// trait FilterProps2 {
//   def apply[Smth, Ps <: TypeSet](implicit filt: FilterProps[Smth, Ps]): Aux[Smth, Ps, filt.Out] = filt

//   type Aux[Smth, In <: TypeSet, O <: TypeSet] = FilterProps[Smth, In] { type Out = O }
  
//   implicit def emptyFilter[Smth]: Aux[Smth, ∅, ∅] =
//     new FilterProps[Smth, ∅] {
//       type Out = ∅
//       def apply(s: ∅): Out = ∅
//     }

//   // the low-priority case when there is no evidence (just skipping head)
//   implicit def skipFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
//     (implicit t: Aux[Smth, T, OutT]): Aux[Smth, H :~: T, OutT] =
//       new FilterProps[Smth, H :~: T] { type Out = OutT
//         def apply(s: H :~: T): Out = t(s.tail)
//       }
// }


// /* This applies `FilterProps` to a list of `Smth`s (`Ts` here) */
// trait ZipWithProps[Ts <: TypeSet, Ps <: TypeSet] extends DepFn2[Ts, Ps] {
//   type Out <: TypeSet
// }

// object ZipWithProps {
//   def apply[Ts <: TypeSet, Ps <: TypeSet]
//     (implicit z: ZipWithProps[Ts, Ps]): Aux[Ts, Ps, z.Out] = z

//   type Aux[Ts <: TypeSet, Ps <: TypeSet, O <: TypeSet] = ZipWithProps[Ts, Ps] { type Out = O }
  
//   implicit def emptyZipWithProps[Ps <: TypeSet]: Aux[∅, Ps, ∅] =
//     new ZipWithProps[∅, Ps] {
//       type Out = ∅
//       def apply(s: ∅, ps: Ps): Out = ∅
//     }

//   implicit def consZipWithProps[H, T <: TypeSet, Ps <: TypeSet, OutT <: TypeSet]
//     (implicit 
//       h: FilterProps[H, Ps],
//       t: Aux[T, Ps, OutT]
//     ): Aux[H :~: T, Ps, (H, h.Out) :~: OutT] =
//       new ZipWithProps[H :~: T, Ps] { type Out = (H, h.Out) :~: OutT
//         def apply(s: H :~: T, ps: Ps): Out = (s.head, h(ps)) :~: t(s.tail, ps)
//       }
// }
