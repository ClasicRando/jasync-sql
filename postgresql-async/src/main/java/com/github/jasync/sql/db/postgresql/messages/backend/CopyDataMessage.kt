package com.github.jasync.sql.db.postgresql.messages.backend

data class CopyDataMessage(val row: ByteArray) : ServerMessage(ServerMessage.CopyData) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CopyDataMessage

        return row.contentEquals(other.row)
    }

    override fun hashCode(): Int {
        return row.contentHashCode()
    }
}
