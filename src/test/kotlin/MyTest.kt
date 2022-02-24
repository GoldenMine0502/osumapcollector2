import org.junit.Test

class MyTest {
    @Test
    fun stringTest() {
        val data = "123pa4"
        val x = data.filter { it in '0'..'9' }.toInt()

        println(x)
    }
}