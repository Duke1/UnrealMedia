package com.qfleng.um.util.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.Closeable
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


const val TAG = "CoroutineExt"

class CtxCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        if (coroutineContext.isActive)
            coroutineContext.cancel()
    }
}


/**
 * 等待器
 */
class Waiter {
    private val channel: Channel<Unit> = Channel(0)

    suspend fun doWait() {
        channel.receive()
    }

    fun doNotify() {
        channel.offer(Unit)
    }
}


class CoroutineError @JvmOverloads constructor(detailMessage: String, throwable: Throwable? = null) : RuntimeException(detailMessage, throwable)


/**
 * 异步辅助
 *
 * <a href="https://www.kotlincn.net/docs/reference/coroutines/basics.html">Kotlin 协程</a>
 */
fun getCoroutineScope(): CtxCoroutineScope {
    return CtxCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate);
}

/**
 * @param asyncFunc 异步执行函数
 * @param complete 完成异步任务之后，执行的同步函数
 * @param observeOn 默认 Dispatchers.Main
 */
fun <R> doAsync(
        scope: CoroutineScope = getCoroutineScope(),
        asyncFunc: suspend () -> R,
        observeOn: CoroutineDispatcher = Dispatchers.Main,
        observer: (R) -> Unit,
        error: (String, Throwable) -> Unit = fun(msg, ex) {
            println("CoroutineExceptionHandler:$msg")
        }
) {
    val handler = CoroutineExceptionHandler { _, exception ->
        error(exception.message ?: "", exception)
    }

    scope.launch {
        val result = withContext(Dispatchers.IO + handler) {
            try {
                val data = asyncFunc()

                data
            } catch (e: Exception) {
                //
            }
        }
        launch(observeOn) {

            if (result is Exception) {
                error(result.message ?: "", result)
            } else {
                observer(result as R)
            }

            if (scope is CtxCoroutineScope)
                scope.close()
        }
    }

}

