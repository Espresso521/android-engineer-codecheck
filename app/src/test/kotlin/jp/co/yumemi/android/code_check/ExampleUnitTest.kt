package jp.co.yumemi.android.code_check

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val ret = integerBreak(10)
        println("huze ret is $ret")
    }

    fun integerBreak(n: Int): Int {
        val dp: Array<List<Int>> = arrayOf(
            listOf(0, 0),
            listOf(0, 1),
            listOf(1, 1),
            listOf(1, 2),
            listOf(2, 2),
            listOf(2, 3),
            listOf(3, 3),
            listOf(3, 2, 2),
            listOf(3, 3, 2),
            listOf(3, 3, 3),
            listOf(3, 3, 2, 2),//10
            listOf(3, 3, 3, 2),
            listOf(3, 3, 3, 3),
            listOf(3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 2),//20
            listOf(3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3),//30
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),//40
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),//50
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),//60
        )

        dp[n].reduceIndexed {
            index, acc, num ->
                println("huze index is $index acc is $acc, num is $num")
            acc*num
        }

        return dp[n].reduce { acc, num ->
            println("huze acc is $acc, num is $num")
            acc * num
        }
    }

    fun integerBreak1(n: Int): Int {
        val dp: Array<List<Int>> = arrayOf(
            listOf(0, 0),
            listOf(0, 1),
            listOf(1, 1),
            listOf(1, 2),
            listOf(2, 2),
            listOf(2, 3),
            listOf(3, 3),
            listOf(3, 2, 2),
            listOf(3, 3, 2),
            listOf(3, 3, 3),
            listOf(3, 3, 2, 2),//10
            listOf(3, 3, 3, 2),
            listOf(3, 3, 3, 3),
            listOf(3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 2),//20
            listOf(3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3),//30
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),//40
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),//50
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
            listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),//60
        )

        if(n == 2) return 1
        if(n == 3) return 2

        return if(n % 3 == 0) {
            myPow(3, n/3)
        } else if(n%3 ==1){
            myPow(3, (n/3)-1) * 4
        } else {
            myPow(3, n/3) * 2
        }

    }

    private fun myPow(e: Int, x: Int): Int {
        var ret = 1
        for(i in 0 until x) {
            ret *= e
        }
        println("myPow ret is $ret")
        return ret
    }

    @Test
    fun commonTest() {
        var image = arrayOf(intArrayOf(1, 1, 1), intArrayOf(1, 1, 0), intArrayOf(1, 0, 1))
        val ret = floodFill(image, 1, 1, 2)
        for (i in 0 until image.size) {
            for (j in 0 until image[0].size) {
                println("ret[$i][$j] = ${ret[i][j]}")
            }
        }
        assert(ret[0][0] == 2)
    }

    private fun floodFill(image: Array<IntArray>, sr: Int, sc: Int, color: Int): Array<IntArray> {
        if (image[sr][sc] == color) return image
        helper(image, sr, sc, image[sr][sc], color)
        return image
    }


    private fun helper(image: Array<IntArray>, i: Int, j: Int, color: Int, newColor: Int) {
        val m = image.size
        val n = image[0].size
        if (i < 0 || i >= m || j < 0 || j >= n || image[i][j] != color) return
        image[i][j] = newColor
        helper(image, i + 1, j, color, newColor)
        helper(image, i, j + 1, color, newColor)
        helper(image, i - 1, j, color, newColor)
        helper(image, i, j - 1, color, newColor)
    }


    @Test
    fun just4Test() {
        val a = setOf("a", "a1", "a2", "a")
        println("huze : " + a.count())
        generateSequence { }
    }

    @Test
    fun myTest() {
        val a = getPlayTime(8133000)
        println("huze: $a")

        val nums = intArrayOf(1, 1, 2, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6)
        var ret = searchLeft(nums, 5)
        println("huze: $ret")

        ret = searchRight(nums, 5)
        println("huze: $ret")

        val rnums = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        rotateArray(rnums, 5)
        rnums.forEach {
            println(it)
        }
    }

    @Test
    fun peakElement() {
        val ret = findPeakElement(intArrayOf(8, 9, 7, 6, 5, 4, 3, 2, 1))
        println("huze: $ret")
    }

    @Test
    fun removeDupElement() {
        val nums = ListNode(1)
        var t = nums
        t.next = ListNode(1)
        t = t.next!!
        t.next = ListNode(0)
        t = t.next!!
        t.next = ListNode(2)
        t = t.next!!
        t.next = ListNode(2)
        t = t.next!!
        t.next = ListNode(3)
        t = t.next!!
        t.next = ListNode(3)
        t = t.next!!
        t.next = ListNode(5)
        t = t.next!!
        t.next = ListNode(4)
        t = t.next!!
        t.next = ListNode(4)
        t = t.next!!

        var a = deleteDuplicates(nums)

        while(a!=null) {
            println("a.val ${a.`val`}")
            a=a.next
        }
    }

    class ListNode(var `val`: Int) {
        var next: ListNode? = null
    }

    fun deleteDuplicates(head: ListNode?): ListNode? {
        if (head == null) return null
        val tempHead = ListNode(0).apply { next = head }
        var previous: ListNode = tempHead
        var current: ListNode = head
        var next = current.next
        var toDelete = Integer.MAX_VALUE
        while (next != null) {
            if (next!!.`val` == current.`val`) {
                previous.next = next!!.next
                toDelete = current.`val`
            } else if (current.`val` != toDelete) {
                previous = current
            }
            current = next!!
            next = next!!.next
        }
        return tempHead.next
    }


    fun findPeakElement(nums: IntArray): Int {
        var leftIndex = 0
        var rightIndex = nums.lastIndex

        while (leftIndex < rightIndex) {
            val midIndex = (leftIndex + rightIndex) ushr 1
            println("huze: midIndex is $midIndex")
            when {
                nums[midIndex] > nums[midIndex + 1] -> rightIndex = midIndex
                nums[midIndex] < nums[midIndex + 1] -> leftIndex = midIndex + 1
            }

            println("huze: rightIndex is $rightIndex")
            println("huze: leftIndex is $leftIndex")
        }
        return leftIndex
    }

    private fun getPlayTime(time: Int): String {
        val duration = time.div(1000.0).plus(0.5).toInt()
        val hour = duration / 3600
        val minute = (duration - (hour * 3600)) / 60
        val second = duration % 60
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    fun searchRight(nums: IntArray, target: Int): Int {
        var left = 0
        var right = nums.size - 1
        var index = -1
        if (target < nums[0]) return -1
        if (target > nums[right]) return -1
        var ret = -1
        while (left <= right) {
            index = left + (right - left) / 2
            if (nums[index] == target) {
                ret = index
                left = index + 1
            } else if (nums[index] < target) { //nums[index] < target < nums[index + 1]
                left = index + 1
            } else { //nums[index] > target > nums[index - 1]
                right = index - 1
            }
        }
        return ret
    }

    fun searchLeft(nums: IntArray, target: Int): Int {
        var left = 0
        var right = nums.size - 1
        var index = -1
        if (target < nums[0]) return -1
        if (target > nums[right]) return -1
        var ret = -1
        while (left <= right) {
            index = left + (right - left) / 2
            if (nums[index] == target) {
                ret = index
                right = index - 1
            } else if (nums[index] < target) { //nums[index] < target < nums[index + 1]
                left = index + 1
            } else { //nums[index] > target > nums[index - 1]
                right = index - 1
            }
        }
        return ret
    }

    fun rotateArray(arr: IntArray, steps: Int) {
        val n = arr.size
        val k = steps % n

        // Reverse the first n - k elements of the array
        reverseArray(arr, 0, n - k - 1)

        // Reverse the last k elements of the array
        reverseArray(arr, n - k, n - 1)

        // Reverse the entire array
        reverseArray(arr, 0, n - 1)
    }

    fun reverseArray(arr: IntArray, start: Int, end: Int) {
        var i = start
        var j = end
        while (i < j) {
            val temp = arr[i]
            arr[i] = arr[j]
            arr[j] = temp
            i++
            j--
        }
    }

    @Test
    fun forTest() {
//        val max = lengthOfLIS(intArrayOf(1,3,6,7,9,4,10,5,6))
        val max = minDistance("leetcode", "etco")
        println("huze: max is $max")
    }

    fun minDistance(word1: String, word2: String): Int {
        val m = word1.length
        val n = word2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        // Base cases
        for (i in 0..m) {
            dp[i][0] = i
        }
        for (j in 0..n) {
            dp[0][j] = j
        }

        // Fill the dp table
        for (i in 1..m) {
            for (j in 1..n) {
                if (word1[i - 1] == word2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                }
            }
        }

        dp.forEachIndexed{ i, v1->
            v1.forEachIndexed { j, v->
                print("kotaku: dp[$i][$j] is $v ; ")
            }
            println()
        }

        return dp[m][n]
    }

    fun lengthOfLIS(nums: IntArray): Int {
        val dp = IntArray(nums.size) { 1 } // 初始化 dp 数组，每个元素默认为 1

        for (i in 1 until nums.size) {
            for (j in 0 until i) {
                if (nums[i] > nums[j]) {
                    dp[i] = maxOf(dp[i], dp[j] + 1)
                }
            }
        }

        dp.forEachIndexed{ index, v->
            println("kotaku: dp[$index] is $v")
        }

        return dp[nums.size -1]
    }


    fun fibonacci(n: Int): Int {
        return if (n <= 1) {
            n
        } else {
            fibonacci(n - 1) + fibonacci(n - 2)
        }
    }

    fun fibonaccinew(n: Int): Int {
        return if (n <= 2) {
            n
        } else {
            fibonacci(n - 1) + fibonacci(n - 2) + fibonacci(n - 3)
        }
    }

    fun numDecodings(s: String): Int {
        if (s.isEmpty() || s[0] == '0') {
            return 0
        }

        val n = s.length
        val dp = IntArray(n + 1)
        dp[0] = 1
        dp[1] = 1

        for (i in 2..n) {
            val oneDigit = s.substring(i - 1, i).toInt()
            val twoDigits = s.substring(i - 2, i).toInt()

            if (oneDigit in 1..9) {
                dp[i] += dp[i - 1]
            }

            if (twoDigits in 10..26) {
                dp[i] += dp[i - 2]
            }
        }

        dp.forEachIndexed() { index, value ->
            println("kotaku: dp[$index] is $value")
        }

        return dp[n]
    }


    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        backtrack(nums, 0, mutableListOf(), result)
        return result
    }

    private fun backtrack(nums: IntArray, index: Int, currentSubset: MutableList<Int>, result: MutableList<List<Int>>) {
        result.add(currentSubset.toList())
        for (i in index until nums.size) {
            currentSubset.add(nums[i])
            backtrack(nums, i + 1, currentSubset, result)
            currentSubset.removeAt(currentSubset.size - 1)
        }
    }

    fun maxProfit(prices: IntArray): Int {
        var max = 0
        var currentGap = 0
        for(i in 1 until prices.size) {
            currentGap += prices[i] - prices[i - 1]
            if (currentGap < 0)
                currentGap = 0
            else if (currentGap > max)
                max = currentGap
        }
        return max
    }

    fun runningSum(nums: IntArray): IntArray {
        val ret = IntArray(nums.size)

        for(i in nums.indices) {
            ret[i] = nums.take(i+1).sum()
        }

        return ret
    }

}