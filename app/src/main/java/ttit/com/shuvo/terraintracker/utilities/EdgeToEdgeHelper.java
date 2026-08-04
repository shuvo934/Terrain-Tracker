package ttit.com.shuvo.terraintracker.utilities;

import android.os.Build;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EdgeToEdgeHelper {

    private EdgeToEdgeHelper() {}
    /**
     * Call before setContentView().
     */
    public static void enable(@NonNull ComponentActivity activity) {
        EdgeToEdge.enable(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.getWindow()
                    .setNavigationBarContrastEnforced(false);
        }
    }

    public static void applyInsets(
            @NonNull ComponentActivity activity,
            @NonNull View rootView,
            boolean darkStatusBarIcons,
            boolean darkNavigationBarIcons
    ) {

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        activity.getWindow(),
                        activity.getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(darkStatusBarIcons);
        controller.setAppearanceLightNavigationBars(
                darkNavigationBarIcons
        );

        final int initialLeft = rootView.getPaddingLeft();
        final int initialTop = rootView.getPaddingTop();
        final int initialRight = rootView.getPaddingRight();
        final int initialBottom = rootView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    int insetTypes =
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout();

                    Insets safeInsets =
                            windowInsets.getInsets(insetTypes);

                    view.setPadding(
                            initialLeft + safeInsets.left,
                            initialTop + safeInsets.top,
                            initialRight + safeInsets.right,
                            initialBottom + safeInsets.bottom
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(rootView);
    }

    public static void applyInsetsIme(
            @NonNull ComponentActivity activity,
            @NonNull View rootView,
            boolean darkStatusBarIcons,
            boolean darkNavigationBarIcons
    ) {

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        activity.getWindow(),
                        activity.getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(darkStatusBarIcons);
        controller.setAppearanceLightNavigationBars(
                darkNavigationBarIcons
        );

        final int initialLeft = rootView.getPaddingLeft();
        final int initialTop = rootView.getPaddingTop();
        final int initialRight = rootView.getPaddingRight();
        final int initialBottom = rootView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    int insetTypes =
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout();

                    Insets safeInsets =
                            windowInsets.getInsets(insetTypes);

                    Insets imeInsets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.ime()
                    );

                    int requiredBottomInset = Math.max(
                            safeInsets.bottom,
                            imeInsets.bottom
                    );

                    view.setPadding(
                            initialLeft + safeInsets.left,
                            initialTop + safeInsets.top,
                            initialRight + safeInsets.right,
                            initialBottom + requiredBottomInset
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(rootView);
    }
}
