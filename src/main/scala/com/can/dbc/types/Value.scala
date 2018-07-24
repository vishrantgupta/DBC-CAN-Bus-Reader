package com.can.dbc.types

class Value {

  protected var _type: String = _
  protected var _slope = .0
  protected var _intercept = .0
  protected var _unit: String = _
  protected var _min = .0
  protected var _max = .0

  def `type`: String = if (Option(_type).isDefined) _type else "unsigned"

  def type_=(value: String): Unit = this._type = value

  def slope: Double = if (Option(_slope).isDefined) _slope else 1.0D

  def slope_=(value: Double): Unit = this._slope = value

  def intercept: Double = if (Option(_intercept).isDefined) _intercept else 0.0D

  def intercept_=(value: Double): Unit = this._intercept = value

  def unit: String = if (Option(_unit).isDefined) _unit else "1"

  def unit_=(value: String): Unit = this._unit = value

  def min: Double = if (Option(_min).isDefined) _min else 0.0D

  def min_=(value: Double): Unit = this._min = value

  def max: Double = if (Option(_max).isDefined) _max else 1.0D

  def max_=(value: Double): Unit = this._max = value

}
