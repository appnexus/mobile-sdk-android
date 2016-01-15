package com.appnexus.opensdk.shadows;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.VideoView;
import static org.robolectric.internal.Shadow.directlyOn;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Implements(value = VideoView.class, callThroughByDefault = true)
public class ShadowVideoView extends org.robolectric.shadows.ShadowVideoView{
    private final CustomSurfaceHolder customSurfaceHolder = new CustomSurfaceHolder();

    @RealObject
    private VideoView videoView;

    @Implementation
    public SurfaceHolder getHolder() {
        return customSurfaceHolder;
    }

    public void onCreateSurfaceHolderCallBack() {
        Set<SurfaceHolder.Callback> callbacks = customSurfaceHolder.getCallbacks();
        if (callbacks != null && !callbacks.isEmpty()) {
            Iterator<SurfaceHolder.Callback> iterator = callbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().surfaceCreated(customSurfaceHolder);
            }
        }
    }

    @Implementation
    public void setVideoURI(Uri uri){
        directlyOn(videoView, VideoView.class).setVideoURI(uri);
    }

    @Implementation
    public void setVideoURI(Uri uri, Map<String, String> headers){
        directlyOn(videoView, VideoView.class).setVideoURI(uri, headers);
    }

    @Implementation
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        directlyOn(videoView, VideoView.class).setOnPreparedListener(l);
    }

    @Implementation
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        directlyOn(videoView, VideoView.class).setOnErrorListener(l);
    }

    @Implementation
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        directlyOn(videoView, VideoView.class).setOnCompletionListener(l);
    }

    @Implementation
    public void setVideoPath(String path) {
        directlyOn(videoView, VideoView.class).setVideoPath(path);
    }

    @Implementation
    public void start() {
        directlyOn(videoView, VideoView.class).start();
    }

    @Implementation
    public void stopPlayback() {
        directlyOn(videoView, VideoView.class).stopPlayback();
    }

    @Implementation
    public void suspend() {
        directlyOn(videoView, VideoView.class).suspend();
    }

    @Implementation
    public void pause() {
        directlyOn(videoView, VideoView.class).pause();
    }

    @Implementation
    public void resume() {
        directlyOn(videoView, VideoView.class).resume();
    }

    @Implementation
    public boolean isPlaying() {
        return directlyOn(videoView, VideoView.class).isPlaying();
    }

    @Implementation
    public boolean canPause() {
        return directlyOn(videoView, VideoView.class).canPause();
    }

    @Implementation
    public void seekTo(int msec) {
        directlyOn(videoView, VideoView.class).seekTo(msec);
    }

    @Implementation
    public int getCurrentPosition() {
        return directlyOn(videoView, VideoView.class).getCurrentPosition();
    }

    public static class CustomSurfaceHolder implements SurfaceHolder {
        private final Set<Callback> callbacks = new HashSet<>();

        @Override
        public void addCallback(Callback callback) {
            callbacks.add(callback);
        }

        public Set<Callback> getCallbacks() {
            return callbacks;
        }

        @Override
        public void removeCallback(Callback callback) {
            callbacks.remove(callback);
        }

        @Override
        public boolean isCreating() {
            return false;
        }

        @Override
        public void setType(int i) {
        }

        @Override
        public void setFixedSize(int i, int i1) {
        }

        @Override
        public void setSizeFromLayout() {
        }

        @Override
        public void setFormat(int i) {
        }

        @Override
        public void setKeepScreenOn(boolean b) {
        }

        @Override
        public Canvas lockCanvas() {
            return null;
        }

        @Override
        public Canvas lockCanvas(Rect rect) {
            return null;
        }

        @Override
        public void unlockCanvasAndPost(Canvas canvas) {
        }

        @Override
        public Rect getSurfaceFrame() {
            return null;
        }

        @Override
        public Surface getSurface() {
            return null;
        }
    }

}
