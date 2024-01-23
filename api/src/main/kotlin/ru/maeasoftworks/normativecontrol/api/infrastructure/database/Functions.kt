package ru.maeasoftworks.normativecontrol.api.infrastructure.database

import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

suspend inline fun <T> transaction(crossinline fn: suspend Transaction.() -> T): T {
    return Database {
        withTransaction {
            fn(Transaction)
        }
    }
}

@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class UnsafeDatabaseUsage

@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class UnsafeDataAccess(val description: String)

@UnsafeDatabaseUsage
suspend inline fun <T> unsafeDatabaseUsage(crossinline fn: suspend Transaction.() -> T): T {
    return Database {
        fn(Transaction)
    }
}