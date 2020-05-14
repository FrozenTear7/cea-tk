package messages

import actors.BestGenotype
import actors.IGenotype
import kotlinx.coroutines.channels.Channel

class MessagePong(
    val actorId: Int,
    val genotype: IGenotype,
    val bestGenotype: BestGenotype,
    val responseChannel: Channel<IMessage>
) : IMessage