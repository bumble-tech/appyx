Pod::Spec.new do |spec|
    spec.name                     = 'ios'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'https://bumble-tech.github.io/appyx/interactions/'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'https://github.com/bumble-tech/'
    spec.license                  = 'Apache License, Version 2.0'
    spec.summary                  = 'appyx-interactions ios module'
    spec.vendored_frameworks      = 'build/cocoapods/framework/ios.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '16.4'
                
                
    if !Dir.exist?('build/cocoapods/framework/ios.framework') || Dir.empty?('build/cocoapods/framework/ios.framework')
        raise "

        Kotlin framework 'ios' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :demos:appyx-interactions:ios:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':demos:appyx-interactions:ios',
        'PRODUCT_MODULE_NAME' => 'ios',
    }
                
    spec.script_phases = [
        {
            :name => 'Build ios',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['build/compose/ios/ios/compose-resources']
end