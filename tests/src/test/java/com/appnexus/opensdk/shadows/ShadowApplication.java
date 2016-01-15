package com.appnexus.opensdk.shadows;

import android.app.Application;
import android.content.ContentResolver;

import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import static org.robolectric.internal.Shadow.directlyOn;

@Implements(value = Application.class, callThroughByDefault = true)
public class ShadowApplication extends org.robolectric.shadows.ShadowApplication {
    @RealObject
    private Application application;

    @Override
    public ContentResolver getContentResolver() {
        return directlyOn(application, Application.class).getContentResolver();
    }
}
