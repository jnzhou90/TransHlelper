package com.ibm.transhlelper.uitls;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 录音工具类
 * Created by 江南 on 2015/7/21.
 */
public class AudioRecorder {
    private static int SAMPLE_RATE_IN_HZ = 8000; // 采样率

    private MediaRecorder mMediaRecorder;
    private String mPath;//录音路径

    public AudioRecorder(String path) {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
           // mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
           // mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        }
        this.mPath = sanitizePath(path);
    }

    private String sanitizePath(String path) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/voiceRecord/" + path + ".amr";
    }

    /**
     * 开始录音
     * @throws IOException
     */

    public void start() throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted,It is  " + state
                    + ".");
        }
        File directory = new File(mPath).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created");
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // recorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
        mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        mMediaRecorder.setOutputFile(mPath);
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    /**
     * 结束录音
     * @throws IOException
     */
    public void stop() throws IOException {
        mMediaRecorder.stop();
        mMediaRecorder.release();
    }

    /**
     * 获取录音的秒数
     * @return
     */
    public double getAmplitude() {
        if (mMediaRecorder != null) {
            return (mMediaRecorder.getMaxAmplitude());
        } else
            return 0;
    }
}
