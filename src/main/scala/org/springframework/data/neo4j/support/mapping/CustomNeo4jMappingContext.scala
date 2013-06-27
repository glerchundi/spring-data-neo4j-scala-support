package org.springframework.data.neo4j.support.mapping

import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.neo4j.mapping.Neo4jPersistentProperty

import java.beans.PropertyDescriptor
import java.lang.reflect.Field

class CustomNeo4jMappingContext extends Neo4jMappingContext {

  override protected def createPersistentProperty(
      field: Field,
      descriptor: PropertyDescriptor,
      owner: Neo4jPersistentEntityImpl[_],
      simpleTypeHolder: SimpleTypeHolder): Neo4jPersistentProperty = {
    new CustomNeo4jPersistentPropertyImpl(field, descriptor, owner, simpleTypeHolder, this)
  }

}
