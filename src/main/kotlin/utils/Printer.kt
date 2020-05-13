package utils

// Utility class for cleaner output management.
// If verbose is true, Printer.msg defaults to println()
// If verbose is false, only crucial messages will be printed

class Printer(private val verbose: Boolean) {
    companion object {
        var verbose = true
        public fun msg(content: String, crucial: Boolean = false) : Unit {
            if (verbose or crucial) {
                println(content)
            }
        }
        public fun setMode(mode : Boolean) : Unit {
            verbose = mode
        }
    }
}