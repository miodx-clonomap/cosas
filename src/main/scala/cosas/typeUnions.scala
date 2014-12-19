package ohnosequences.cosas

object typeUnions {

  import shapeless.{ <:!< }
  import shapeless.Nat._


  /* There are two type-level constructors of a type union.
     A generic term looks like `either[A]#or[B]#or[C]`. */
  trait AnyTypeUnion {

    type or[Y] <: AnyTypeUnion
    type union // this is like return
    type Arity <: shapeless.Nat
    type PrevBoundNot
    type intersection
  }

  // object AnyTypeUnion {

  private[cosas] type not[-T] = (T => Nothing)
  private[cosas] type just[+T] = not[not[T]]

  type empty = empty.type
  object empty extends AnyTypeUnion {

    type Arity = _0
    type union = not[not[Nothing]]
    type Head = Nothing

    type PrevBoundNot = not[Nothing] 
    type or[Z] = typeUnions.or[empty, Z]
    type intersection = Any
  }

  sealed trait either[X] extends AnyTypeUnion {

    type Arity = _1
    type union = not[not[X]]
    type Head = X

    type PrevBoundNot = not[X] 
    type or[Z] = typeUnions.or[either[X], Z]
    type intersection = X
  }

  sealed trait or[T <: AnyTypeUnion, S] extends AnyTypeUnion {

    type Head = S
    type Arity = shapeless.Succ[T#Arity]
    type union = not[not[S] with T#PrevBoundNot]
    type PrevBoundNot = not[S] with T#PrevBoundNot
    type or[Z] = typeUnions.or[typeUnions.or[T,S], Z]
    type intersection = T#intersection with S
  }

  type :∨:[S, T <: AnyTypeUnion] = (T or S)


  /*
    Type-level operations
  */
  @annotation.implicitNotFound(msg = "Can't prove that ${X} is one of ${U}")
  type    isOneOf[X, U <: AnyTypeUnion] = just[X] <:<  U#union

  @annotation.implicitNotFound(msg = "Can't prove that ${X} is not one of ${U}")
  type isNotOneOf[X, U <: AnyTypeUnion] = just[X] <:!< U#union

  final type oneOf[U <: AnyTypeUnion] = {
    type    is[X] = X    isOneOf U
    type isNot[X] = X isNotOneOf U
  }

  type arity[U <: AnyTypeUnion] = U#Arity

  @annotation.implicitNotFound(msg = "Can't prove that ${V} is subunion of ${U}")
  type    isSubunionOf[V <: AnyTypeUnion, U <: AnyTypeUnion] = V#union <:<  U#union

  @annotation.implicitNotFound(msg = "Can't prove that ${V <: AnyTypeUnion} is not subunion of ${U}")
  type isNotSubunionOf[V <: AnyTypeUnion, U <: AnyTypeUnion] = V#union <:!< U#union

}