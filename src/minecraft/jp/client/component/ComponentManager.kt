package jp.client.component

import jp.client.Client
import jp.client.component.impl.BlinkComponent
import jp.client.component.impl.ProjectionComponent
import jp.client.component.impl.RotationComponent
import jp.client.component.impl.ViaMCPComponent

class ComponentManager : ArrayList<Component>() {
    init {
        this.addAll(listOf(
            BlinkComponent,
            ProjectionComponent,
            RotationComponent,
            ViaMCPComponent
        ))

        this.forEach { component ->
            Client.eventBus.register(component)
        }
    }
}