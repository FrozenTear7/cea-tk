package messages

import actors.IGenotype

class MessageReplace(override val messageType: MessageType, val genotype: IGenotype) : IMessage