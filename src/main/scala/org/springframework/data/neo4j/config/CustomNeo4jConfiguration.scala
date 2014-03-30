package org.springframework.data.neo4j.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.{Converter, ConverterRegistry}
import org.springframework.data.neo4j.fieldaccess.CustomNodeDelegatingFieldAccessorFactory
import org.springframework.data.neo4j.fieldaccess.CustomRelationshipDelegatingFieldAccessorFactory
import org.springframework.data.neo4j.fieldaccess.FieldAccessorFactoryFactory;
import org.springframework.data.neo4j.listeners._
import org.springframework.data.neo4j.support.MappingInfrastructureFactoryBean
import org.springframework.data.neo4j.support.mapping.Neo4jMappingContext
import org.springframework.data.neo4j.support.mapping.CustomNeo4jMappingContext

import java.lang.{ Byte => JByte }
import java.lang.{ Short => JShort }
import java.lang.{ Integer => JInt }
import java.lang.{ Long => JLong }
import java.lang.{ Float => JFloat }
import java.lang.{ Double => JDouble }
import java.lang.{ Character => JChar }
import java.lang.{ String => JString }
import java.lang.{ Boolean => JBoolean }
import java.util.UUID

import org.neo4j.graphdb.GraphDatabaseService

@Configuration
abstract class CustomNeo4jConfiguration extends Neo4jConfiguration {

  {
    this.setGraphDatabaseService(this.graphDatabaseService())
  }

  def isStandalone: Boolean = true

  /* default scala primitive types converters */
  def scalaConverters: Traversable[Converter[_, _]] = {
    List(
      // UUID
      new Converter[UUID, JString] { def convert(s: UUID): JString = s.toString() },
      new Converter[JString, UUID] { def convert(s: JString): UUID = UUID.fromString(s) }
    )
  }

  @Bean
  def graphDatabaseService(): GraphDatabaseService

  @Bean
  def afterSaveEventApplicationListener() = {
    new AfterSaveEventApplicationListener()
  }

  @Bean
  def beforeSaveEventApplicationListener() = {
    new BeforeSaveEventApplicationListener()
  }

  @Bean
  def deleteEventApplicationListener() = {
    new DeleteEventApplicationListener()
  }

  @Bean
  override protected def neo4jConversionService() = {
    super.neo4jConversionService() match {
      case converterRegistry: ConverterRegistry => {
        scalaConverters.foreach(c => converterRegistry.addConverter(c))
        converterRegistry
      }
      case conversionService =>
        throw new IllegalArgumentException("conversionservice is no ConverterRegistry:" + conversionService)
    }
  }

  // TODO: Request for feature: add an overriding point here and avoid overriding neo4jMappingContext
  def instantiateNeo4jMappingContext(): Neo4jMappingContext = {
    new CustomNeo4jMappingContext()
  }

  @Bean
  @throws(classOf[Exception])
  override def neo4jMappingContext(): Neo4jMappingContext = {
    val mappingContext = instantiateNeo4jMappingContext()
    val initialEntitySet = getInitialEntitySet()
    if (initialEntitySet != null) {
      mappingContext.setInitialEntitySet(initialEntitySet);
    }
    mappingContext.setEntityAlias(entityAlias());
    mappingContext
  }

  @throws(classOf[Exception])
  override def nodeDelegatingFieldAccessorFactory(): FieldAccessorFactoryFactory = {
    CustomNodeDelegatingFieldAccessorFactory.Factory()
  }

  @throws(classOf[Exception])
  override def relationshipDelegatingFieldAccessorFactory(): FieldAccessorFactoryFactory = {
    CustomRelationshipDelegatingFieldAccessorFactory.Factory()
  }

  @throws(classOf[Exception])
  override def mappingInfrastructure(): MappingInfrastructureFactoryBean = {
    val factoryBean = super.mappingInfrastructure()
    if (isStandalone)
      factoryBean.afterPropertiesSet()
    factoryBean
  }

}