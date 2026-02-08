package bast.quinn.finance.config

data class ServerConfig(
    val host: String = "localhost",
    val port: Int = 9000,
    val pathToDist: String = "src/dist/",
    // val pathToTrainingFiles: String = "/training/"
    val pathToAssetFiles: String = "./src/public/"
)