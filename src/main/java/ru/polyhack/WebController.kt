package ru.polyhack

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class WebController(
        private val projectService: ProjectService
) {

    @RequestMapping("/")
    fun index() = "index"


    @RequestMapping("/add-team")
    fun addTeam(
            @RequestParam("name") name: String,
            @RequestParam("team") team: String
    ) : Any {
        projectService.add(name = name, team = team)
        return RedirectView("/")
    }

    @RequestMapping("/stats")
    fun stats() : Any {
        return RedirectView("/")
    }
}
