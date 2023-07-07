package me.santio.coffee.common.async

import me.santio.coffee.common.annotations.Sync

/**
 * Represents an asynchronous driver, this allows commands to run asynchronously.
 * While not required, this is highly recommended to be implemented to allow users to choose
 * whether they want to run commands synchronously or asynchronously.
 * @see Sync
 */
abstract class AsyncDriver {
    abstract fun runAsync(runnable: Runnable)
    abstract fun runSync(runnable: Runnable)
}