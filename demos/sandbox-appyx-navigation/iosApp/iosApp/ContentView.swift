import UIKit
import SwiftUI
import ios

struct ComposeView: UIViewControllerRepresentable {

    var lifecycleHelper: LifecycleHelper

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(lifecycleHelper: lifecycleHelper)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var lifecycleHelper: LifecycleHelper

    var body: some View {
        ComposeView(lifecycleHelper: lifecycleHelper)
                .ignoresSafeArea(.all)
    }
}
