package org.springframework.data.neo4j.listeners

import java.lang.Override
import java.lang.reflect.Method

import org.springframework.context.ApplicationListener
import org.springframework.data.neo4j.annotation.{ BeforeSaveEvent => BeforeSaveEventAnnotation }
import org.springframework.data.neo4j.lifecycle.BeforeSaveEvent

class BeforeSaveEventApplicationListener extends ApplicationListener[BeforeSaveEvent[AnyRef]] {

  def findAnnotatedMethods(entity: AnyRef): Array[Method] = {
    val allMethods: Array[Method] = entity.getClass().getMethods()
    allMethods filter (m => m.isAnnotationPresent(classOf[BeforeSaveEventAnnotation]))
  }

  @Override
  def onApplicationEvent(event: BeforeSaveEvent[AnyRef]): Unit = {
    val entity = event.getEntity()
    findAnnotatedMethods(entity) foreach (_.invoke(entity))
  }

}
