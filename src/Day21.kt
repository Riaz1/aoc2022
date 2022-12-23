
fun main() {
    data class LineItem(
        val key1: String,
        val operator: String,
        val key2: String,
        var result: Double?
    )

    val fileName =
        "Day21_input"
//        "Day21_sample"

    val steps = readInput(fileName).associateBy(
        keySelector = {v -> v.substringBefore(":").trim()},
        valueTransform = {v ->
            if (v.substringAfter(":").trim().toDoubleOrNull() != null) {
                LineItem("", "", "", v.substringAfter(":").trim().toDoubleOrNull())
            } else {
                LineItem(
                    v.substringAfter(":").trim().split(" ")[0],
                    v.substringAfter(":").trim().split(" ")[1],
                    v.substringAfter(":").trim().split(" ")[2],
                    null
                )
            }
        })

    var rootValue : Double? = null
    while (rootValue == null) {
        run breaking@ {
            steps.forEach { (key, item) ->
                if (key == "root" && item.result != null) {
                    rootValue = item.result
                    return@breaking
                }

                if (item.result == null &&
                    steps[item.key1]?.result != null &&
                    steps[item.key2]?.result != null) {
                    steps[key]?.result = when (item.operator) {
                        "+" -> steps[item.key2]?.result?.let {steps[item.key1]?.result?.plus(it) }
                        "-" -> steps[item.key2]?.result?.let {steps[item.key1]?.result?.minus(it) }
                        "*" -> steps[item.key2]?.result?.let {steps[item.key1]?.result?.times(it) }
                        "/" -> steps[item.key2]?.result?.let {steps[item.key1]?.result?.div(it) }
                        else -> null
                    }
                }
            }
        }
    }

    rootValue.println()
    "%.2f".format(rootValue).println()
}
