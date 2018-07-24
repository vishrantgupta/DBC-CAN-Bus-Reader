package com.can.dbc.types

import scala.collection.mutable.ListBuffer

class Consumer {

  protected var _nodeRef: ListBuffer[NodeRef] = ListBuffer.empty

  def nodeRef: ListBuffer[NodeRef] = {
    _nodeRef
  }

}
