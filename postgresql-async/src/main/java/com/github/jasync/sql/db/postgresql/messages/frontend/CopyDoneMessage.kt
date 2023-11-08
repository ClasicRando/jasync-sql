package com.github.jasync.sql.db.postgresql.messages.frontend

import com.github.jasync.sql.db.postgresql.messages.backend.ServerMessage

object CopyDoneMessage : ClientMessage(ServerMessage.CopyDone)
