package com.can.dbc.types

import java.math.BigInteger

/** Label for signal
  *
  * @author Vishrant Gupta
  *
  */
class Label extends BasicLabelType {

  protected var _value: BigInteger = _

  def value: BigInteger = _value

  def value_=(value: BigInteger): Unit = this._value = value

}
