package com.can.dbc.types

import scala.collection.mutable.ListBuffer

/** Factory class for different document type
  *
  * @author Vishrant Gupta
  *
  */
class ObjectFactory() {

  def createDocument(_content: String, _name: String,
    _version: String = "", _author: String = "",
    _company: String = "", _date: String = "") = new Document(_content, _name, _version, _author, _company, _date)

  def createNodeRef = new NodeRef

  def createValue = new Value

  def createMessage = new Message

  def createProducer = new Producer

  def createMultiplex = new Multiplex

  def createMuxGroup = new MuxGroup

  def createSignal = new Signal

  def createConsumer = new Consumer

  def createLabelSet = new LabelSet

  def createLabel = new Label

  def createNetworkDefinition(_document: Document, _node: ListBuffer[Node] = ListBuffer.empty,
    _bus: ListBuffer[Bus] = ListBuffer.empty) = new NetworkDefinition(_document, _node, _bus)

  def createNode(_id: String, _name: String) = new Node(_id, _name)

  def createBus = new Bus

}
