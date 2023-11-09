package com.github.jasync.sql.db.postgresql.exceptions

import com.github.jasync.sql.db.exceptions.DatabaseException

class NotInCopyModeException :
    DatabaseException("Copy operation executed on connection that is not in copy mode")
