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
  val orderRouter = system.actorOf(Props[OrderRouter], "orderRouter")
  val orderItem1 = OrderItem("1", "TypeA", "An item of type A.", 23.95)
  val orderItem2 = OrderItem("2", "TypeB", "An item of type B.", 99.95)
  val orderItem3 = OrderItem("3", "TypeC", "An item of type C.", 14.95)
  val orderItem4 = OrderItem("4", "TypeC", "Extra item of type C.", 99.999)
  val orderItems = Map(orderItem1.itemType -> orderItem1, orderItem2.itemType -> orderItem2, orderItem3.itemType -> orderItem3, orderItem4.itemType -> orderItem4)
  orderRouter ! OrderPlaced(Order(orderItems))
  awaitCompletion
  println("Splitter: is completed.")
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

class OrderItemTypeBProcessor extends Actor {
  def receive = {
    case TypeBItemOrdered(orderItem) =>
      println(s"OrderItemTypeBProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeBProcessor: received unexpected message")
  }
}

class OrderItemTypeCProcessor extends Actor {
  def receive = {
    case TypeCItemOrdered(orderItem) =>
      println(s"OrderItemTypeCProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeCProcessor: received unexpected message")
  }
}