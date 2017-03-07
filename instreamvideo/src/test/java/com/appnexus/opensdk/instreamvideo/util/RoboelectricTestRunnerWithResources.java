package com.appnexus.opensdk.instreamvideo.util;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.res.FsFile;

/**
 * More dynamic path resolution.
 * <p>
 * This workaround is only for Mac Users necessary and only if they don't use the $MODULE_DIR$
 * workaround mentioned at http://robolectric.org/getting-started/.
 * <p>
 * Follow this issue at https://code.google.com/p/android/issues/detail?id=158015
 * <p>
 * Modified version of https://github.com/nenick/AndroidStudioAndRobolectric/blob/master/app/src/test/java/com/example/myapplication/CustomRobolectricRunner.java
 */
public class RoboelectricTestRunnerWithResources extends RobolectricTestRunner {

    public RoboelectricTestRunnerWithResources(Class<?> klass) throws InitializationError {
        super(klass);
    }

    // Use this for running from Android Studio 2.2.3+ and command line
    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest appManifest = super.getAppManifest(config);
        FsFile androidManifestFile = FileFsFile.from(appManifest.getAndroidManifestFile().getPath().replace("full", "aapt"));
        FsFile resDirectory = FileFsFile.from(appManifest.getResDirectory().getPath());
        FsFile assetsDirectory = FileFsFile.from(appManifest.getAssetsDirectory().getPath());

        return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);

    }
}