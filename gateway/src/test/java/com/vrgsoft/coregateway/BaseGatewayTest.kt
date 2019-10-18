package com.vrgsoft.coregateway

import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.vrgsoft.coregateway.base.LiveDataTest
import com.vrgsoft.coreremote.error.ConnectionError
import com.vrgsoft.coreremote.result.BaseResult
import com.vrgsoft.coreremote.result.ErrorResult
import com.vrgsoft.coreremote.result.SuccessResult
import com.vrgsoft.networkmanager.NetworkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class BaseGatewayTest : LiveDataTest() {

    private lateinit var gateway: BaseGateway
    private lateinit var manager: NetworkManager

    override fun setUpDetail() {
        manager = spy(NetworkManager())
        gateway = MockedGateway(manager)
    }

    @Test
    fun testSuccess() {
        val testData = 1488
        runBlockingTest {
            val call: (suspend () -> BaseResult<Int>) = {
                SuccessResult(testData)
            }
            val result = gateway.executeRemote(call)

            verify(manager).startProcessing()
            verify(manager).stopProcessing()
            Assert.assertEquals(true, result is SuccessResult)
            Assert.assertEquals(testData, (result as SuccessResult).data)
        }
    }

    @Test
    fun testError() {
        runBlockingTest {
            val call: (suspend () -> BaseResult<Int>) = {
                ErrorResult(ConnectionError())
            }
            val result = gateway.executeRemote(call)

            verify(manager).startProcessing()
            verify(manager).stopProcessing()
            Assert.assertEquals(true, result is ErrorResult)
            Assert.assertEquals(true, (result as ErrorResult).error is ConnectionError)
        }
    }
}