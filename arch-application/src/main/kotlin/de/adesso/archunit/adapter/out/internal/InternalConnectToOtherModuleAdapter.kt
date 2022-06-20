package de.adesso.archunit.adapter.out.internal

import de.adesso.archunit.application.port.out.ConnectToOtherModulePort
import org.springframework.stereotype.Service

@Service
internal class InternalConnectToOtherModuleAdapter: ConnectToOtherModulePort {
    override fun doSomething() {
        TODO("Not yet implemented")
    }
}
