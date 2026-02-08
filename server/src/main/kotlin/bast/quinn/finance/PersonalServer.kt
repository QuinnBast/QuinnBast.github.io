package bast.quinn.finance

import bast.quinn.finance.PersonalServerMain.Companion.appMicrometerRegistry
import bast.quinn.finance.blog.BlogPostLoader
import bast.quinn.finance.config.ServerConfig
import bast.quinn.finance.models.BlogMetaResponse
import bast.quinn.finance.models.TrainingModuleResponse
import bast.quinn.finance.models.TrainingModulesList
import bast.quinn.finance.training.TrainingContentLoader
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Application.routing(config: ServerConfig) {

    val blogLoader = BlogPostLoader(config)
    val trainingLoader = TrainingContentLoader(config)

    routing {
        singlePageApplication {
            vue(config.pathToDist)
        }
        staticFiles("/assets", File(config.pathToAssetFiles))

        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
        route("/api") {
            get("/training/modules") {
                call.respond(TrainingModuleResponse(trainingLoader.getTrainingModules()))
            }
            get("/training/meta/{...}") {
                // Get the lesson meta for the chosen module.
                call.respond(trainingLoader.getTrainingLesson(call.request.path().split("/training/meta/")[1]))
            }
            get("/blog/meta") {
                call.respond(BlogMetaResponse(blogLoader.getBlogMeta()))
            }
//            get("/blog/content") {
//                call.respond(blogLoader.getBlogContent(call.parameters["path"]!!))
//            }
            get("/reload") {
                blogLoader.reloadBlogMeta()
                trainingLoader.reloadTrainingMeta()
                call.respondText("OK")
            }
        }
    }
}