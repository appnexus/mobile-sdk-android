package com.appnexus.opensdk.util;

import com.appnexus.opensdk.BuildConfig;

import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.ConfigUtils;
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

    //@FIXME - Only permanant solution for this is to use Android studio 3.0 and do
    // includeAndroidResources = true
    // We can wait till Roboelectic 3.8 release to do this http://robolectric.org/blog/2017/11/13/resources-for-real/
    public RoboelectricTestRunnerWithResources(Class<?> klass) throws InitializationError {
        super(klass);
    }


    // Works with Android Studio 2.3.3 and  buildToolsVersion '25.0.0' Upgrade to these if you are not using them dont change this setting untill unless the versions are incremented and to support newer versions.
    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest appManifest = super.getAppManifest(config);
        FsFile androidManifestFile = appManifest.getAndroidManifestFile();
        FsFile resDirectory;
        FsFile assetsDirectory;


        System.out.print("Before::androidManifestFile" + androidManifestFile.getPath() + '\n');
        System.out.print("Before::getResDirectory" + appManifest.getResDirectory().getPath() + '\n');
        System.out.print("Before::getAssetsDirectory" + appManifest.getAssetsDirectory().getPath() + '\n');


        String moduleRoot = getModuleRootPath(config);

        /**************** Works for Command Line and Jenkins - START *********************************/
            resDirectory = FileFsFile.from(appManifest.getResDirectory().getPath());
            assetsDirectory = FileFsFile.from(appManifest.getAssetsDirectory().getPath());
        /**************** Works for Command Line and Jenkins - END *********************************/




        /**************** Works with Android Studio Never check in this - START *********************************/
/*        if(appManifest.getResDirectory().getPath().contains("release")) {
            resDirectory = FileFsFile.from(appManifest.getResDirectory().getPath().replace("release", "default"));
            assetsDirectory = FileFsFile.from(appManifest.getAssetsDirectory().getPath().replace("release", "default"));
        }else{
            resDirectory = FileFsFile.from(appManifest.getResDirectory().getPath().replace("/debug", "/androidTest/debug"));
            assetsDirectory = FileFsFile.from(appManifest.getAssetsDirectory().getPath());

        }*/
        /**************** Works with Android Studio Never check in this - END *********************************/


        System.out.print("After::androidManifestFile" + androidManifestFile.getPath() + '\n');
        System.out.print("After::getResDirectory" + resDirectory.getPath() + '\n');
        System.out.print("After::getAssetsDirectory" + assetsDirectory.getPath() + '\n');


        return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);

    }

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }


}