package com.maeasoftworks.normativecontrol.dtos.docs

class MethodInfo (val root: String,
                  val path: String,
                  val type: MethodType,
                  val description: String,
                  val queryParams: List<Argument>?,
                  val bodyParams: List<Argument>?,
                  val returns: List<Response>)