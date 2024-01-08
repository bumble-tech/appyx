import SwiftUI
import ios

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ZStack {
                Color.white.ignoresSafeArea(.all) // status bar color
                ContentView()
            }
            .onOpenURL { incomingURL in
                Main_iosKt.handleDeepLinks(url: incomingURL)
            }
        }
    }
}
