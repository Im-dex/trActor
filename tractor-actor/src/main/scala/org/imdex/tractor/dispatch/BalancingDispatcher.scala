package org.imdex.tractor.dispatch

import java.util.concurrent._

import org.imdex.tractor.actor.ActorIndex

import scala.concurrent.duration._
import scala.language.postfixOps

sealed trait ExecutionOrder
object ExecutionOrder {
    case object FIFO extends ExecutionOrder
    case object LIFO extends ExecutionOrder
}

object BalancingDispatcher {
    val DefaultThreadTimeout = 60 seconds

    private[this] def parallelism(parallelismMin: Int, parallelismMax: Int, factor: Double): Int = {
        val result = (Runtime.getRuntime.availableProcessors() * factor).ceil.toInt
        (result max parallelismMin) min parallelismMax
    }

    def threadPool(threadsCount: Int, maxThreadsCount: Int, throughput: Int = Dispatcher.DefaultThroughput,
                   threadTimeout: FiniteDuration = DefaultThreadTimeout): BalancingDispatcher = {
        new BalancingDispatcher(
            new ThreadPoolExecutor(
                threadsCount,
                maxThreadsCount,
                threadTimeout.length,
                threadTimeout.unit,
                new LinkedBlockingQueue[Runnable]
            ),
            throughput
        )
    }

    def forkJoin(parallelismMin: Int, parallelismMax: Int, throughput: Int = Dispatcher.DefaultThroughput, factor: Double = 1.0,
                 order: ExecutionOrder = ExecutionOrder.FIFO): BalancingDispatcher = {
        new BalancingDispatcher(
            new ForkJoinPool(
                parallelism(parallelismMin, parallelismMax, factor),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                order eq ExecutionOrder.FIFO
            ),
            throughput
        )
    }
}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
class BalancingDispatcher private[BalancingDispatcher] (executorService: ExecutorService,
                                                        throughput: Int = Dispatcher.DefaultThroughput) extends Dispatcher(executorService, throughput) {
    private[this] val actors = new ConcurrentHashMap[ActorIndex, ActorData]()

    override protected def register(index: ActorIndex, data: ActorData): Unit = {
        actors.put(index, data)
    }
}
