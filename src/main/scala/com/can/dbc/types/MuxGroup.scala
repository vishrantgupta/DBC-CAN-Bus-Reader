package com.can.dbc.types

/** Multiplex group
  *
  * @author Vishrant Gupta
  *
  */
case class MuxGroup(
    signal: List[Signal] = List.empty,
    count: Long = 0)