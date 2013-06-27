package org.springframework.data.neo4j.listeners

import java.lang.Override

import org.springframework.context.ApplicationListener
//import org.springframework.data.neo4j.annotation.{ AfterSaveEvent => AfterSaveEventAnnotation }
import org.springframework.data.neo4j.lifecycle.AfterSaveEvent

class AfterSaveEventApplicationListener extends ApplicationListener[AfterSaveEvent[AnyRef]] {

  @Override
  def onApplicationEvent(event: AfterSaveEvent[AnyRef]): Unit = {
  }

}
