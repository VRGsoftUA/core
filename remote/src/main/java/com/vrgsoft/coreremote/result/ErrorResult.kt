package com.vrgsoft.coreremote.result

import com.vrgsoft.coreremote.error.BaseError

class ErrorResult<T>(
    val error: BaseError
) : BaseResult<T>()