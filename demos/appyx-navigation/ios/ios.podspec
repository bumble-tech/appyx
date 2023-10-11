Pod::Spec.new do |spec|
    spec.name                     = 'ios'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'https://bumble-tech.github.io/appyx/navigation/'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'https://github.com/bumble-tech/'
    spec.license                  = 'Apache License, Version 2.0'
    spec.summary                  = 'appyx-navigation iOS module'
    spec.vendored_frameworks      = 'build/cocoapods/framework/ios.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '16.4'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':demos:appyx-navigation:ios',
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