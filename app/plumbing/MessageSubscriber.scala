package plumbing

import akka.actor.{ActorSystem, Props}
import com.eigenroute.plumbing.{MessageBrokerMessageType, RabbitMQPublisherSubscriber}
import com.google.inject.Inject
import play.api.inject.ApplicationLifecycle

class MessageSubscriber @Inject() (
    override val actorSystem: ActorSystem,
    override val lifecycle: ApplicationLifecycle
  )
  extends RabbitMQPublisherSubscriber {

  override val props: Props = MessageBrokerMessageDispatcher.props(publish)
  
  override val convert: (String) => Option[MessageBrokerMessageType] = (foo: String) => None //TODO: Replace with yor own implementation

}