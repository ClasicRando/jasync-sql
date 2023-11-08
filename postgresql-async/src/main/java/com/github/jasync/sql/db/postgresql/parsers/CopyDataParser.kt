package com.github.jasync.sql.db.postgresql.parsers

import com.github.jasync.sql.db.postgresql.messages.backend.CopyDataMessage
import com.github.jasync.sql.db.postgresql.messages.backend.CopyOutResponseMessage
import com.github.jasync.sql.db.postgresql.messages.backend.ServerMessage
import com.github.jasync.sql.db.util.readCString
import io.netty.buffer.ByteBuf

object CopyDataParser : MessageParser {
    override fun parseMessage(buffer: ByteBuf): ServerMessage {
        val row = ByteArray(buffer.readableBytes())
        buffer.readBytes(row)
        return CopyDataMessage(row)
    }
}
