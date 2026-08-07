dependencies {
    api("org.lushplugins:LushLib:1.0.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString() + ".lushrecipes"
            artifactId = rootProject.name + "-API"
            version = rootProject.version.toString()
            from(project.components["java"])
        }
    }
}