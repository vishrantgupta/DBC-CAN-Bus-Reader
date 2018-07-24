package com.can.dbc.types

import scala.collection.mutable.ListBuffer

/** Defines the network
  *
  * @author Vishrant Gupta
  *
  */
case class NetworkDefinition(document: Document, node: ListBuffer[Node] = ListBuffer.empty,
    bus: ListBuffer[Bus] = ListBuffer.empty)
