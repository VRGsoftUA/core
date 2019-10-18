package com.vrgsoft.coregateway

import com.vrgsoft.coreremote.error.BaseError
import com.vrgsoft.networkmanager.NetworkManager

class MockedGateway(manager: NetworkManager) : BaseGateway(manager) {
    override fun calculateMessage(error: BaseError): String {
        return ""
    }
}