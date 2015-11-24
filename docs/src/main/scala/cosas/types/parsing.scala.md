
```scala
package ohnosequences.cosas.types

import ohnosequences.cosas._, fns._, klists._

// TODO update to DepFns
trait AnyDenotationParser {

  type Type <: AnyType
  val tpe: Type

  // the type used to denote Type
  type D <: Type#Raw

  type Value
  type From = (String, Value)

  val parser: Value => Option[D]

  val labelRep: String

  def apply(k: String, v: Value): Either[DenotationParserError, Type := D] = k match {

    case `labelRep` => parser(v).fold[Either[DenotationParserError, Type := D]](
        Left(ErrorParsingValue(tpe)(v))
      )(
        d => Right(tpe := d)
      )

    case _ => Left(WrongKey(tpe, k, labelRep))
  }
}

sealed trait DenotationParserError
case class ErrorParsingValue[Tpe <: AnyType, Value](val tpe: Tpe)(val from: Value)
extends DenotationParserError
case class WrongKey[Tpe <: AnyType](val tpe: Tpe, val got: String, val expected: String)
extends DenotationParserError

class DenotationParser[T <: AnyType, D0 <: T#Raw, V](
  val tpe: T,
  val labelRep: String
)(
  val parser: V => Option[D0]
)
extends AnyDenotationParser {

  type Type = T
  type D = D0
  type Value = V
}

case object AnyDenotationParser {

  implicit def genericParser[T <: AnyType { type Raw >: D }, D](implicit tpe: T): DenotationParser[T,D,D] =
    new DenotationParser(tpe, tpe.label)(d => Some(d))
}

trait ParseDenotationsError
case class KeyNotFound[V](val key: String, val map: Map[String,V]) extends ParseDenotationsError
case class ErrorParsing[PE <: DenotationParserError](val err: PE) extends ParseDenotationsError

class ParseDenotations[V, Ts <: AnyProductType] extends DepFn1[Map[String,V], Either[ParseDenotationsError,Ts#Raw]]

case object ParseDenotations {

  implicit def empty[V,X]
  : AnyApp1At[ParseDenotations[V,unit], Map[String,V]] { type Y =  Either[ParseDenotationsError,*[AnyDenotation]] } =
    App1 { map: Map[String,V] => Right(*[AnyDenotation]) }

  implicit def emptyParam[V, T <: AnyType, X]
  : AnyApp1At[ParseDenotations[V,In[T]], Map[String,V]] { type Y =  Either[ParseDenotationsError,*[AnyDenotation]] } =
    App1 { map: Map[String,V] => Right(*[AnyDenotation]) }

  implicit def nonEmpty[
    V,
    H <: Ts#Types#Bound { type Raw >: HR }, HR, Ts <: AnyProductType { type Raw >: Ds }, Ds <: AnyKList.withBound[AnyDenotation]
  ](implicit
    parseRest: AnyApp1At[ParseDenotations[V,Ts], Map[String,V]] { type Y  = Either[ParseDenotationsError,Ds] },
    parseH: DenotationParser[H,HR,V]
  )
  : AnyApp1At[ParseDenotations[V, H :×: Ts], Map[String,V]] { type Y = Either[ParseDenotationsError, (H := HR) :: Ds] } =
    App1 { map: Map[String,V] => {

      map.get(parseH.labelRep).fold[Either[ParseDenotationsError, (H := HR) :: Ds]](
        Left(KeyNotFound(parseH.labelRep, map))
      )(
        v => parseH(parseH.labelRep, v) fold (

          l => Left(ErrorParsing(l)),

          r => parseRest(map).fold[Either[ParseDenotationsError, (H := HR) :: Ds]] (
            err => Left(err),
            td  => Right(r :: (td: Ds))
          )
        )
      )
    }
  }
}

```




