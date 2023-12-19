package me.prmncr.hotloader

/**
 * Mock interface for generated `HotLoader` object.
 * Actual generated object does not implement this interface due to necessity of this package importing.
 */
interface HotLoaderMock {
    /**
     * Loads all objects that was marked with [HotLoaded] annotation.
     */
    fun load()
}