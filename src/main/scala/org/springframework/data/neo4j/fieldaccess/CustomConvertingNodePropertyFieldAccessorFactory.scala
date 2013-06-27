package org.springframework.data.neo4j.fieldaccess

import org.springframework.data.neo4j.fieldaccess.PropertyFieldAccessorFactory.PropertyFieldAccessor
import org.springframework.data.neo4j.mapping.{MappingPolicy, Neo4jPersistentProperty}
import org.springframework.data.neo4j.support.Neo4jTemplate

class CustomConvertingNodePropertyFieldAccessorFactory(template: Neo4jTemplate)
  extends ConvertingNodePropertyFieldAccessorFactory(template) {

  override def accept(property: Neo4jPersistentProperty): Boolean = {
    super.accept(property)
  }

  override def forField(field: Neo4jPersistentProperty): FieldAccessor = {
    new CustomConvertingNodePropertyFieldAccessor(template, field)
  }

  class CustomConvertingNodePropertyFieldAccessor(template: Neo4jTemplate, property: Neo4jPersistentProperty)
    extends PropertyFieldAccessor(template, property) {

    val propertyConverter = new CustomPropertyConverter(template.getConversionService(), property)

    override def setValue(entity: AnyRef, newVal: AnyRef, mappingPolicy: MappingPolicy): AnyRef = {
      val value =
        if (propertyConverter.isObjectOrSupportedType(newVal, this.property)) {
          newVal
        }
        else {
          propertyConverter.serializePropertyValue(newVal)
        }
      super.setValue(entity, value, mappingPolicy)
      newVal
    }

    override protected def doGetValue(entity: AnyRef): AnyRef = {
      val ret = super.doGetValue(entity);
      if (propertyConverter.isObjectOrSupportedType(ret, this.property)) {
        ret
      }
      propertyConverter.deserializePropertyValue(ret)
    }

    override def convertSimplePropertyValue(value: AnyRef): AnyRef = {
      value
    }

  }

}