package com.ibm.transhlelper;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.transhlelper.uitls.AudioRecorder;
import com.ibm.transhlelper.uitls.ExtAudioRecorder;
import com.ibm.transhlelper.uitls.TranslationHelper;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ChatActivity extends Activity {

    private TextView txtPrimary;
    private TextView txTranslate;
    private ImageButton imgBtnTxtLeft;
    private ImageButton imgBtnTxtRight;
    private LinearLayout linLayoutChat;
    private EditText editTxt;
    private ImageButton imgBtnRecorderLeft;
    private ImageButton imgBtnRecorderRight;

    private MediaPlayer mMediaPlayer;
    public  String sourceTag;
    public  String targetTag;

//    private TextView mTvRecordTxt;
//    private TextView mTvRecordPath;
    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecVolume;


    private Thread mRecordThread;
    private Dialog mRecordDialog;

    private ExtAudioRecorder mExtAudioRecorder;//mav格式的文件

    private AudioRecorder mAudioRecorder;
    // private static final int MAX_RECORD_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制

    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音
    private static final String RECORD_FILENAME = UUID.randomUUID().toString(); // 录音文件名
    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长
    private double voiceValue = 0.0; // 录音的音量值
    private boolean playState = false; // 录音的播放状态
    private boolean moveState = false; // 手指是否移动
    private float downY;
    private boolean playPreState = false;
    private Handler handler = null;

    private String  ttsStr= "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_chat);
        initView();
        loadData();
        setListener();
    }



    private void initView() {

        linLayoutChat =(LinearLayout) findViewById(R.id.linlayoutchat);
        txtPrimary =(TextView)findViewById(R.id.txtprimary);
        txTranslate= (TextView)findViewById(R.id.txttranslate);
        imgBtnTxtLeft = (ImageButton)findViewById(R.id.imgbtntxtleft);
        imgBtnTxtRight = (ImageButton)findViewById(R.id.imgbtntxtright);
        editTxt =(EditText)findViewById(R.id.edittxt);
        imgBtnRecorderLeft = (ImageButton)findViewById(R.id.imgbtnrecorderleft);
        imgBtnRecorderRight = (ImageButton)findViewById(R.id.imgbtnrecorderright);

    }
    private void loadData() {
        Intent intent = getIntent();
        sourceTag = intent.getStringExtra("source");
        targetTag = intent.getStringExtra("target");
        Toast.makeText(getApplicationContext(),"接收到的"+sourceTag+"    "+targetTag,Toast.LENGTH_SHORT).show();

    }

    private void setListener() {
        imgBtnTxtLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentStr = editTxt.getText().toString().trim();
               if(contentStr == null || contentStr.length() <= 0){
                   Toast.makeText(getApplicationContext(),"请输入你要翻译的内容!",Toast.LENGTH_LONG).show();
               }else{
                   txtPrimary.setText(contentStr);
                   //将txtPrimary中的语言从sourceTag (先设置为英语)转化为targetTag语言,是上一个界面传过来的参数
                   txTranslate.setText(TranslationHelper.translate(contentStr, sourceTag, targetTag));//改功能没有问题
                   //txTranslate.setText(translate(contentStr,"en","fr"));
               }

            }
        });
        //改 功能应该再使用了
        imgBtnTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resources resources = getContext().getResources();
                Resources resources = getApplicationContext().getResources();
                Drawable layoutBackDrawable = resources.getDrawable(R.drawable.conversation_bubble_right_bg);
                linLayoutChat.setBackgroundDrawable(layoutBackDrawable);
            }
        });

