package com.example

import akka.actor._

case class Order(orderItems: Map[String, OrderItem]) {
  val grandTotal: Double = orderItems.values.map(_.price).sum

  override def toString = {
    s"Order(Order Items: $orderItems Totaling: $grandTotal)"
  }
}

case class OrderItem(id: String, itemType: String, description: String, price: Double) {
  override def toString = {
    s"OrderItem($id, $itemType, '$description', $price)"
  }
}

object SplitterDriver extends CompletableApp(4) {
}
