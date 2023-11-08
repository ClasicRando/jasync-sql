package com.github.jasync.sql.db.postgresql.messages.frontend

import com.github.jasync.sql.db.postgresql.messages.backend.ServerMessage

data class ClientCopyDataMessage(val row: ByteArray) : ClientMessage(ServerMessage.CopyData) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientCopyDataMessage

        return row.contentEquals(other.row)
    }

    override fun hashCode(): Int {
        return row.contentHashCode()
    }
}
