package messages

import actors.Actor

class MessagePing(override val messageType: MessageType, val actor: Actor) : IMessage