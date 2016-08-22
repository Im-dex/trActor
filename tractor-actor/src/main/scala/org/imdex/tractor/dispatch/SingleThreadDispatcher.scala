package org.imdex.tractor.dispatch

import java.util.concurrent.{ExecutorService, Executors}

object SingleThreadDispatcher {
    def apply(throughput: Int = Dispatcher.DefaultThroughput): SingleThreadDispatcher = {
        new SingleThreadDispatcher(Executors.newSingleThreadExecutor(), throughput)
    }
}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
class SingleThreadDispatcher private[SingleThreadDispatcher] (executorService: ExecutorService,
                                                              throughput: Int) extends Dispatcher(executorService, throughput)
