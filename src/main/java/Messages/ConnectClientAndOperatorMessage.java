package Messages;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectClientAndOperatorMessage {
    ActorRef operator;
    ActorRef client;
}
