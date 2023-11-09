package com.github.aysnc.sql.db.integration

import com.github.aysnc.sql.db.verifyException
import com.github.jasync.sql.db.postgresql.exceptions.InCopyModeException
import com.github.jasync.sql.db.util.length
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CopyInSpec : DatabaseTestHelper() {
    private val copyInTable = """
        CREATE TABLE if not exists testing.copy_in_table
        (
            id bigserial PRIMARY KEY,
            content varchar(255) NOT NULL
        )
    """.trimIndent()

    private fun init() {
        withHandler { handler ->
            executeDdl(handler, "create schema if not exists testing")
            executeDdl(handler, "drop table if exists testing.copy_in_table")
            executeDdl(handler, this.copyInTable)
        }
    }

    @Test
    fun `copy in should write all rows`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_in_table FROM STDIN WITH (FORMAT csv)"
            val result = executeQuery(handler, copyQuery)
            assertThat(result.rowsAffected).isEqualTo(0)
            assertThat(result.statusMessage).isEqualTo("CopyIn Started")
            assertThat(result.rows.length).isEqualTo(0)

            for (i in 1..100) {
                handler.writeCopyData("$i,Row $i\n".toByteArray())
            }

            val copyResult = awaitFuture(handler.endCopy())
            assertThat(copyResult.rowsAffected).isEqualTo(100)
            assertThat(copyResult.statusMessage).isEqualTo("COPY 100")
            assertThat(copyResult.rows.length).isEqualTo(0)

            // Confirm that query execution is enabled after copy out completed
            executeQuery(handler, "SELECT 1")
        }
    }

    @Test
    fun `copy in should abort without exception when failing copy`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_in_table FROM STDIN WITH (FORMAT csv)"
            val result = executeQuery(handler, copyQuery)
            assertThat(result.rowsAffected).isEqualTo(0)
            assertThat(result.statusMessage).isEqualTo("CopyIn Started")
            assertThat(result.rows.length).isEqualTo(0)

            for (i in 1..100) {
                handler.writeCopyData("$i,Row $i\n".toByteArray())
            }

            val copyResult = awaitFuture(handler.failCopy("Test fail"))
            assertThat(copyResult.rowsAffected).isEqualTo(0)
            assertThat(copyResult.statusMessage).isEqualTo("CopyIn failed by client")
            assertThat(copyResult.rows.length).isEqualTo(0)

            // Confirm that query execution is enabled after copy out completed
            executeQuery(handler, "SELECT 1")
        }
    }

    @Test
    fun `after copy in query functionality disabled`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_in_table FROM STDIN WITH (FORMAT csv)"
            executeQuery(handler, copyQuery)
            verifyException(InCopyModeException::class.java) {
                executeQuery(handler, "SELECT 1")
            }
        }
    }

    @Test
    fun `after copy in prepared statement functionality disabled`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_in_table FROM STDIN WITH (FORMAT csv)"
            executeQuery(handler, copyQuery)
            verifyException(InCopyModeException::class.java) {
                executePreparedStatement(handler, "SELECT ?::text", listOf("test"))
            }
        }
    }
}
