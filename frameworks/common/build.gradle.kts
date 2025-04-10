plugins {
    id("framework-convention")
}

noArg {
    annotation("faustofan.app.framework.common.annotation.NoArgConstructor")
}

dependencies {

}

tasks.test {
    enabled = false
}