package messages

import genotypes.BestGenotype
import kotlinx.coroutines.channels.Channel

class MessagePing(val actorId: Int, val bestGenotype: BestGenotype, val responseChannel: Channel<IMessage>) : IMessage