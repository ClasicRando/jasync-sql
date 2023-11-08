package com.github.jasync.sql.db.postgresql.messages.frontend

import com.github.jasync.sql.db.postgresql.messages.backend.ServerMessage

data class CopyFailMessage(val failureMessage: String) : ClientMessage(ServerMessage.CopyFail)