//        //读取英文,变成语音,并播放出来的
//        txtPrimary.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//           public void onClick(View v) {
//                File videoFile = TranslationHelper.textToSpeech("hello", RECORD_FILENAME);
//                if(!playState){
//                    mMediaPlayer = new MediaPlayer();
//                    Toast.makeText(getApplicationContext(),videoFile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();
//                    try {
//                        mMediaPlayer.setDataSource(getAmrPath());
//                        mMediaPlayer.prepare();
//                        playState =true;
//                        mMediaPlayer.start();
//                        //设置播放结束监听
//                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                if(playState){
//                                    playState = false;
//                                }
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }else{
//                    if(mMediaPlayer.isPlaying()){
//                        mMediaPlayer.stop();
//                        playState = false;
//                    }else{
//                        playState = false;
//                    }
//                }
//
//          }
//        });
        //点击文字和文字后面的图片读出录音的发音
        txTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playState){
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(getAmrPath());
                        mMediaPlayer.prepare();
                        playState =true;
                        mMediaPlayer.start();
                        //设置播放结束监听
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (playState) {
                                    playState = false;
                                }
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message m = new Message();
                                m.what = 0;
                                handler.sendMessage(m);

                               ttsStr = TranslationHelper.speechToText(getAmrPath());

                            }
                        }).start();
                        handler = new Handler(){
                            public void handleMessage(Message msg){
                                super.handleMessage(msg);
                                if(msg.what == 0){
                                    Toast.makeText(getApplication(),"ttsStr:"+ttsStr,Toast.LENGTH_LONG).show();
                                    txtPrimary.setText(ttsStr);
                                }
                            }
                        };




                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                        playState = false;
                    }else{
                        playState = false;
                    }
                }

            }
        });

        //右边话筒 的按钮进行录音操作格式wav格式的
        imgBtnRecorderRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下按钮
                        if (recordState != RECORD_ON) {
                            downY = event.getY();
                            //deleteOldFile();
                            recordState = RECORD_ON;
                            mExtAudioRecorder = ExtAudioRecorder.getInstanse(false);

                            try {
                                showVoiceDialog(0);
                                File fileExt=  mExtAudioRecorder.recordChat(Environment.getExternalStorageDirectory() + "/voiceRecord/", "a.wav");
                                Toast.makeText(getApplicationContext(),fileExt.getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 滑动手指
                        float moveY = event.getY();
                        if (moveY - downY > 50) {
                            moveState = true;
                            showVoiceDialog(1);
                        }
                        if (moveY - downY < 20) {
                            moveState = false;
                            showVoiceDialog(0);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: // 松开手指
                        if (recordState == RECORD_ON) {
                            recordState = RECORD_OFF;
                            if (mRecordDialog.isShowing()) {
                                mRecordDialog.dismiss();
                            }
                            // mExtAudioRecorder.stop();
                            // mRecordThread.interrupt();
                            mExtAudioRecorder.stopRecord();
                            voiceValue = 0.0;
                            //将语音转化为文字
                           // Toast.makeText(getApplication(), getAmrPath(), Toast.LENGTH_LONG).show();
                               String  ttsStr = TranslationHelper.speechToText(getAmrPath());
                            Toast.makeText(getApplication(),"ttsStr::::"+ttsStr,Toast.LENGTH_LONG).show();
                              //txtPrimary.setText(ttsStr);

                            if (!moveState) {
                                if (recodeTime < MIN_RECORD_TIME) {
                                    showWarnToast("时间太短  录音失败");
                                } else {
//                                    mTvRecordTxt.setText("录音时间："
//                                            + ((int) recodeTime));
//                                    mTvRecordPath.setText("文件路径：" + getAmrPath());
                                }
                            }
                            moveState = false;
                        }
                        break;
                }
                return false;
            }
        });
        //长按按钮显示录音的效果,这个后买要使用的
        imgBtnRecorderLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下按钮
                        if (recordState != RECORD_ON) {
                            downY = event.getY();
                            //deleteOldFile();
                            mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
                            recordState = RECORD_ON;
                            try {
                                mAudioRecorder.start();
                                recordTimethread();
                                showVoiceDialog(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 滑动手指
                        float moveY = event.getY();
                        if (moveY - downY > 50) {
                            moveState = true;
                            showVoiceDialog(1);
                        }
                        if (moveY - downY < 20) {
                            moveState = false;
                            showVoiceDialog(0);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: // 松开手指
                        if (recordState == RECORD_ON) {
                            recordState = RECORD_OFF;
                            if (mRecordDialog.isShowing()) {
                                mRecordDialog.dismiss();
                            }
                            try {
                                mAudioRecorder.stop();
                                mRecordThread.interrupt();
                                voiceValue = 0.0;
                                //将语音转化为文字
                                Toast.makeText(getApplication(),getAmrPath(),Toast.LENGTH_LONG).show();
//                               String  ttsStr = TranslationHelper.speechToText(getAmrPath());
//                               txtPrimary.setText(ttsStr);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (!moveState) {
                                if (recodeTime < MIN_RECORD_TIME) {
                                    showWarnToast("时间太短  录音失败");
                                } else {
//                                    mTvRecordTxt.setText("录音时间："
//                                            + ((int) recodeTime));
//                                    mTvRecordPath.setText("文件路径：" + getAmrPath());
                                }
                            }
                            moveState = false;
                        }
                        break;
                }
                return false;
            }
        });
    }



    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }


    // 录音时显示Dialog
    void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(ChatActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.mipmap.record_cancel);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;

            default:
                mIvRecVolume.setImageResource(R.mipmap.record_animate_01);
                mTvRecordDialogTxt.setText("向下滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    // 录音时间太短时Toast显示
    void showWarnToast(String toastText) {
        Toast toast = new Toast(ChatActivity.this);
        LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(ChatActivity.this);
        imageView.setImageResource(R.mipmap.voice_to_short); // 图标

        TextView mTv = new TextView(ChatActivity.this);
        mTv.setText(toastText);
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.mipmap.record_bg);// 设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.show();
    }
    // 获取文件手机路径
    private String getAmrPath() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "/voiceRecord/" + RECORD_FILENAME + ".amr");
        return file.getAbsolutePath();
    }

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        if (voiceValue < 600.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_01);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_02);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_03);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_04);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_05);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_06);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_07);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_08);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_09);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_10);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_11);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_12);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_13);
        } else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.mipmap.record_animate_14);
        }
    }

    // 录音线程
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // 限制录音时长
                // if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                // imgHandle.sendEmptyMessage(0);
                // } else
                {
                    try {
                        Thread.sleep(150);
                        recodeTime += 0.15;
                        // 获取音量，更新dialog
                        if (!moveState) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setDialogImage();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
