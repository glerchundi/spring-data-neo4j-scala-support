package org.springframework.data.neo4j.support.mapping

import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.neo4j.mapping.Neo4jPersistentProperty

import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import org.springframework.core.convert.ConversionService

class CustomNeo4jPersistentPropertyImpl(
    field: Field,
    propertyDescriptor: PropertyDescriptor,
    owner: PersistentEntity[_, Neo4jPersistentProperty],
    simpleTypeHolder: SimpleTypeHolder,
    ctx: Neo4jMappingContext)
  extends Neo4jPersistentPropertyImpl(field, propertyDescriptor, owner, simpleTypeHolder, ctx) {

  lazy val optionClazz      = Class.forName("scala.Option")
  lazy val traversableClazz = Class.forName("scala.collection.Iterable")

  override def isSerializablePropertyField(conversionService: ConversionService) = {
    super.isSerializablePropertyField(conversionService) || {
      val dstType = getPropertyType
      val srcType =
        information.getType match {
          case t if traversableClazz.isAssignableFrom(t)  => information.getComponentType.getType
          case t if optionClazz.isAssignableFrom(t)       => information.getComponentType.getType
          case t                                          => t
        }
      conversionService.canConvert(srcType, dstType) && conversionService.canConvert(dstType, srcType)
    }
  }

}