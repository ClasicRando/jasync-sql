package com.github.jasync.sql.db.postgresql.messages.backend

data class CopyInResponseMessage(
    val isBinary: Boolean,
    val columnCount: Int,
    val columnData: Array<Boolean>
) : ServerMessage(ServerMessage.CopyInResponse) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CopyInResponseMessage

        if (isBinary != other.isBinary) return false
        if (columnCount != other.columnCount) return false
        if (!columnData.contentEquals(other.columnData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isBinary.hashCode()
        result = 31 * result + columnCount
        result = 31 * result + columnData.contentHashCode()
        return result
    }
}
