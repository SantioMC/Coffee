package me.santio.coffee.common.async

import java.util.concurrent.Executors

/**
 * A driver that uses Java Executors to run tasks asynchronously.
 * @see Executors
 * @see AsyncDriver
 */
object ExecutorAsyncDriver: AsyncDriver() {
    override fun runAsync(runnable: Runnable) {
        Executors.newSingleThreadExecutor().submit(runnable)
    }

    override fun runSync(runnable: Runnable) {
        runnable.run()
    }
}