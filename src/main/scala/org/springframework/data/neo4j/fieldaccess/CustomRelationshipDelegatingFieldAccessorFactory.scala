package org.springframework.data.neo4j.fieldaccess

import java.util.Collection
import org.springframework.data.neo4j.support.Neo4jTemplate

import scala.collection.JavaConverters._

class CustomRelationshipDelegatingFieldAccessorFactory(template: Neo4jTemplate)
  extends RelationshipDelegatingFieldAccessorFactory(template) {

  override protected def createAccessorFactories(): Collection[_ <: FieldAccessorFactory] = (
    super.createAccessorFactories().asScala.map {
      case x: ConvertingNodePropertyFieldAccessorFactory => new CustomConvertingNodePropertyFieldAccessorFactory(template)
      case x => x
    } ++
    List(
    )
  ).asJavaCollection

}

object CustomRelationshipDelegatingFieldAccessorFactory {

  def Factory(): FieldAccessorFactoryFactory = {
    new FieldAccessorFactoryFactory() {
      override def create(template: Neo4jTemplate): DelegatingFieldAccessorFactory =
        new CustomRelationshipDelegatingFieldAccessorFactory(template)
    }
  }

}
