package bast.quinn.finance.training

import bast.quinn.finance.config.ServerConfig
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class TrainingMeta(
    val title: String,
    val description: String,
    val path: String? = null,
    val lessons: List<LessonMeta>,
    val lastUpdated: Long,
)

@Serializable
data class LessonMeta(
    val title: String,
    val fileName: String,
    val path: String? = null,
    val lessonNumber: Int,
)

class TrainingContentLoader(
    private val config: ServerConfig
) {

    private var trainingModules: MutableList<TrainingMeta> = mutableListOf()

    init {
        reloadTrainingMeta()
    }

    // Function to cache blog post meta-data so that we don't need to read each file every time.
    fun reloadTrainingMeta() {
        // get directories in folder
        val basePath = File(config.pathToAssetFiles)
        val training = File(basePath, "training")
        val modules = training
            .listFiles()!!
            .toList()

        // With each module, let's get some meta about it.
        // Like the number of lessons, and a breif description.
        modules.forEach {
            // Search for lessons within each module.
            val filesInModule = it.walk()
            val lessons = filesInModule.filter { it.extension == "md" || it.extension == "adoc" }
            val lessonCount = lessons.count()

            // Read the top level README.md or README.adoc to determine the title:
            val readmemd = File(it, "README.md")
            val readmeadoc = File(it, "README.adoc")
            val readme = if (readmeadoc.exists()) readmeadoc else readmemd

            val title = readme
                .readText()
                .lines()
                .firstOrNull()
                ?.replace("# ", "")
                ?.replace("= ", "")
                ?: it.name

            val description = readme.readText().lines().drop(1).take(4).joinToString("\n")

            val lessonArray = lessons.toList()
                .filter { it.name.split("-").first().toIntOrNull() != null}
                .map {
                val lessonTitle = it
                    .readText()
                    .lines()
                    .firstOrNull()
                    ?.replace("# ", "")
                    ?.replace("= ", "")
                    ?: it.name

                val lessonNumber = it.name.split("-").first().toInt() ?: 0

                LessonMeta(
                    lessonTitle,
                    it.name,
                    "/assets/" + it.toRelativeString(basePath).replace("\\", "/"),
                    lessonNumber
                )
            }.toList()

            trainingModules.add(
                TrainingMeta(
                    title,
                    description,
                    "/assets/" + readme.toRelativeString(basePath).replace("\\", "/"),
                    lessonArray,
                    readme.lastModified()
                )
            )
        }
    }

    fun getTrainingModules() = trainingModules
    fun getTrainingLesson(path: String): TrainingMeta {
        // Search modules to find where this path is a part of the lessons of the module.
        val modulesContainingPath = trainingModules
            .filter { it.lessons.any { lesson -> lesson.path == "/$path" } }

        if (modulesContainingPath.isNotEmpty()) {
            return modulesContainingPath.first()
        } else {
            return trainingModules.filter {
                it.path == "/$path"
            }.firstOrNull() ?: throw Exception("No training module found for path: $path")
        }
    }
}