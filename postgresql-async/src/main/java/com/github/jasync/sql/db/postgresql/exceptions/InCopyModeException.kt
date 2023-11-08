package com.github.jasync.sql.db.postgresql.exceptions

import com.github.jasync.sql.db.exceptions.DatabaseException

class InCopyModeException
    : DatabaseException("Regular operation executed on connection that is in copy mode")
