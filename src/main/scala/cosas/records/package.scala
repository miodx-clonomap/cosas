package ohnosequences.cosas

import ohnosequences.cosas._, types._, klists._

package object records {

  type EmptyRecordType = AnyRecordType.withKeys[unit]

  implicit def recordReorderSyntax[Vs <: AnyKList.withBound[AnyDenotation]](vs: Vs)
  : syntax.RecordReorderSyntax[Vs] =
    syntax.RecordReorderSyntax(vs)
}
