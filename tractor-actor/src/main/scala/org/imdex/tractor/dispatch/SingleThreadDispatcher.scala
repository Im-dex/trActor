package org.imdex.tractor.dispatch

import java.util.concurrent.{ExecutorService, Executors}

import org.imdex.tractor.actor.ActorIndex

import scala.collection.mutable

object SingleThreadDispatcher {
    def apply(throughput: Int = Dispatcher.DefaultThroughput): SingleThreadDispatcher = {
        new SingleThreadDispatcher(Executors.newSingleThreadExecutor(), throughput)
    }
}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
class SingleThreadDispatcher private[SingleThreadDispatcher] (executorService: ExecutorService,
                                                              throughput: Int) extends Dispatcher(executorService, throughput) {
    private[this] val actors = mutable.HashMap.empty[ActorIndex, ActorData]

    override protected def register(index: ActorIndex, data: ActorData): Unit = execute {
        actors.put(index, data)
    }
}
