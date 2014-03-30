package org.springframework.data.neo4j.fieldaccess

import java.lang.reflect.{ Array => JArray }
import java.util.{ ArrayList => JArrayList }

import org.springframework.data.neo4j.mapping.Neo4jPersistentProperty
import org.springframework.core.convert.ConversionService

class CustomPropertyConverter(conversionService: ConversionService, property: Neo4jPersistentProperty)
  extends PropertyConverter(conversionService, property) {

  lazy val optionClazz      = Class.forName("scala.Option")
  lazy val traversableClazz = Class.forName("scala.collection.Traversable")
  lazy val setClazz         = Class.forName("scala.collection.Set")
  lazy val seqClazz         = Class.forName("scala.collection.Seq")

  lazy val information = property.getTypeInformation

  private def serializePropertyValueFromTraversable(values: Traversable[_]): AnyRef = {
    val targetClazz = property.getPropertyType
    val list = new JArrayList[AnyRef]()
    values.foreach(v => list.add(conversionService.convert(v, targetClazz).asInstanceOf[AnyRef]))
    list.toArray(
      JArray
        .newInstance(targetClazz, values.size)
        .asInstanceOf[Array[AnyRef]]
    )
  }

  private def serializePropertyValueFromOption(value: Option[_]): AnyRef = {
    value match {
      case None => null
      case Some(innerValue) => {
        val targetClazz = property.getPropertyType
        val result = if (conversionService.canConvert(innerValue.getClass, targetClazz)) {
          conversionService.convert(innerValue, targetClazz)
        }
        else {
          innerValue
        }
        result.asInstanceOf[AnyRef]
      }
    }
  }

  private def deserializePropertyValueToSeq(values: Array[AnyRef]): AnyRef = {
    val actualType = information.getActualType.getType
    values
      .map(v => conversionService.convert(v, actualType))
      .toList
      .asInstanceOf[AnyRef]
  }

  private def deserializePropertyValueToSet(values: Array[AnyRef]): AnyRef = {
    val actualType = information.getActualType.getType
    values
      .map(v => conversionService.convert(v, actualType))
      .toSet
      .asInstanceOf[AnyRef]
  }

  private def deserializePropertyValueToOption(value: AnyRef): AnyRef = {
    val actualType = information.getActualType.getType
    val valueToWrap = if (conversionService.canConvert(value.getClass, actualType)) {
      conversionService.convert(value, actualType)
    }
    else {
      value
    }

    Some(valueToWrap)
      .asInstanceOf[AnyRef]
  }

  override def serializePropertyValue(newVal: AnyRef): AnyRef = {
    Option(newVal).map(v => {
      information.getType match {
        case t if traversableClazz.isAssignableFrom(t)  =>
          serializePropertyValueFromTraversable(v.asInstanceOf[Traversable[_]])
        case t if optionClazz.isAssignableFrom(t)       =>
          serializePropertyValueFromOption(v.asInstanceOf[Option[_]])
        case _                                          =>
          super.serializePropertyValue(v)
      }
    }).getOrElse(null)
  }

  override def deserializePropertyValue(newVal: AnyRef): AnyRef = {
    Option(newVal).map(v => {
      information.getType match {
        case t if seqClazz.isAssignableFrom(t)          =>
          deserializePropertyValueToSeq(v.asInstanceOf[Array[AnyRef]])
        case t if setClazz.isAssignableFrom(t)          =>
          deserializePropertyValueToSet(v.asInstanceOf[Array[AnyRef]])
        case t if optionClazz.isAssignableFrom(t)       =>
          deserializePropertyValueToOption(v)
        case _                                          =>
          super.deserializePropertyValue(v)
      }
    }).getOrElse(null)
  }

}
