
```scala
package ohnosequences.cosas.klists

import ohnosequences.cosas._, fns._
```

Does cons if predicate is true or skips the element if not

```scala
class consIf[P <: AnyPredicate] extends DepFn2[
  P#In1, AnyKList.Of[P#In1],
  AnyKList.Of[P#In1]
]

case object consIf extends consIf_false {

  implicit def pTrue[
    P <: AnyPredicate { type In1 >: H },
    H <: T#Bound, T <: AnyKList { type Bound <: P#In1 }
  ](implicit
    ev: P isTrueOn H
  ): AnyApp2At[consIf[P],
      H, T
    ] { type Y = H :: T } =
    App2 { (h: H, t: T) => h :: t }
}

trait consIf_false {

  implicit def pFalse[
    P <: AnyPredicate { type In1 >: H },
    H <: T#Bound, T <: AnyKList { type Bound <: P#In1 }
  ]
  : AnyApp2At[consIf[P],
      H, T
    ] { type Y = T } =
    App2 { (_, t: T) => t }
}
```

p.filter(l) is just consIf(p).foldRight(KNil)(l)

```scala
class filter[P <: AnyPredicate] extends DepFn2[
  P, AnyKList.Of[P#In1],
  AnyKList.Of[P#In1]
]

case object filter {

  implicit def default[
    P <: AnyPredicate { type In1 >: O#Bound },
    L <: AnyKList { type Bound <: P#In1 },
    O <: AnyKList
  ](implicit
    appFold: AnyApp3At[FoldRight[consIf[P]],
      consIf[P], *[L#Bound], L
    ] { type Y = O }
  ): AnyApp2At[filter[P],
      P, L
    ] { type Y = O } =
    App2 { (p: P, l: L) => appFold(new consIf[P], *[L#Bound], l) }
}

```




[test/scala/cosas/DenotationTests.scala]: ../../../../test/scala/cosas/DenotationTests.scala.md
[test/scala/cosas/EqualityTests.scala]: ../../../../test/scala/cosas/EqualityTests.scala.md
[test/scala/cosas/DependentFunctionsTests.scala]: ../../../../test/scala/cosas/DependentFunctionsTests.scala.md
[test/scala/cosas/KListsTests.scala]: ../../../../test/scala/cosas/KListsTests.scala.md
[test/scala/cosas/RecordTests.scala]: ../../../../test/scala/cosas/RecordTests.scala.md
[test/scala/cosas/NatTests.scala]: ../../../../test/scala/cosas/NatTests.scala.md
[test/scala/cosas/TypeUnionTests.scala]: ../../../../test/scala/cosas/TypeUnionTests.scala.md
[main/scala/cosas/package.scala]: ../package.scala.md
[main/scala/cosas/types/package.scala]: ../types/package.scala.md
[main/scala/cosas/types/types.scala]: ../types/types.scala.md
[main/scala/cosas/types/parsing.scala]: ../types/parsing.scala.md
[main/scala/cosas/types/productTypes.scala]: ../types/productTypes.scala.md
[main/scala/cosas/types/syntax.scala]: ../types/syntax.scala.md
[main/scala/cosas/types/project.scala]: ../types/project.scala.md
[main/scala/cosas/types/denotations.scala]: ../types/denotations.scala.md
[main/scala/cosas/types/functionTypes.scala]: ../types/functionTypes.scala.md
[main/scala/cosas/types/serialization.scala]: ../types/serialization.scala.md
[main/scala/cosas/klists/replace.scala]: replace.scala.md
[main/scala/cosas/klists/cons.scala]: cons.scala.md
[main/scala/cosas/klists/klists.scala]: klists.scala.md
[main/scala/cosas/klists/take.scala]: take.scala.md
[main/scala/cosas/klists/package.scala]: package.scala.md
[main/scala/cosas/klists/takeFirst.scala]: takeFirst.scala.md
[main/scala/cosas/klists/toList.scala]: toList.scala.md
[main/scala/cosas/klists/filter.scala]: filter.scala.md
[main/scala/cosas/klists/pick.scala]: pick.scala.md
[main/scala/cosas/klists/drop.scala]: drop.scala.md
[main/scala/cosas/klists/map.scala]: map.scala.md
[main/scala/cosas/klists/at.scala]: at.scala.md
[main/scala/cosas/klists/syntax.scala]: syntax.scala.md
[main/scala/cosas/klists/fold.scala]: fold.scala.md
[main/scala/cosas/klists/noDuplicates.scala]: noDuplicates.scala.md
[main/scala/cosas/klists/slice.scala]: slice.scala.md
[main/scala/cosas/klists/find.scala]: find.scala.md
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