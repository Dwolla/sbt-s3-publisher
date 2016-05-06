package com.dwolla.util

trait Environment {
  def get(name: String): Option[String]
}

object SystemEnvironment extends Environment {
  override def get(name: String): Option[String] = Option(System.getenv(name))
}
