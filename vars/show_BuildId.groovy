
/** @return The tag name, or `null` if the current commit isn't a tag. */
String gitTagName() {
    commit = getCommit()
    if (commit) {
        desc = sh(script: "git describe --tags ${commit}", returnStdout: true)?.trim()
        if (isTag(desc)) {
            return desc
        }
    }
    return null
}

/** @return The tag message, or `null` if the current commit isn't a tag. */
String gitTagMessage() {
    name = gitTagName()
    msg = sh(script: "git tag -n10000 -l ${name}", returnStdout: true)?.trim()
    if (msg) {
        return msg.substring(name.size()+1, msg.size())
    }
    return null
}

@NonCPS
boolean isTag(String desc) {
    match = desc =~ /.+-[0-9]+-g[0-9A-Fa-f]{6,}$/
    result = !match
    match = null // prevent serialisation
    return result
}
String getCommit() {
    return sh(script: 'git rev-parse HEAD', returnStdout: true)?.trim()
}



def call() {

    def VERSION_NUMBER=VersionNumber([
        versionNumberString :'${BUILD_MONTH}.${BUILDS_TODAY}.${BUILD_NUMBER}',
        projectStartDate : '2019-02-09',
        versionPrefix : 'v'
        ])
    
    def TAG_NAME = gitTagName();
    
    if ( ${GIT_BRANCH} == 'master' || ${GIT_BRANCH} == 'hostfix' || ${GIT_BRANCH} == 'develop' ) {
         return ${VERSION_NUMBER} ;
    }else if ( ${GIT_BRANCH} == 'release' || ( ${GIT_BRANCH} ==~ 'Feature-*' &&  ${TAG_NAME} ==~ 'release-*' ) )
         return '${VERSION_NUMBER}-SNAPSHOT'
    }else {
         retrun 'Not-Applicable'
    }

}