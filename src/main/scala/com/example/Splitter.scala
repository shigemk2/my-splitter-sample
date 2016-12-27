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

case class OrderPlaced(order: Order)
case class TypeAItemOrdered(orderItem: OrderItem)
case class TypeBItemOrdered(orderItem: OrderItem)
case class TypeCItemOrdered(orderItem: OrderItem)

object SplitterDriver extends CompletableApp(4) {
}

class OrderRouter extends Actor {
  val orderItemTypeAProcessor = context.actorOf(Props[OrderItemTypeAProcessor], "orderItemTypeAProcessor")
  val orderItemTypeBProcessor = context.actorOf(Props[OrderItemTypeBProcessor], "orderItemTypeBProcessor")
  val orderItemTypeCProcessor = context.actorOf(Props[OrderItemTypeCProcessor], "orderItemTypeCProcessor")

  def receive = {
    case OrderPlaced(order) =>
      println(order)
      order.orderItems foreach { case (itemType, orderItem) => itemType match {
        case "TypeA" =>
          println(s"OrderRouter: routing $itemType")
          orderItemTypeAProcessor ! TypeAItemOrdered(orderItem)
        case "TypeB" =>
          println(s"OrderRouter: routing $itemType")
          orderItemTypeBProcessor ! TypeBItemOrdered(orderItem)
        case "TypeC" =>
          println(s"OrderRouter: routing $itemType")
          orderItemTypeCProcessor ! TypeCItemOrdered(orderItem)
      }}

      SplitterDriver.completedStep()
    case _ =>
      println("OrderRouter: received unexpected message")
  }
}

class OrderItemTypeAProcessor extends Actor {
  def receive = {
    case TypeAItemOrdered(orderItem) =>
      println(s"OrderItemTypeAProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeAProcessor: received unexpected message")
  }
}
