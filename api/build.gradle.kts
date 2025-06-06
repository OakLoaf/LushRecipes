dependencies {
    api("org.lushplugins:LushLib:0.10.73")
}

tasks {
    shadowJar {
        relocate("org.lushplugins.lushlib", "org.lushplugins.lushrecipes.api.libraries.lushlib")
    }
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