package messages

interface IMessage {
    val messageType: MessageType
    val messageValue: Int
}