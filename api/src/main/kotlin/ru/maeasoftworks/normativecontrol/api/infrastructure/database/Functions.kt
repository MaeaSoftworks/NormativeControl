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

@UnsafeDatabaseUsage
suspend inline fun <T> unsafeDatabaseUse(crossinline fn: suspend Transaction.() -> T): T {
    return Database {
        fn(Transaction)
    }
}