package com.can.dbc.types

/** Used to create network document
  *
  * @author Vishrant Gupta
  *
  */
case class Document(
    content: String = "", name: String = "",
    version: String = "", author: String = "",
    company: String = "", date: String = ""
)