package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View.MeasureSpec;
import com.android.setupwizardlib.R;

@TargetApi(14)
public class IllustrationVideoView extends TextureView implements Animatable, SurfaceTextureListener, OnPreparedListener, OnSeekCompleteListener, OnInfoListener {
    private static final String TAG = "IllustrationVideoView";
    protected float mAspectRatio = 1.0f;
    @Nullable
    protected MediaPlayer mMediaPlayer;
    @VisibleForTesting
    Surface mSurface;
    @RawRes
    private int mVideoResId = 0;

    public IllustrationVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwIllustrationVideoView);
        this.mVideoResId = obtainStyledAttributes.getResourceId(R.styleable.SuwIllustrationVideoView_suwVideo, 0);
        obtainStyledAttributes.recycle();
        setScaleX(0.9999999f);
        setScaleX(0.9999999f);
        setSurfaceTextureListener(this);
    }

    private void createMediaPlayer() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
        }
        if (this.mSurface != null && this.mVideoResId != 0) {
            this.mMediaPlayer = MediaPlayer.create(getContext(), this.mVideoResId);
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.setSurface(this.mSurface);
                this.mMediaPlayer.setOnPreparedListener(this);
                this.mMediaPlayer.setOnSeekCompleteListener(this);
                this.mMediaPlayer.setOnInfoListener(this);
                float videoHeight = ((float) this.mMediaPlayer.getVideoHeight()) / ((float) this.mMediaPlayer.getVideoWidth());
                if (this.mAspectRatio != videoHeight) {
                    this.mAspectRatio = videoHeight;
                    requestLayout();
                }
            } else {
                Log.wtf(TAG, "Unable to initialize media player for video view");
            }
            if (getWindowVisibility() == 0) {
                start();
            }
        }
    }

    public int getCurrentPosition() {
        return this.mMediaPlayer == null ? 0 : this.mMediaPlayer.getCurrentPosition();
    }

    public boolean isRunning() {
        return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        if (i == 3) {
            setVisibility(0);
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        if (((float) size2) < ((float) size) * this.mAspectRatio) {
            size = (int) (((float) size2) / this.mAspectRatio);
        } else {
            size2 = (int) (((float) size) * this.mAspectRatio);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, 1073741824), MeasureSpec.makeMeasureSpec(size2, 1073741824));
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(shouldLoop());
    }

    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        setVisibility(4);
        this.mSurface = new Surface(surfaceTexture);
        createMediaPlayer();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            start();
        } else {
            stop();
        }
    }

    public void release() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }
    }

    public void setVideoResource(@RawRes int i) {
        if (i != this.mVideoResId) {
            this.mVideoResId = i;
            createMediaPlayer();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean shouldLoop() {
        return true;
    }

    public void start() {
        if (this.mMediaPlayer != null && !this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.start();
        }
    }

    public void stop() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.pause();
        }
    }
}
