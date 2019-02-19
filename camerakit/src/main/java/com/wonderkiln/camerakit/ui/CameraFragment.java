package com.wonderkiln.camerakit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.core.R;
import com.wonderkiln.camerakit.receiver.PhoneCallStateReceiver;
import com.wonderkiln.camerakit.utils.DateUtil;
import com.wonderkiln.camerakit.utils.DensityUtil;
import com.wonderkiln.camerakit.utils.FileOperateUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFragment extends BaseFragment{

    public static String TAG = "CameraFragment";
    public static final int CAMERA_TYPE_IMAGE = 0;
    public static final int CAMERA_TYPE_VIDEO = 1;
    CameraView cameraView;
    FrameLayout mOvrtlay;
    TextView hideArctExit;
    TextView mCameraShutterButton;
    TextView mRecordShutterButton;
    TextView mVideoTime;
    ImageView mFlashView;
    ImageView mSwitchCameraView;//使用前置或后置摄像头
    TextView mOpenHideCamera;
    Button mOpenHideCameraNotice;
    View mHeaderBar;//相机底下布局
    TextView tvMyCameraHint;
    FrameLayout mOvrtlayNotice;
    TextView cameraHideHelpText;
    TextView tvIKnow;
    Button exitCamera;
    private int cameraType;
    //是否是在暗拍模式下
    private boolean isHiddenCamera;
    private boolean mCapturingPicture;
    private boolean mCapturingVideo;
    private long mRecordStartTime;
    private String resultDir;//照片存储路径
    private String videoQuality = "2";//视频质量
    private int videoDuration;//视频要求时长
    /**  录像存放路径 ，用以生成缩略图*/
    private String mRecordPath;
    private File mVideoFile = null;
    /** 用以执行定时任务的Handler对象*/
    private Handler mHandler;
    private SimpleDateFormat mTimeFormat;
    private int phoneCallingState;//录制视频过程来电情况，0没来电 1响铃 2挂断 3接听
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> thumbnailList = new ArrayList<>();
    private boolean ifRingToCaptureVideo;//响铃后无视又开始拍摄
    private CameraKitResultListener cameraKitResultListener;
    private CameraKitHideCaptureListener cameraKitHideCaptureListener;
    private CameraKitHideCaptureNoticeListener cameraKitHideCaptureNoticeListener;

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public boolean isHiddenCamera() {
        return isHiddenCamera;
    }

    public void setHiddenCamera(boolean hiddenCamera) {
        isHiddenCamera = hiddenCamera;
    }

    public boolean isCapturingVideo() {
        return mCapturingVideo;
    }

    public void setIsCapturingVideo(boolean mCapturingVideo) {
        this.mCapturingVideo = mCapturingVideo;
    }

    public void setCameraKitResultListener(CameraKitResultListener cameraKitResultListener) {
        this.cameraKitResultListener = cameraKitResultListener;
    }

    public void setCameraKitHideCaptureListener(CameraKitHideCaptureListener cameraKitHideCaptureListener) {
        this.cameraKitHideCaptureListener = cameraKitHideCaptureListener;
    }

    public void setCameraKitHideCaptureNoticeListener(CameraKitHideCaptureNoticeListener cameraKitHideCaptureNoticeListener) {
        this.cameraKitHideCaptureNoticeListener = cameraKitHideCaptureNoticeListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_layout, container, false);
        cameraView = (CameraView) view.findViewById(R.id.camera);
        mOvrtlay = (FrameLayout) view.findViewById(R.id.hide_frame_overlay);
        hideArctExit = (TextView) view.findViewById(R.id.hide_arct_exit);
        mCameraShutterButton = (TextView) view.findViewById(R.id.btn_shutter_camera);
        mRecordShutterButton = (TextView) view.findViewById(R.id.btn_shutter_video);
        mVideoTime = (TextView) view.findViewById(R.id.video_time);
        mFlashView = (ImageView) view.findViewById(R.id.btn_flash_mode);
        mSwitchCameraView = (ImageView) view.findViewById(R.id.btn_switch_camera);
        mOpenHideCamera = (TextView) view.findViewById(R.id.bt_hide_camera_open);
        mOpenHideCameraNotice = (Button) view.findViewById(R.id.bt_hide_camera_notice);
        mHeaderBar = view.findViewById(R.id.camera_header_bar);
        tvMyCameraHint = (TextView) view.findViewById(R.id.tv_mycamera_hint);
        mOvrtlayNotice = (FrameLayout) view.findViewById(R.id.frame_notice);
        cameraHideHelpText = (TextView) view.findViewById(R.id.cameraHideHelpText);
        tvIKnow = (TextView) view.findViewById(R.id.bt_iknow);
        exitCamera = (Button) view.findViewById(R.id.bt_exit_camera);
        mCameraShutterButton.setOnClickListener(onClickListener);
        mRecordShutterButton.setOnClickListener(onClickListener);
        mFlashView.setOnClickListener(onClickListener);
        mSwitchCameraView.setOnClickListener(onClickListener);
        mOpenHideCamera.setOnClickListener(onClickListener);
        mOpenHideCameraNotice.setOnClickListener(onClickListener);
        mOvrtlayNotice.setOnClickListener(onClickListener);
        exitCamera.setOnClickListener(onClickListener);
        hideArctExit.setOnClickListener(onClickListener);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(cameraType == CAMERA_TYPE_VIDEO) {//拍视频
            getActivity().unregisterReceiver(phoneCallStateReceiver);
        }
        super.onDestroy();
    }

    private void initView(){
        if(cameraType == CAMERA_TYPE_VIDEO){
            mHeaderBar.setVisibility(View.GONE);//隐藏底部菜单
            mCameraShutterButton.setVisibility(View.GONE);
            mRecordShutterButton.setVisibility(View.VISIBLE);
            if(videoQuality.equals("1")){
                cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_LOWEST);
            }else if(videoQuality.equals("3")){
                cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_480P);
            }else {
                cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_QVGA);
            }
            createVideoFile();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PhoneCallStateReceiver.CALL_STATE_RING);
            intentFilter.addAction(PhoneCallStateReceiver.CALL_STATE_DISCONNECT);
            intentFilter.addAction(PhoneCallStateReceiver.CALL_STATE_ANSWER);
            getActivity().registerReceiver(phoneCallStateReceiver,intentFilter);
            cameraHideHelpText.setText(R.string.camera_hide_help_text_video);
        }else {
            tvMyCameraHint.setVisibility(View.VISIBLE);
            mCameraShutterButton.setVisibility(View.VISIBLE);
            mRecordShutterButton.setVisibility(View.GONE);
            cameraHideHelpText.setText(R.string.camera_hide_help_text_photo);
        }
        //没有前置摄像头,就隐藏控件
        Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                mSwitchCameraView.setVisibility(View.VISIBLE);
            } else {
                mSwitchCameraView.setVisibility(View.GONE);
            }
        }
        mHandler=new Handler();
        mTimeFormat=new SimpleDateFormat("mm:ss", Locale.getDefault());

    }


    public void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                onPicture(cameraKitImage.getJpeg());
            }
        });

    }

    private void createVideoFile(){
        if(resultDir != null) {
            String path = FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_VIDEO, resultDir);

            File directory = new File(path);
            if (!directory.exists())
                directory.mkdirs();
            try {
                String name = "video" + FileOperateUtil.createFileNmae(".mp4");
                mRecordPath = path + File.separator + name;
                mVideoFile = new File(mRecordPath);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }else {
            Log.e(TAG, "------resultDir null!");
        }
    }
    
    private void captureVideo() {
        if(phoneCallingState == 0) {

            if (videoDuration != 0) {
                showHintDialog(new DialogDefineClick() {
                    @Override
                    public void defineClick() {
                        cameraView.captureVideo(mVideoFile);
                        mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
                        mCapturingVideo = true;
                        mRecordStartTime = SystemClock.uptimeMillis();
                        mVideoTime.setVisibility(View.VISIBLE);
                        mVideoTime.setText("00:00");
                        mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
                    }
                }, getActivity().getString(R.string.text_slicejobs_hint), "这个视频至少需要录制" + DateUtil.castMinut(videoDuration), "我知道了", false);
            } else {
                cameraView.captureVideo(mVideoFile);
                mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
                mCapturingVideo = true;
                mRecordStartTime = SystemClock.uptimeMillis();
                mVideoTime.setVisibility(View.VISIBLE);
                mVideoTime.setText("00:00");
                mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
            }
        }else if(phoneCallingState == 1){
            ifRingToCaptureVideo = true;
            if (videoDuration != 0) {
                if (videoList != null && videoList.size() != 0) {
                    int allDuration = 0;
                    for (int i = 0; i < videoList.size(); i++) {
                        String tampVideoPath = videoList.get(i);
                        MediaPlayer player = new MediaPlayer();
                        try {
                            player.setDataSource(tampVideoPath);
                            player.prepare();
                            int duration = player.getDuration() / 1000;//获取音频视频时长，单位毫秒
                            allDuration += duration;
                            player.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showHintDialog(new DialogDefineClick() {
                        @Override
                        public void defineClick() {
                            createVideoFile();//重新创建视频文件
                            cameraView.captureVideo(mVideoFile);
                            mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
                            mCapturingVideo = true;
                            mRecordStartTime = SystemClock.uptimeMillis();
                            mVideoTime.setVisibility(View.VISIBLE);
                            mVideoTime.setText("00:00");
                            mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
                        }
                    }, getActivity().getString(R.string.text_slicejobs_hint), "这个视频至少需要录制" + DateUtil.castMinut(videoDuration - allDuration), "我知道了", false);
                }else {
                    showHintDialog(new DialogDefineClick() {
                        @Override
                        public void defineClick() {
                            createVideoFile();//重新创建视频文件
                            cameraView.captureVideo(mVideoFile);
                            mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
                            mCapturingVideo = true;
                            mRecordStartTime = SystemClock.uptimeMillis();
                            mVideoTime.setVisibility(View.VISIBLE);
                            mVideoTime.setText("00:00");
                            mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
                        }
                    }, getActivity().getString(R.string.text_slicejobs_hint), "这个视频至少需要录制" + DateUtil.castMinut(videoDuration), "我知道了", false);
                }
            } else {
                createVideoFile();//重新创建视频文件
                cameraView.captureVideo(mVideoFile);
                mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
                mCapturingVideo = true;
                mRecordStartTime = SystemClock.uptimeMillis();
                mVideoTime.setVisibility(View.VISIBLE);
                mVideoTime.setText("00:00");
                mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
            }
        }else {
            createVideoFile();//重新创建视频文件
            cameraView.captureVideo(mVideoFile);
            mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_recording);
            mCapturingVideo = true;
            mRecordStartTime = SystemClock.uptimeMillis();
            mVideoTime.setVisibility(View.VISIBLE);
            mVideoTime.setText("00:00");
            mHandler.postAtTime(recordRunnable, mVideoTime, SystemClock.uptimeMillis() + 1000);
        }
    }

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        if(cameraKitResultListener != null){
            cameraKitResultListener.onGetPicture(jpeg);
        }
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        cameraView.toggleFacing();
    }

    private void clickStopRecord() {
        if(phoneCallingState == 0) {
            if (videoDuration != 0) {
                //发送事件
                long recordTime = SystemClock.uptimeMillis() - mRecordStartTime;
                long recordTimeS = recordTime / 1000;

                if (videoDuration + 2 > recordTimeS) {//没到时间
                    showHintDialog(new DialogDefineClick() {
                        @Override
                        public void defineClick() {

                        }
                    }, getActivity().getString(R.string.text_slicejobs_hint), "你还差" + DateUtil.castMinut(videoDuration + 2 - recordTimeS) + "才能结束哦", "我知道了", false);
                } else {
                    cameraView.stopVideo();//停止录像
                    onGetVideo();
                }
            } else {
                cameraView.stopVideo();//停止录像
                onGetVideo();
            }
        }else {
            if (videoDuration != 0) {
                //计算这些分段视频总时长是否符合要求
                if (videoList != null && videoList.size() != 0) {
                    int allDuration = 0;
                    //发送事件
                    long recordTime = SystemClock.uptimeMillis() - mRecordStartTime;
                    long recordTimeS = recordTime / 1000;
                    for (int i = 0; i < videoList.size(); i++) {
                        String tampVideoPath = videoList.get(i);
                        MediaPlayer player = new MediaPlayer();
                        try {
                            player.setDataSource(tampVideoPath);
                            player.prepare();
                            int duration = player.getDuration() / 1000;//获取音频视频时长，单位毫秒
                            allDuration += duration;
                            player.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (allDuration + recordTimeS >= videoDuration + 2) {
                        cameraView.stopVideo();//停止录像
                        onGetVideo();
                    } else {
                        showHintDialog(new DialogDefineClick() {
                            @Override
                            public void defineClick() {

                            }
                        }, getActivity().getString(R.string.text_slicejobs_hint), "你还差" + DateUtil.castMinut(videoDuration + 2 - allDuration - recordTimeS) + "才能结束哦", "我知道了", false);
                    }
                } else {
                    long recordTime = SystemClock.uptimeMillis() - mRecordStartTime;
                    long recordTimeS = recordTime / 1000;
                    if (videoDuration + 2 > recordTimeS) {//没到时间
                        showHintDialog(new DialogDefineClick() {
                            @Override
                            public void defineClick() {

                            }
                        }, getActivity().getString(R.string.text_slicejobs_hint), "你还差" + DateUtil.castMinut(videoDuration + 2 - recordTimeS) + "才能结束哦", "我知道了", false);
                    } else {
                        cameraView.stopVideo();//停止录像
                        onGetVideo();
                    }
                }
            }else {
                cameraView.stopVideo();//停止录像
                onGetVideo();
            }
        }
    }

    Runnable recordRunnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(mCapturingVideo){
                long recordTime=SystemClock.uptimeMillis()-mRecordStartTime;
                mVideoTime.setText(mTimeFormat.format(new Date(recordTime)));
                mHandler.postAtTime(this,mVideoTime, SystemClock.uptimeMillis()+1000);
            }else {
                mVideoTime.setVisibility(View.GONE);
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_shutter_camera) {
                if (!isHiddenCamera) {
                    capturePhoto();
                }

            } else if (view.getId() == R.id.btn_shutter_video) {
                if (!mCapturingVideo) {//是否正在录像中
                    captureVideo();
                } else {
                    clickStopRecord();
                }

            } else if (view.getId() == R.id.btn_flash_mode) {
                if (cameraView.getFlash() == CameraKit.Constants.FLASH_ON) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    mFlashView.setImageResource(R.drawable.btn_flash_off);
                } else if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
                    mFlashView.setImageResource(R.drawable.btn_flash_auto);
                } else if (cameraView.getFlash() == CameraKit.Constants.FLASH_AUTO) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
                    mFlashView.setImageResource(R.drawable.btn_flash_torch);
                } else if (cameraView.getFlash() == CameraKit.Constants.FLASH_TORCH) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    mFlashView.setImageResource(R.drawable.btn_flash_on);
                }

            } else if (view.getId() == R.id.btn_switch_camera) {
                toggleCamera();

            } else if (view.getId() == R.id.bt_hide_camera_open) {
                startHideCapture();
            } else if (view.getId() == R.id.bt_exit_camera) {
                exitPage();

            } else if (view.getId() == R.id.bt_hide_camera_notice) {
                mOvrtlayNotice.setVisibility(View.VISIBLE);
                if(cameraKitHideCaptureNoticeListener != null){
                    cameraKitHideCaptureNoticeListener.onHideCaptureNoticeOpen();
                }
            } else if (view.getId() == R.id.frame_notice) {
                mOvrtlayNotice.setVisibility(View.GONE);
                if(cameraKitHideCaptureNoticeListener != null){
                    cameraKitHideCaptureNoticeListener.onHideCaptureNoticeClose();
                }
            } else if (view.getId() == R.id.hide_arct_exit) {
                exitHideCapture();
            }
        }
    };

    private void onGetVideo(){
        mCapturingVideo = false;
        mRecordShutterButton.setBackgroundResource(R.drawable.ic_video_start);
        mVideoTime.setVisibility(View.GONE);
        if(mRecordPath!=null){
            //创建缩略图,该方法只能获取384X512的缩略图，舍弃，使用源码中的获取缩略图方法
            //			Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(mRecordPath, Thumbnails.MINI_KIND);
            Bitmap bitmap=getVideoThumbnail(mRecordPath);

            if(bitmap!=null){
                String mThumbnailFolder=FileOperateUtil.getFolderPath(getActivity(),  FileOperateUtil.TYPE_THUMBNAIL,resultDir);
                File folder=new File(mThumbnailFolder);
                if(!folder.exists()){
                    folder.mkdirs();
                }
                File file=new File(mRecordPath);
                file=new File(folder+File.separator+file.getName().replace("mp4", "jpg"));
                //存图片小图
                BufferedOutputStream bufferos= null;
                try {
                    bufferos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferos);
                    bufferos.flush();
                    bufferos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String currThumbnailPath = file.getAbsolutePath();
                if(phoneCallingState == 0) {//录制视频中没有来电情况
                    if(cameraKitResultListener != null){
                        cameraKitResultListener.onGetVideo(mRecordPath,currThumbnailPath);
                    }
                }else if(phoneCallingState == 1){//录制过程中响铃，保存视频
                    videoList.add(mRecordPath);
                    thumbnailList.add(currThumbnailPath);
                    if(ifRingToCaptureVideo){
                        if(cameraKitResultListener != null){
                            cameraKitResultListener.onGetVideoList(videoList,thumbnailList);
                        }
                    }
                }else if(phoneCallingState == 2){//录制过程中响铃挂断后又开始录制的
                    videoList.add(mRecordPath);
                    thumbnailList.add(currThumbnailPath);
                    if(ifRingToCaptureVideo){
                        if(cameraKitResultListener != null){
                            cameraKitResultListener.onGetVideoList(videoList,thumbnailList);
                        }
                    }
                }
            }
        }
    }

    BroadcastReceiver phoneCallStateReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PhoneCallStateReceiver.CALL_STATE_ANSWER)){
                phoneCallingState = 3;
            }else if(intent.getAction().equals(PhoneCallStateReceiver.CALL_STATE_DISCONNECT)){
                if(phoneCallingState != 2) {
                    phoneCallingState = 2;
                    showHintDialog(new DialogClickLinear() {
                        @Override
                        public void cancelClick() {
                            handler.sendEmptyMessage(10000);
                        }

                        @Override
                        public void defineClick() {
                            cameraView.start();
                            captureVideo();
                        }
                    }, getActivity().getString(R.string.text_slicejobs_hint), "是否继续拍摄？如果取消可能会丢失已拍摄的数据哦", "取消", "继续", false);
                }
            }else if(intent.getAction().equals(PhoneCallStateReceiver.CALL_STATE_RING)){//录音过程中来电
                if(phoneCallingState != 1) {
                    phoneCallingState = 1;
                    cameraView.stopVideo();//停止录像
                    onGetVideo();
                }
            }
        }
    };

    /**
     *  获取帧缩略图，根据容器的高宽进行缩放
     *  @param filePath
     *  @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if(bitmap==null)
            return null;
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pWidth=cameraView.getWidth();// 容器宽度
        int pHeight=cameraView.getHeight();//容器高度
        //获取宽高跟容器宽高相比较小的倍数，以此为标准进行缩放
        float scale = Math.min((float)width/pWidth, (float)height/pHeight);
        int w = Math.round(scale * pWidth);
        int h = Math.round(scale * pHeight);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 10000){
                if(cameraKitResultListener != null){
                    cameraKitResultListener.onGetVideo(null,null);
                }
            }else if(msg.what == 10001){
                captureVideo();
            }
        }
    };

    /*
    * 页面退出方法，供Activity调用
    * */
    public void exitPage(){
        if(!mCapturingVideo){//未开始录制
            if(cameraKitResultListener != null){
                cameraKitResultListener.onCameraExit();
            }
        }else {
            if (videoDuration != 0) {
                //发送事件
                long recordTime = SystemClock.uptimeMillis() - mRecordStartTime;
                long recordTimeS = recordTime / 1000;

                if (videoDuration + 2 > recordTimeS) {//没到时间
                    showHintDialog(new DialogClickLinear() {
                        @Override
                        public void cancelClick() {

                        }

                        @Override
                        public void defineClick() {
                            cameraView.stop();
                            if(cameraKitResultListener != null){
                                cameraKitResultListener.onCameraExit();
                            }
                        }
                    }, getActivity().getString(R.string.text_slicejobs_hint), "录制时长不满足要求，退出不会保存，确定要退出？", "取消", "确定", false);
                } else {
                    cameraView.stopVideo();//停止录像
                    onGetVideo();
                }
            } else {
                cameraView.stopVideo();//停止录像
                onGetVideo();
            }
        }
    }

    /*
    * 开始暗拍
    * */
    public void startHideCapture(){
        isHiddenCamera = true;
        mOvrtlay.setVisibility(View.VISIBLE);
        if(cameraKitHideCaptureListener != null){
            cameraKitHideCaptureListener.onHideCaptureStart();
        }
    }

    /*
     * 退出暗拍
     * */
    public void exitHideCapture(){
        mOvrtlay.setVisibility(View.GONE);
        isHiddenCamera = false;
        if(cameraKitHideCaptureListener != null){
            cameraKitHideCaptureListener.onHideCaptureExit();
        }
    }

    public interface CameraKitResultListener{
        void onGetPicture(byte[] jpeg);
        void onGetVideo(String mVideoPath,String thumbnailPath);
        void onGetVideoList(ArrayList mVideoPath, ArrayList thumbnailPath);
        void onCameraExit();
    }

    public interface CameraKitHideCaptureListener{
        void onHideCaptureStart();
        void onHideCaptureExit();
    }

    public interface CameraKitHideCaptureNoticeListener{
        void onHideCaptureNoticeOpen();
        void onHideCaptureNoticeClose();
    }
}
