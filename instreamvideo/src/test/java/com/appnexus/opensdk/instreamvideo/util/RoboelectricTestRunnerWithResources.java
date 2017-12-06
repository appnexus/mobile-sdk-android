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
        FsFile androidManifestFile = appManifest.getAndroidManifestFile();
        FsFile resDirectory;
        FsFile assetsDirectory;


        String moduleRoot = getModuleRootPath(config);
        androidManifestFile = FileFsFile.from(moduleRoot, appManifest.getAndroidManifestFile().getPath().replace("bundles", "manifests/aapt"));

        if(appManifest.getResDirectory().getPath().contains("release")) {
            resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath().replace("release", "default"));
            assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath().replace("release", "default"));
        }else{
            resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath().replace("bundles/debug/res", "res/merged/debug"));
            assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath());
        }


        System.out.print(androidManifestFile.getPath() + '\n');
        System.out.print(resDirectory.getPath() + '\n');
        System.out.print(assetsDirectory.getPath() + '\n');


        return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);

    }

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }
}