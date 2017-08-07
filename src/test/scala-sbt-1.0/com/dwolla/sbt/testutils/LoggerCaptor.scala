package com.dwolla.sbt.testutils

import java.util.function.Supplier

import org.specs2.mock.mockito.ArgumentCapture

import scala.reflect.ClassTag

object LoggerCaptor {
  def loggerCapture(): ArgumentCapture[Supplier[String]] = new ArgumentCapture[Supplier[String]]
}
