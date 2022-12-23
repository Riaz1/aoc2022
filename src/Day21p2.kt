fun main() {
    data class LineItem(
        val operand1: String,
        val operator: String,
        val operand2: String,
        var result: Double?,
        var result1: Double?,
        var result2: Double?,
        var parent: String?
    )

    fun getPathFromRoot(key: String, steps: Map<String, LineItem>): ArrayDeque<String> {
        var path = ArrayDeque<String>()

        path.addFirst(key)
        var item = steps[key]

        while(item?.parent != null) {
            path.addFirst(item?.parent!!)
            item = steps[item?.parent]
        }

        path.removeFirst() //remove root
        return path
    }

    //total = x operator y
    fun solveForXAsOperand1(total: Double?, operator: String?, y: Double?): Double? {
        return when (operator) {
            "+" -> y?.let { total?.minus(it) }
            "-" -> y?.let { total?.plus(it) }
            "*" -> y?.let { total?.div(it) }
            "/" -> y?.let { total?.times(it) }
            else -> null
        }
    }

    //total = y operator x
    fun solveForXAsOperand2(total: Double?, operator: String?, y: Double?): Double? {
        return when (operator) {
            "+" -> y?.let { total?.minus(it) }
            "-" -> y?.let { (total?.minus(it))?.times(-1) }
            "*" -> y?.let { total?.div(it) }
            "/" -> y?.let { it.div(total!!) }
            else -> null
        }
    }

    val fileName =
        "Day21_input"
//        "Day21_sample"

    val steps = readInput(fileName).associateBy(
        keySelector = {v -> v.substringBefore(":").trim()},
        valueTransform = {v ->
            if (v.substringAfter(":").trim().toDoubleOrNull() != null) {
                LineItem(
                    "",
                    "",
                    "",
                    v.substringAfter(":").trim().toDoubleOrNull(),
                    null,
                    null,
                    null)
            } else {
                LineItem(
                    v.substringAfter(":").trim().split(" ")[0],
                    v.substringAfter(":").trim().split(" ")[1],
                    v.substringAfter(":").trim().split(" ")[2],
                    null,
                    null,
                    null,
                    null
                )
            }
        })

    var rootValue : Double? = null
    while (rootValue == null) {
        run breaking@ {
            steps.forEach { (key, item) ->
                steps[item.operand1]?.let {
                    if (it.parent == null) {
                        steps[item.operand1]?.parent = key
                    }
                }

                steps[item.operand2]?.let {
                    if (it.parent == null) {
                        steps[item.operand2]?.parent = key
                    }
                }

                if (key == "root" && item.result != null) {
                    rootValue = item.result
                    return@breaking
                }

                if (item.result == null &&
                    steps[item.operand1]?.result != null &&
                    steps[item.operand2]?.result != null) {

                    steps[key]?.result1 = steps[item.operand1]?.result
                    steps[key]?.result2 = steps[item.operand2]?.result

                    steps[key]?.result = when (item.operator) {
                        "+" -> steps[item.operand2]?.result?.let {steps[item.operand1]?.result?.plus(it) }
                        "-" -> steps[item.operand2]?.result?.let { steps[item.operand1]?.result?.minus(it) }
                        "*" -> steps[item.operand2]?.result?.let {steps[item.operand1]?.result?.times(it) }
                        "/" -> steps[item.operand2]?.result?.let {steps[item.operand1]?.result?.div(it) }
                        else -> null
                    }
                }
            }
        }
    }

    rootValue.println()
    "%.2f".format(rootValue).println()

    //part 2
    val target = "humn"
    val rootToTarget = getPathFromRoot(target, steps)

    var newResult: Double?
    var nextNode = steps["root"]
    newResult = if (rootToTarget.first() == nextNode?.operand1) {
        nextNode?.result2
    } else {
        nextNode?.result1
    }

    while (rootToTarget.isNotEmpty()) {
        val key = rootToTarget.removeFirst()
        if (key == target) {
            "%.2f".format(newResult).println()
            break
        }

        nextNode = steps[key]
        val x = rootToTarget.first() //solve for x
        var xIsOperand1 = false
        val y = if (nextNode?.operand1 == x) {
            xIsOperand1 = true
            nextNode?.result2
        } else {
            nextNode?.result1
        }

        //println("solve for $x where y is $y and xisoperand1: $xIsOperand1 and the total is: $newResult")
        newResult = if (xIsOperand1) {
            solveForXAsOperand1(newResult, nextNode?.operator, y)
        } else {
            solveForXAsOperand2(newResult, nextNode?.operator, y)
        }
    }
}

