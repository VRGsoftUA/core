package com.vrgsoft.coreremote

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.vrgsoft.coreremote.error.ConnectionError
import com.vrgsoft.coreremote.error.NetworkError
import com.vrgsoft.coreremote.result.ErrorResult
import com.vrgsoft.coreremote.result.SuccessResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
internal class BaseRepositoryTest {
    lateinit var repository: BaseRepository
    lateinit var api: TestApi

    private var isSuccess = true

    fun setUp() {
        repository = TestRepository()

        val response = mock<Response<String>> {
            on { isSuccessful } doReturn isSuccess
            on { body() } doReturn "1"
            on { code() } doReturn 401
            on { message() } doReturn "Unauthorized"
        }

        api = mock {
            on { call() } doReturn response.toDeferred()
        }
    }

    @Test
    fun transform() {
        setUp()
        val result = SuccessResult("1")

        val transformed = with(repository) {
            result.transform { it.toInt() }
        }

        assertEquals(1, transformed.data)
    }

    @Test
    fun transformIsSuccess() {
        setUp()
        val result = SuccessResult("1")

        val transformed = with(repository) {
            result.transformIsSuccess { it.toInt() }
        }

        require(transformed is SuccessResult<Int>)
        assertEquals(1, transformed.data)
    }

    @Test
    fun transformIsConnectionError() {
        setUp()
        val result = ErrorResult<String>(ConnectionError())

        val transformed = with(repository) {
            result.transformIsSuccess { it.toInt() }
        }

        require(transformed is ErrorResult<Int>)
        require(transformed.error is ConnectionError)
    }

    @Test
    fun transformIsNetworkError() {
        setUp()
        val code = 401
        val message = "Unauthorized"
        val result = ErrorResult<String>(NetworkError(code, message))

        val transformed = with(repository) {
            result.transformIsSuccess { it.toInt() }
        }

        require(transformed is ErrorResult<Int>)

        with(transformed) {
            require(error is NetworkError)
            assertEquals(code, (error as NetworkError).code)
            assertEquals(message, (error as NetworkError).message)
        }
    }

    @Test
    fun executeSuccess() {
        isSuccess = true
        setUp()
        val data = "1"

        runBlockingTest {
            val transformed = with(repository) {
                execute { api.call() }
            }

            require(transformed is SuccessResult<String>)
            assertEquals(data, transformed.data)
        }
    }

    @Test
    fun executeError() {
        isSuccess = false
        setUp()
        val code = 401
        val message = "Unauthorized"

        runBlockingTest {
            val transformed = with(repository) {
                execute { api.call() }
            }

            require(transformed is ErrorResult<String>)
            require(transformed.error is NetworkError)

            with(transformed.error as NetworkError) {
                assertEquals(code, this.code)
                assertEquals(message, this.message)
            }
        }
    }

    @Test
    fun executeThrow() {
        isSuccess = true
        setUp()

        runBlockingTest {
            val transformed = with(repository) {
                execute {
                    throw IllegalArgumentException()
                    api.call()
                }
            }

            require(transformed is ErrorResult)
            require(transformed.error is ConnectionError)
        }
    }

    fun <T> T.toDeferred() = GlobalScope.async { this@toDeferred }
}