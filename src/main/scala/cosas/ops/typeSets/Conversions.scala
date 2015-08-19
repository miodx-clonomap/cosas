/* ## Conversions to HList and List */

package ohnosequences.cosas.ops.typeSets

import ohnosequences.cosas._, fns._, typeSets._, types._
import shapeless._, poly._

case object id extends Poly1 { implicit def default[T] = at[T]((t:T) => t) }


@annotation.implicitNotFound(msg = "Can't convert ${S} to an HList")
trait ToHList[S <: AnyTypeSet] extends Fn1[S] with OutBound[HList]

object ToHList {

  def apply[S <: AnyTypeSet](implicit toHList: ToHList[S]): ToHList[S] = toHList

  implicit def any[S <: AnyTypeSet, O <: HList]
    (implicit
      mapper: MapToHList[id.type, S] { type Out = O }
    ):  ToHList[S] with Out[O] =
    new ToHList[S] with Out[O] {
      def apply(s: S): Out = mapper(s)
    }

}


@annotation.implicitNotFound(msg = "Can't convert ${L} to a TypeSet (maybe element types are not distinct)")
trait FromHList[L <: HList] extends Fn1[L] with OutBound[AnyTypeSet]

object FromHList {

  def apply[L <: HList](implicit fromHList: FromHList[L]): FromHList[L] = fromHList

  implicit def hnil[N <: HNil]:
        FromHList[N] with Out[∅] =
    new FromHList[N] with Out[∅] {
      def apply(l: N): Out = ∅
    }

  implicit def cons[H, T <: HList, OutT <: AnyTypeSet]
    (implicit
      rest: FromHList[T] { type Out = OutT },
      check: H ∉ OutT
    ):  FromHList[H :: T] with Out[H :~: OutT] =
    new FromHList[H :: T] with Out[H :~: OutT] {
      def apply(l: H :: T): Out = l.head :~: rest(l.tail)
    }

}


@annotation.implicitNotFound(msg = "Can't convert ${S} to a List")
trait ToList[S <: AnyTypeSet] extends Fn1[S] with OutInContainer[List]

object ToList {

  def apply[S <: AnyTypeSet](implicit toList: ToList[S]): ToList[S] = toList

  // case object id_ extends Poly1 { implicit def default[T <: X, X] = at[T]{ (t: T) => (t: X) } }

  // implicit def any[S <: AnyTypeSet, O](implicit
  //     mapper: (id_.type MapToList S) with wrapped[O]
  //   ):  ToList[S] with InContainer[O] =
  //   new ToList[S] with InContainer[O] { def apply(s: S): Out = mapper(s) }

  implicit def empty[X]:
        ToList[∅] with InContainer[X] =
    new ToList[∅] with InContainer[X] { def apply(s: ∅): Out = Nil }

  implicit def one[X, H <: X]:
        ToList[H :~: ∅] with InContainer[X] =
    new ToList[H :~: ∅] with InContainer[X] { def apply(s: H :~: ∅): Out = List[X](s.head) }

  implicit def cons2[X, H1 <: X, H2 <: X, T <: AnyTypeSet]
    (implicit
      lt: ToList[H2 :~: T] { type O = X }
    ):  ToList[H1 :~: H2 :~: T] with InContainer[X] =
    new ToList[H1 :~: H2 :~: T] with InContainer[X] {

      def apply(s: H1 :~: H2 :~: T): Out = s.head :: lt(s.tail.head :~: s.tail.tail)
    }

}


@annotation.implicitNotFound(msg = "Can't convert ${S} to Map[${K}, ${V}]. Note that a set of denotations is expected")
trait ToMap[S <: AnyTypeSet, K, V] extends Fn1[S] with Out[Map[K, V]]

object ToMap {

  implicit def empty[K, V]:
        ToMap[∅, K, V] =
    new ToMap[∅, K, V] { def apply(s: ∅): Out = Map[K, V]() }

  implicit def cons[
    K <: AnyType, V,
    D <: AnyDenotation { type Tpe <: K; type Value <: V },
    T <: AnyTypeSet
  ](implicit
    rest: ToMap[T, K, V],
    key: D#Tpe
  ):  ToMap[D :~: T, K, V] =
  new ToMap[D :~: T, K, V] {

    def apply(s: D :~: T): Out =  rest(s.tail) + (key -> s.head.value)
  }
}


@annotation.implicitNotFound(msg = "Can't parse typeset ${S} from ${X}")
// NOTE: it should be restricted to AnyTypeSet.Of[AnyType], when :~: is known to return the same thing
trait ParseFrom[S <: AnyTypeSet, X] extends Fn2[S, X] with OutBound[AnyTypeSet]

object ParseFrom {

  def apply[S <: AnyTypeSet, X]
    (implicit parser: ParseFrom[S, X]): ParseFrom[S, X] = parser

  implicit def empty[X]:
        (∅ ParseFrom X) with Out[∅] =
    new (∅ ParseFrom X) with Out[∅] {

      def apply(s: ∅, x: X): Out = ∅
    }

  implicit def cons[X,
    H <: AnyType, T <: AnyTypeSet, TO <: AnyTypeSet
  ](implicit
    f: (H, X) => (ValueOf[H], X),
    t: ParseFrom[T, X] { type Out = TO }
  ):  ((H :~: T) ParseFrom X) with Out[ValueOf[H] :~: TO] =
  new ((H :~: T) ParseFrom X) with Out[ValueOf[H] :~: TO] {

    def apply(s: H :~: T, x: X): Out = {
      val (head, rest) = f(s.head, x)
      head :~: t(s.tail, rest)
    }
  }
}


@annotation.implicitNotFound(msg = "Can't serialize typeset ${S} to ${X}")
trait SerializeTo[S <: AnyTypeSet, X] extends Fn1[S] with Out[X]

object SerializeTo {

  def apply[S <: AnyTypeSet, X]
    (implicit serializer: S SerializeTo X): S SerializeTo X = serializer

  implicit def empty[X](implicit m: Monoid[X]):
        (∅ SerializeTo X) =
    new (∅ SerializeTo X) {

      def apply(r: ∅): Out = m.id
    }

  implicit def cons[X,
    H, T <: AnyTypeSet
  ](implicit
    m: Monoid[X],
    f: H => X,
    t: T SerializeTo X
  ):  ((H :~: T) SerializeTo X) =
  new ((H :~: T) SerializeTo X) {

    def apply(s: H :~: T): Out = m.op(f(s.head), t(s.tail))
  }

}

trait AnyMonoid {
  type M
  def id: M
  def op(a: M, b: M): M
}

trait Monoid[T] extends AnyMonoid { type M = T }

object AnyMonoid {

  implicit def mapMonoid[X, Y]: Monoid[Map[X, Y]] = new Monoid[Map[X, Y]] {

    def id: M = Map[X, Y]()
    def op(a: M, b: M): M = a ++ b
  }

  implicit def listMonoid[X]: Monoid[List[X]] = new Monoid[List[X]] {

    def id: M = List[X]()
    def op(a: M, b: M): M = a ++ b
  }
}
