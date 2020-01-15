package brandon.backend.backendAssignment1Manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendAssignment1ManagerApplication

//TODO should really rename this to slave and use another program to delegate to these containers

fun main(args: Array<String>) {
	runApplication<BackendAssignment1ManagerApplication>(*args)
}
