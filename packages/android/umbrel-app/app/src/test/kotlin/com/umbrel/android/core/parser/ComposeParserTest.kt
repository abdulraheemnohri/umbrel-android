package com.umbrel.android.core.parser

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

class ComposeParserTest {
    @Test
    fun testParseSimpleCompose() {
        val yaml = """
            services:
              web:
                image: nextcloud
                ports:
                  - "8080:80"
                environment:
                  - DEBUG=true
        """.trimIndent()

        val parser = ComposeParser()
        val services = parser.parseCompose(ByteArrayInputStream(yaml.toByteArray()))

        assertEquals(1, services.size)
        assertEquals("web", services[0].name)
        assertEquals("nextcloud", services[0].image)
        assertEquals(8080, services[0].ports[0])
        assertEquals("true", services[0].environment["DEBUG"])
    }
}
