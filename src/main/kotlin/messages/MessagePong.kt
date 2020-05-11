package messages

import actors.Actor
import actors.IGenotype

class MessagePong(override val messageType: MessageType, val actor: Actor, val genotype: IGenotype) : IMessage