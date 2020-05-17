package messages

import genotypes.BestGenotype
import genotypes.IGenotype
import kotlinx.coroutines.channels.Channel

class MessagePong(
    val actorId: Int,
    val genotype: IGenotype,
    val bestGenotype: BestGenotype,
    val responseChannel: Channel<IMessage>
) : IMessage