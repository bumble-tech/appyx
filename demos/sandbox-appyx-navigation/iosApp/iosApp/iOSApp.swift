import SwiftUI
import ios
import Foundation
import UIKit

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    @Environment(\.scenePhase) var scenePhase

	var body: some Scene {
		WindowGroup {
		    ZStack {
		        Color.white.ignoresSafeArea(.all) // status bar color
			    ContentView(lifecycleHelper: appDelegate.lifecycleHolder.lifecycleHelper)
			}
			.preferredColorScheme(.light)
	        .onChange(of: scenePhase) { newPhase in
                   switch newPhase {
                   case .background: appDelegate.lifecycleHolder.lifecycleHelper.created()
                   case .inactive: appDelegate.lifecycleHolder.lifecycleHelper.created()
                   case .active: appDelegate.lifecycleHolder.lifecycleHelper.resumed()
                   @unknown default: break
                   }
               }
		}
	}
}

class AppDelegate: NSObject, UIApplicationDelegate {
    let lifecycleHolder: LifecycleHolder = LifecycleHolder()
}

class LifecycleHolder {

    let lifecycleHelper: LifecycleHelper

    init() {
        lifecycleHelper = LifecycleHelper()
        lifecycleHelper.created()
    }

    deinit {
        // Destroy the root component before it is deallocated
        lifecycleHelper.destroyed()
    }
}
