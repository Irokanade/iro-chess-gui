plugins {
    id("java")
    id("application")
}

group = "com.iro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.iro.Main")
    applicationDefaultJvmArgs = listOf("-Djava.library.path=src/main/cpp/build")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    jvmArgs("-Djava.library.path=src/main/cpp/build")
}

tasks.test {
    useJUnitPlatform()
}