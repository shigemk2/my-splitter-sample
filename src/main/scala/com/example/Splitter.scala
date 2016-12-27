package com.example

import akka.actor._

case class Order(orderItems: Map[String, OrderItem]) {
  val grandTotal: Double = orderItems.values.map(_.price).sum

  override def toString = {
    s"Order(Order Items: $orderItems Totaling: $grandTotal)"
  }
}

object SplitterDriver extends CompletableApp(4) {
}
