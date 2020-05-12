package messages

import actors.Actor

class MessagePong(override val messageType: MessageType, val actor: Actor) : IMessage