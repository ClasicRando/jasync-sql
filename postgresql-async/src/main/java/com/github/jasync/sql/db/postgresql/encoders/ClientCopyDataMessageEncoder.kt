package com.github.jasync.sql.db.postgresql.encoders

import com.github.jasync.sql.db.postgresql.messages.frontend.ClientCopyDataMessage
import com.github.jasync.sql.db.postgresql.messages.frontend.ClientMessage
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

object ClientCopyDataMessageEncoder : Encoder {
    override fun encode(message: ClientMessage): ByteBuf {
        val m = message as ClientCopyDataMessage
        val buffer = Unpooled.buffer()
        buffer.writeByte(message.kind)
        buffer.writeInt(message.row.size + 4)
        buffer.writeBytes(message.row)
        return buffer
    }
}
