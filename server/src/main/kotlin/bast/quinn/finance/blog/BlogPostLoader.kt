package bast.quinn.finance.blog

import bast.quinn.finance.config.ServerConfig
import kotlinx.serialization.Serializable
import org.yaml.snakeyaml.Yaml
import java.io.File

@Serializable
data class BlogMeta(
    val title: String,
    val path: String? = null,
    val meta: Map<String, String>
)

class BlogPostLoader(
    private val config: ServerConfig
) {

    private var blogPosts: List<BlogMeta> = listOf()

    init {
        reloadBlogMeta()
    }

    // Function to cache blog post meta-data so that we don't need to read each file every time.
    fun reloadBlogMeta() {
        // get directories in folder
        val basePath = File(config.pathToAssetFiles)
        val articles = File(basePath, "articles")
        val files = articles
            .walk()
            .filter { it.extension == "md"} // Filter only blog posts.

        // With each file, open it and get the blog meta.
        blogPosts = files.toList().map {
            val text = it.readText()
            val yamlMeta = text.split("---")[1]

            val yaml = Yaml()
            val meta = yaml.load<MutableMap<String, Any>>(yamlMeta)

            meta["filename"] = it.name

            val metaAsString = meta.mapValues {
                it.value.toString()
            }

            BlogMeta(
                meta["title"] as String,
                "/assets/" + it.toRelativeString(basePath).replace("\\", "/"),
                metaAsString
            )
        }
    }

    fun getBlogMeta() = blogPosts
    fun getBlogContent(path: String) = File(config.pathToAssetFiles + "/articles/$path").readText()
}