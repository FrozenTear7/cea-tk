package messages

import actors.IGenotype

class MessageLoggerPong(override val messageType: MessageType, val bestGenotype: IGenotype) : IMessage