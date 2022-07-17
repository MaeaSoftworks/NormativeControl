package com.maeasoftworks.docx4nc.model

import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import org.jvnet.jaxb2_commons.ppp.Child

/**
 * Класс, содержащий соответствие между параграфом и свойствами параграфа, и между прогоном и свойствами прогона
 *
 * @author prmncr
 */
class Resolver(private val resolver: PropertyResolver) {

    /**
     * Соответствие между параграфом и свойствами параграфа
     *
     * @author prmncr
     */
    private var pPrs: MutableMap<String, PPr> = HashMap()

    /**
     * Соответствие между прогоном и свойствами прогона
     *
     * @author prmncr
     */
    private var rPrs: MutableMap<R, RPr> = HashMap()

    /**
     * Получает новый объект свойств параграфа с помощью объекта resolver и возвращает его, либо не получает
     * и возвращает уже имеющийся в pPrs
     * @param p объект параграфа
     *
     * @author prmncr
     */
    fun getEffectivePPr(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getEffectivePPr(p.pPr).also { pPrs[p.paraId] = it }
    }

    /**
     * Получает новый объект свойств прогона с помощью объекта resolver и возвращает его, либо не получает
     * и возвращает уже имеющийся в rPrs
     * @param r объект прогона
     *
     * @author prmncr
     */
    fun getEffectiveRPr(r: R): RPr {
        return rPrs[r] ?: resolver.getEffectiveRPr(
            r.rPr,
            pPrs[(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P).paraId]
                ?: getEffectivePPr(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P)
        ).also { rPrs[r] = it }
    }
}
