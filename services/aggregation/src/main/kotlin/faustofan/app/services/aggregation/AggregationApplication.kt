package faustofan.app.services.aggregation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class AggregationApplication

fun main(args: Array<String>) {
	runApplication<AggregationApplication>(*args)
}

@RestController
class GreetingController {

	@GetMapping("/")
	fun greet(): String {
		return "Hello World!"
	}
}

