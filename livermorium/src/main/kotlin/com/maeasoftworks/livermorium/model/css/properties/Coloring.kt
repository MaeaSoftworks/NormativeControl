package com.maeasoftworks.livermorium.model.css.properties

object Color : Property(converter = { if (it != "null") "#$it" else null })

object BackgroundColor : Property(converter = { if (it != null) "#$it" else null })
