/**
 * This class does some testing
 * @author JaronZ
 */
class Test {
	/**
	 * The current value of this test instance
	 */
	private var currentValue: Int

	/**
	 * This function prints hello world
	 */
	fun someMethod() {
		println("Hello World!")
	}

	/**
	 * This function multiplies the current value by the given number
	 * @param number The number to multiply the current value by
	 * @return The resulting number
	 */
	fun mult(number: Int): Int {
		currentValue *= number
		return currentValue
	}

	/**
	 * Returns the current value
	 * @return The current value
	 */
	fun getCurrentValue(): Int = currentValue

	companion object {
		/**
		 * A static method that prints a message
		 */
		@JvmStatic
		fun someStaticMethod() {
			println("This is a static method!")
		}
	}
}

/**
 * This function prints a test message
 */
fun someFunction() {
	println("Test message")
}

/**
 * Sums up two numbers together
 * @param a The first number to sum
 * @param b The second number to sum
 * @return The two numbers summed up
 */
fun sum(
	a: Int,
	b: Int,
): Int = a + b
