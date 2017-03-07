package com.appnexus.opensdk.util;

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
        FsFile androidManifestFile = appManifest.getAndroidManifestFile();

        if (androidManifestFile.exists()) {
            return appManifest;
        } else {
            String moduleRoot = getModuleRootPath(config);
            androidManifestFile = FileFsFile.from(moduleRoot, appManifest.getAndroidManifestFile().getPath().replace("bundles", "manifests/full"));
            FsFile resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath().replace("/res", "").replace("bundles", "res"));
            FsFile assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath().replace("/assets", "").replace("bundles", "assets"));
            return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);
        }
    }

    // Use this for running from older version of Android Studio
    /*protected AndroidManifest getAppManifest(Config config) {

            AndroidManifest appManifest = super.getAppManifest(config);

            FsFile androidManifestFile = FileFsFile.from(appManifest.getAndroidManifestFile().getPath());
            FsFile resDirectory = FileFsFile.from(appManifest.getResDirectory().getPath().replace("debug", "androidTest/debug"));
            FsFile assetsDirectory = FileFsFile.from(appManifest.getAssetsDirectory().getPath().replace("debug", "androidTest/debug"));

            return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);

    }*/

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }
}