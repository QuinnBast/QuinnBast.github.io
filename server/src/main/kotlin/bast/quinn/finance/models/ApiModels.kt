package bast.quinn.finance.models

import bast.quinn.finance.blog.BlogMeta
import bast.quinn.finance.training.TrainingMeta
import kotlinx.serialization.Serializable

@Serializable
data class TrainingModulesList(
    val modules: List<String>
)

@Serializable
data class BlogMetaResponse(
    val blogItems: List<BlogMeta>
)

@Serializable
data class TrainingModuleResponse(
    val modules: List<TrainingMeta>
)