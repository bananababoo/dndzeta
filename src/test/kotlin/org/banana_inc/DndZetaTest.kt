package org.banana_inc


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import kotlin.test.Test

class DndZetaTest{

    private lateinit var server: ServerMock
    private lateinit var plugin: DndZeta

    @BeforeEach
    fun setUp(){
        server = MockBukkit.mock()
        //plugin = MockBukkit.load(DndZeta::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun testStartup(){
//        val sample = mockk<DndZeta>()
//        every { sample.sum(1,4) } returns 5
//        sample.sum(1,4)
//        verify { sample.sum(1,4) }
//        confirmVerified(sample)
    }
}

