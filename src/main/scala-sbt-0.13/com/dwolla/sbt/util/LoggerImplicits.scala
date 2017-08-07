package com.dwolla.sbt.util

import xsbti.F0

import scala.language.implicitConversions

object LoggerImplicits {
  implicit def stringToF0(s: ⇒ String): F0[String] = new F0[String] {
    override def apply(): String = s
  }
}
