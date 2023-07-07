package me.santio.coffee.common.async

import me.santio.coffee.common.annotations.Sync

/**
 * The default async driver, which runs all commands synchronously.
 * @see AsyncDriver
 * @see Sync
 */
internal object DefaultAsyncDriver: AsyncDriver() {
    override fun runAsync(runnable: Runnable) {
        runnable.run()
    }

    override fun runSync(runnable: Runnable) {
        runnable.run()
    }
}