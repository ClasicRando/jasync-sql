package com.github.jasync.sql.db.postgresql.copy

import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * Allows iterating over a backing [queue] that is fed with CopyData responses from a postgresql
 * server. This iterator is blocking/locking since the producer and consumer might need to push and
 * pull from the queue at any point in time.
 */
class CopyOutIterator(private val connection: PostgreSQLConnection) : Iterator<ByteArray> {
    /**
     * Iteration item that can either be:
     * - [None] - initial state and never used again
     * - [Done] - [connection] has received a CopyDone message to signify the iterator should end
     * - [Row] - row data as a [ByteArray]
     */
    sealed interface Item {
        object None : Item
        object Done : Item
        class Row(val row: ByteArray) : Item
    }

    private val queue: BlockingQueue<Item> = LinkedBlockingQueue()
    private var next: Item = Item.None

    /** Add a new row of [bytes] for future iteration */
    fun put(bytes: ByteArray) {
        queue.put(Item.Row(bytes))
    }

    /** Mark the CopyOut operation as done with the special message */
    fun markDone() {
        queue.put(Item.Done)
    }

    override fun hasNext(): Boolean {
        // Do not allow future iteration if another user attempts to start iteration
        if (next == Item.Done) {
            return false
        }
        if (next != Item.None) {
            return true
        }
        next = queue.take()
        if (next == Item.Done) {
            connection.endCopyOutMode()
            return false
        }
        return true
    }

    override fun next(): ByteArray {
        val temp = next
        next = Item.None
        return when (temp) {
            is Item.Row -> temp.row
            // This should never happen since Item.None is only used to initialize and Item.Done
            // is only used to signify the end and will return false from hasNext
            is Item.Done, is Item.None -> error("Returned items must be a row")
        }
    }
}
