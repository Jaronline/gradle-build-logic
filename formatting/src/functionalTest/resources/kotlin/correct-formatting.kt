class Test {
	fun someOtherMethod(): Int {
		someMethod("Some message")
		return 4
	}

	private fun someMethod(a: String) {
		println(a)
		someFunction()
	}
}

private fun someFunction() {
	println("Some other message")
}
