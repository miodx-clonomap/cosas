package ohnosequences.cosas.ops.types

import ohnosequences.cosas._, types._, typeSets._, fns._

trait ParseDenotations[Denotations <: AnyTypeSet, V]
extends Fn1[Map[String,V]] {

  type Out = (Either[AnyDenotationsParsingError, Denotations], Map[String,V])
}

trait AnyDenotationsParsingError
case class KeyNotFound[V](val key: String, val map: Map[String,V]) extends AnyDenotationsParsingError
case class ErrorParsing[PE <: AnyTypeParsingError](val err: PE) extends AnyDenotationsParsingError

case object ParseDenotations {

  // def apply[Types <: AnyTypeSet, V]
  //   (implicit parser: ParsePropertiesFrom[F, V]): ParsePropertiesFrom[F, V] = parser

  implicit def empty[V]: ParseDenotations[∅,V] =
    new ParseDenotations[∅,V] {

      def apply(map: Map[String,V]): Out = (Right(∅), map)
    }

  implicit def cons[
    V,
    H <: AnyType, TD <: AnyTypeSet,
    HR <: H#Raw,
    PH <: AnyDenotationParser { type Type = H; type Value = V; type D = HR },
    PT <: ParseDenotations[TD,V]
  ](implicit
    parseH: PH,
    parseT: PT
  ): ParseDenotations[(H := HR) :~: TD, V] = new ParseDenotations[(H := HR) :~: TD, V] {


    def apply(map: Map[String,V]): Out = parseT(map) match {

      case (Right(td), map) => (map get parseH.labelRep) match {

        case None => (Left(KeyNotFound(parseH.labelRep, map)), map)

        case Some(v) => ( parseH.parse((parseH.labelRep, v)) ).fold[Out](
          err => ( Left(ErrorParsing(err)), map ),
          { hd: PH#Type := PH#D => (Right[AnyDenotationsParsingError, (H := HR) :~: TD]( hd :~: (td: TD) ), map) }
        )
      }

      case (Left(err), map) => (Left(err), map)
    }

    //   val errOrTM: (Either[AnyDenotationsParsingError, PT#Denotations], Map[String,V0]) =
    //     parseT(map)
    //
    //   errOrTM._1.fold(
    //     err => (Left(err), errOrTM._1),
    //     td  => (map get parseH.labelRep) match {
    //
    //         case None => (Left(KeyNotFound(parseH.labelRep, map)), map)
    //
    //         case Some(v) => ( ( (parseH:PH).parse(parseH.labelRep, v: PH#V) ): Either[AnyTypeParsingError, PH#Type := PH#D] ).fold[Out](
    //           err => ( Left(ErrorParsing(err)), map ),
    //           { hd: PH#Type := PH#D => (Right[AnyDenotationsParsingError, Denotations]( hd :~: (td: PT#Denotations) ), map) }
    //         )
    //       }
    //   )
    //   ???
    // }

    // parseT(map) match {
    //
    //   case (Right(td), map) => (map get parseH.labelRep) match {
    //
    //     case None => (Left(KeyNotFound(parseH.labelRep, map)), map)
    //
    //     case Some(v) => ( parseH parse (parseH.labelRep, v) ).fold[Out](
    //       err => ( Left(ErrorParsing(err)), map ),
    //       { hd: PH#Type := PH#D => (Right[AnyDenotationsParsingError, Denotations]( hd :~: (td: PT#Denotations) ), map) }
    //     )
    //   }
    //
    //   case (Left(err), map) => (Left(err), map)
    // }
  }
}