[test/scala/cosas/asserts.scala]: ../../../../test/scala/cosas/asserts.scala.md
[test/scala/cosas/DenotationTests.scala]: ../../../../test/scala/cosas/DenotationTests.scala.md
[test/scala/cosas/EqualityTests.scala]: ../../../../test/scala/cosas/EqualityTests.scala.md
[test/scala/cosas/DependentFunctionsTests.scala]: ../../../../test/scala/cosas/DependentFunctionsTests.scala.md
[test/scala/cosas/KListsTests.scala]: ../../../../test/scala/cosas/KListsTests.scala.md
[test/scala/cosas/RecordTests.scala]: ../../../../test/scala/cosas/RecordTests.scala.md
[test/scala/cosas/NatTests.scala]: ../../../../test/scala/cosas/NatTests.scala.md
[test/scala/cosas/TypeUnionTests.scala]: ../../../../test/scala/cosas/TypeUnionTests.scala.md
[main/scala/cosas/package.scala]: ../package.scala.md
[main/scala/cosas/types/package.scala]: package.scala.md
[main/scala/cosas/types/types.scala]: types.scala.md
[main/scala/cosas/types/parsing.scala]: parsing.scala.md
[main/scala/cosas/types/productTypes.scala]: productTypes.scala.md
[main/scala/cosas/types/syntax.scala]: syntax.scala.md
[main/scala/cosas/types/project.scala]: project.scala.md
[main/scala/cosas/types/denotations.scala]: denotations.scala.md
[main/scala/cosas/types/functionTypes.scala]: functionTypes.scala.md
[main/scala/cosas/types/serialization.scala]: serialization.scala.md
[main/scala/cosas/klists/replace.scala]: ../klists/replace.scala.md
[main/scala/cosas/klists/cons.scala]: ../klists/cons.scala.md
[main/scala/cosas/klists/klists.scala]: ../klists/klists.scala.md
[main/scala/cosas/klists/take.scala]: ../klists/take.scala.md
[main/scala/cosas/klists/package.scala]: ../klists/package.scala.md
[main/scala/cosas/klists/takeFirst.scala]: ../klists/takeFirst.scala.md
[main/scala/cosas/klists/toList.scala]: ../klists/toList.scala.md
[main/scala/cosas/klists/filter.scala]: ../klists/filter.scala.md
[main/scala/cosas/klists/pick.scala]: ../klists/pick.scala.md
[main/scala/cosas/klists/drop.scala]: ../klists/drop.scala.md
[main/scala/cosas/klists/map.scala]: ../klists/map.scala.md
[main/scala/cosas/klists/at.scala]: ../klists/at.scala.md
[main/scala/cosas/klists/syntax.scala]: ../klists/syntax.scala.md
[main/scala/cosas/klists/fold.scala]: ../klists/fold.scala.md
[main/scala/cosas/klists/noDuplicates.scala]: ../klists/noDuplicates.scala.md
[main/scala/cosas/klists/slice.scala]: ../klists/slice.scala.md
[main/scala/cosas/klists/find.scala]: ../klists/find.scala.md
[main/scala/cosas/records/package.scala]: ../records/package.scala.md
[main/scala/cosas/records/recordTypes.scala]: ../records/recordTypes.scala.md
[main/scala/cosas/records/syntax.scala]: ../records/syntax.scala.md
[main/scala/cosas/records/reorder.scala]: ../records/reorder.scala.md
[main/scala/cosas/typeUnions/typeUnions.scala]: ../typeUnions/typeUnions.scala.md
[main/scala/cosas/typeUnions/package.scala]: ../typeUnions/package.scala.md
[main/scala/cosas/fns/predicates.scala]: ../fns/predicates.scala.md
[main/scala/cosas/fns/instances.scala]: ../fns/instances.scala.md
[main/scala/cosas/fns/package.scala]: ../fns/package.scala.md
[main/scala/cosas/fns/syntax.scala]: ../fns/syntax.scala.md
[main/scala/cosas/fns/functions.scala]: ../fns/functions.scala.md
[main/scala/cosas/subtyping.scala]: ../subtyping.scala.md
[main/scala/cosas/witness.scala]: ../witness.scala.md
[main/scala/cosas/equality.scala]: ../equality.scala.md
[main/scala/cosas/Nat.scala]: ../Nat.scala.md
[main/scala/cosas/Bool.scala]: ../Bool.scala.md