package messages

import actors.BestGenotype

class MessageLoggerPong(override val messageType: MessageType, val bestGenotype: BestGenotype) : IMessage