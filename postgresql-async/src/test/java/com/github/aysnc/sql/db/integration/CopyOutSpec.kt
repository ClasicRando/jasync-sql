package com.github.aysnc.sql.db.integration

import com.github.aysnc.sql.db.verifyException
import com.github.jasync.sql.db.postgresql.exceptions.InCopyModeException
import com.github.jasync.sql.db.util.length
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CopyOutSpec : DatabaseTestHelper() {
    private val copyOutTable = """
        CREATE TABLE if not exists testing.copy_out_table
        (
            id bigint PRIMARY KEY,
            content varchar(255) NOT NULL
        )
    """.trimIndent()

    private val copyOutTablePopulate = """
        WITH RECURSIVE recur(n) AS (
            VALUES (1)
            UNION ALL
            SELECT n+1 FROM recur WHERE n < 100
        )
        INSERT INTO testing.copy_out_table(id, content)
        SELECT r.n, 'Row ' || r.n
        FROM recur r
    """.trimIndent()

    private fun init() {
        withHandler { handler ->
            executeDdl(handler, "create schema if not exists testing")
            executeDdl(handler, "drop table if exists testing.copy_out_table")
            executeDdl(handler, this.copyOutTable)
        }
    }

    @Test
    fun `copy to stdout should read all rows`() {
        init()
        withHandler { handler ->
            executeQuery(handler, copyOutTablePopulate)
        }

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_out_table TO STDOUT WITH (FORMAT csv)"
            val result = executeQuery(handler, copyQuery)
            assertThat(result.rowsAffected).isEqualTo(0)
            assertThat(result.statusMessage).isEqualTo("CopyOut Started")
            assertThat(result.rows.length).isEqualTo(0)

            var rowCount = 0
            for (row in handler.readCopyData()) {
                rowCount++
                val rowAsCsv = row.decodeToString()
                val fields = rowAsCsv.trim('\n').split(",")
                assertThat(fields.size).isEqualTo(2)
                val (id, content) = fields
                assertThat(content).isEqualTo("Row $id")
            }

            assertThat(rowCount).isEqualTo(100)

            // Confirm that query execution is enabled after copy out completed
            executeQuery(handler, "SELECT 1")
        }
    }

    @Test
    fun `after copy to stdout query functionality disabled`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_out_table TO STDOUT WITH (FORMAT csv)"
            executeQuery(handler, copyQuery)
            verifyException(InCopyModeException::class.java) {
                executeQuery(handler, "SELECT 1")
            }
        }
    }

    @Test
    fun `after copy to stdout prepared statement functionality disabled`() {
        init()

        withHandler { handler ->
            val copyQuery = "COPY testing.copy_out_table TO STDOUT WITH (FORMAT csv)"
            executeQuery(handler, copyQuery)
            verifyException(InCopyModeException::class.java) {
                executePreparedStatement(handler, "SELECT ?::text", listOf("test"))
            }
        }
    }
}
