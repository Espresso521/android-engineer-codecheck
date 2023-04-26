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
        assertEquals(4, 2 + 2)
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

}