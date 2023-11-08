package com.github.jasync.sql.db.postgresql.parsers

import com.github.jasync.sql.db.postgresql.messages.backend.CopyInResponseMessage
import com.github.jasync.sql.db.postgresql.messages.backend.ServerMessage
import io.netty.buffer.ByteBuf

object CopyInResponseParser : MessageParser {
    override fun parseMessage(buffer: ByteBuf): ServerMessage {
        val isBinary = buffer.readByte().toInt() == 1
        val columnCount = buffer.readShort().toInt()
        val columnData = Array(columnCount) {
            buffer.readShort().toInt() == 1
        }

        return CopyInResponseMessage(isBinary, columnCount, columnData)
    }
}
