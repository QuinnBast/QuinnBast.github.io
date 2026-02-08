rootProject.name = "demo-auth-app"

include("demoAuthApp")

// Services:
project(":demoAuthApp").projectDir = File("./demo-auth-app")
