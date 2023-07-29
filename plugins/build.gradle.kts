val buildTask = tasks.register("buildPlugins")
val detektTask = tasks.register("detekt")

subprojects {
    buildTask.configure { dependsOn(tasks.named("build")) }
    detektTask.configure { dependsOn(tasks.named("detekt")) }
}
