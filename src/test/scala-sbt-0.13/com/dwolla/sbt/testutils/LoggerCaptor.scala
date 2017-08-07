package com.dwolla.sbt.testutils

import org.specs2.mock.mockito.ArgumentCapture
import xsbti.F0

import scala.language.implicitConversions
import scala.reflect.ClassTag

object LoggerCaptor {
  def loggerCapture(): ArgumentCapture[F0[String]] = new ArgumentCapture[F0[String]]

  implicit def f0ToSupplier[T](f0: F0[T]): Gettable[T] = new Gettable[T] {
    override def get(): T = f0()
  }
}

/**
  * Tests use the `java.util.function.Supplier` interface, but that may not be available with sbt 0.13 pre-Java 8
  */
trait Gettable[T] {
  def get(): T
}
