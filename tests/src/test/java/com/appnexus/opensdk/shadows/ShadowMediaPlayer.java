package com.appnexus.opensdk.shadows;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.io.IOException;
import java.util.Map;

import static org.robolectric.internal.Shadow.directlyOn;

@Implements(value = MediaPlayer.class, callThroughByDefault = true)
public class ShadowMediaPlayer extends org.robolectric.shadows.ShadowMediaPlayer {
    @RealObject
    private MediaPlayer player;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Implementation
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException {
        directlyOn(player, MediaPlayer.class).setDataSource(context, uri, headers);
    }

    @Implementation
    public void setDataSource(String path) throws IOException {
        directlyOn(player, MediaPlayer.class).setDataSource(path);
    }

    @Implementation
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        directlyOn(player, MediaPlayer.class).setOnCompletionListener(listener);
    }

    @Implementation
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        directlyOn(player, MediaPlayer.class).setOnSeekCompleteListener(listener);
    }

    @Implementation
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        directlyOn(player, MediaPlayer.class).setOnPreparedListener(listener);
    }

    @Implementation
    public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
        directlyOn(player, MediaPlayer.class).setOnInfoListener(listener);
    }

    @Implementation
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        directlyOn(player, MediaPlayer.class).setOnErrorListener(listener);
    }

    @Implementation
    public boolean isLooping() {
        return directlyOn(player, MediaPlayer.class).isLooping();
    }

    @Implementation
    public void setLooping(boolean looping) {
        directlyOn(player, MediaPlayer.class).setLooping(looping);
    }

    @Implementation
    public void setVolume(float left, float right) {
        directlyOn(player, MediaPlayer.class).setVolume(left, right);
    }

    @Implementation
    public boolean isPlaying() {
        return directlyOn(player, MediaPlayer.class).isPlaying();
    }

    @Implementation
    public void prepare() {
        try {
            directlyOn(player, MediaPlayer.class).prepare();
        } catch (IOException e) {
        }
    }

    @Implementation
    public void prepareAsync() {
        super.prepareAsync();
        directlyOn(player, MediaPlayer.class).prepareAsync();
    }

    @Implementation
    public void start() {
        directlyOn(player, MediaPlayer.class).start();
    }

    @Implementation
    public void _pause() {
        directlyOn(player, MediaPlayer.class, "_pause");
    }

    @Implementation
    public void _release() {
        directlyOn(player, MediaPlayer.class, "_release");
    }

    @Implementation
    public void _reset() {
        directlyOn(player, MediaPlayer.class, "_reset");
    }

    @Implementation
    public void _stop() {
        directlyOn(player, MediaPlayer.class, "_stop");
    }

    @Implementation
    public void attachAuxEffect(int effectId) {
        directlyOn(player, MediaPlayer.class).attachAuxEffect(effectId);
    }

    @Implementation
    public int getAudioSessionId() {
        return directlyOn(player, MediaPlayer.class).getAudioSessionId();
    }

    @Implementation
    public int getCurrentPosition() {
        return directlyOn(player, MediaPlayer.class).getCurrentPosition();
    }

    @Implementation
    public int getDuration() {
        return directlyOn(player, MediaPlayer.class).getDuration();
    }

    @Implementation
    public int getVideoHeight() {
        return directlyOn(player, MediaPlayer.class).getVideoHeight();
    }

    @Implementation
    public int getVideoWidth() {
        return directlyOn(player, MediaPlayer.class).getVideoWidth();
    }

    @Implementation
    public void seekTo(int seekTo) {
        directlyOn(player, MediaPlayer.class).seekTo(seekTo);
    }

    @Implementation
    public void setAudioSessionId(int sessionId) {
        directlyOn(player, MediaPlayer.class).setAudioSessionId(sessionId);
    }

    @Implementation
    public void setAudioStreamType(int audioStreamType) {
        directlyOn(player, MediaPlayer.class).setAudioSessionId(audioStreamType);
    }
}
