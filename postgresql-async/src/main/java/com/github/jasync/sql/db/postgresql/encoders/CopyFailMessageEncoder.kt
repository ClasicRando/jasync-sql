package com.github.jasync.sql.db.postgresql.encoders

import com.github.jasync.sql.db.postgresql.messages.frontend.ClientMessage
import com.github.jasync.sql.db.postgresql.messages.frontend.CopyFailMessage
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset

class CopyFailMessageEncoder(private val charset: Charset) : Encoder {
    override fun encode(message: ClientMessage): ByteBuf {
        val m = message as CopyFailMessage
        val bytes = m.failureMessage.toByteArray(charset)
        val buffer = Unpooled.buffer(5)
        buffer.writeByte(m.kind)
        buffer.writeInt(bytes.size + 5)
        buffer.writeBytes(bytes)
        buffer.writeByte(0)
        return buffer
    }
}
