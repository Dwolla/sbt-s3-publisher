package com.dwolla.sbt.util

import java.util.function.Supplier

import scala.language.implicitConversions

object LoggerImplicits {
  implicit def stringToSupplier(s: ⇒ String): Supplier[String] = () ⇒ s
}
