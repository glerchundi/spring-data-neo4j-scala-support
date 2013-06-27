package org.springframework.data.neo4j.listeners

import java.lang.Override

import org.springframework.context.ApplicationListener
//import org.springframework.data.neo4j.annotation.{ DeleteEvent => DeleteEventAnnotation }
import org.springframework.data.neo4j.lifecycle.DeleteEvent

class DeleteEventApplicationListener extends ApplicationListener[DeleteEvent[AnyRef]] {

  @Override
  def onApplicationEvent(event: DeleteEvent[AnyRef]): Unit = {
  }

}