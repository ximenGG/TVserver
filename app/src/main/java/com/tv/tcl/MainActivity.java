package com.tv.tcl;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * 文 件 名: MainActivity
 * 创 建 人: 何庆
 * 创建日期: 2018/12/30 23:20
 * 修改备注：
 */

public class MainActivity extends BaseActivity {
    String[] mode = {Constants.RESOURCE + R.raw.mode_1, Constants.RESOURCE + R.raw.mode_2, Constants.RESOURCE + R.raw.mode_3, Constants.RESOURCE + R.raw.mode_4, Constants.RESOURCE + R.raw.mode_5, Constants.RESOURCE + R.raw.mode_6};
    private VideoView videoView;
    private TextView textView;
    private int mSeekPosition = 0;
    private int position = 0;
    private TcpListener listener = new TcpListener() {
        @Override
        public void onClientJoin(String ip) {
            debug("客服端IP:" + ip + " connect");
        }

        @Override
        public void onClientMsg(Server.Client client, String msg) {
            debug("客服端消息:" + msg);
            switch (msg) {
                case Constants.MODE_1:
                    debug("切换到模式一");
                    playMode(0);
                    break;
                case Constants.MODE_2:
                    debug("切换到模式二");
                    playMode(1);
                    break;
                case Constants.MODE_3:
                    debug("切换到模式三");
                    playMode(2);
                    break;
                case Constants.MODE_4:
                    debug("切换到模式四");
                    playMode(3);
                    break;
                case Constants.MODE_5:
                    debug("切换到模式五");
                    playMode(4);
                    break;
                case Constants.MODE_6:
                    debug("切换到模式六");
                    playMode(5);
                    break;
                case Constants.MODE_7:
                    toast("关闭调试");
                    runOnUiThread(() -> {
                        if (textView != null) {
                            textView.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.MODE_8:
                    runOnUiThread(() -> {
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("");
                        }
                    });
                    debug("打开调试");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClientQuit(String ip) {
            debug("客服端IP: " + ip + " quit");
        }
    };

    // 当MediaPlayer准备好后触发该回调
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (!mediaPlayer.isPlaying()) {
                if (mSeekPosition != 0) {
                    mediaPlayer.seekTo(mSeekPosition);
                    mediaPlayer.start();
                    mediaPlayer.pause();
                    return;
                }
                mediaPlayer.start();
                //去把你的 加载中的loading界面去掉
            } else
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            if (mSeekPosition != 0) {
                                mp.seekTo(mSeekPosition);
                                mp.start();
                                mp.pause();
                                return true;
                            }
                            //去把你的 加载中的loading界面去掉
                            return true;
                        }
                        return false;
                    }
                });

        }
    };
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (position <= 6) {
                position = 0;
            } else {
                position++;
            }
            playMode(position);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTcpLisener(listener);
        videoView = findViewById(R.id.videoview1);
        textView = findViewById(R.id.tv_debug);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        videoView.setOnPreparedListener(mOnPreparedListener);
        videoView.setOnCompletionListener(completionListener);
        debug("切换到模式一");
        playMode(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);//正常退出App
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void playMode(int i) {
        position = i;
        AppExecutors.getInstance().mainThread().execute(() -> {
            try {
                if (videoView != null) {
                    videoView.setVideoURI(Uri.parse(mode[i]));
                    videoView.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void debug(String str) {
        AppExecutors.getInstance().mainThread().execute(() -> {
            if (textView != null && textView.getVisibility() == View.VISIBLE) {
                textView.append(str + "\n");
            }
        });
    }

}
