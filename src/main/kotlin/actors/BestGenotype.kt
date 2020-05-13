package actors

import java.text.SimpleDateFormat
import java.util.*

class BestGenotype(genotype: IGenotype) {
    var timestamp: Long = System.currentTimeMillis()
    var genotype: IGenotype = genotype
        set(value) {
            timestamp = System.currentTimeMillis()
//        if() { // Fitness check for genotype
            field = value
//        }
        }

    fun getFormattedTimestamp(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS")
        return format.format(date)
    }
}