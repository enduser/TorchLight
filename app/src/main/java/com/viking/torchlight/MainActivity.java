package com.viking.torchlight;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TorchLight" ;

    private ImageView mLedbg;
    private static final int LEDOFF = 0;
    private static final int LEDON = 1;
    private int mLedState = LEDOFF;
    private boolean mLedOnOff = false;
    private PowerManager.WakeLock mWakeLock = null;

    private Camera camera;
    Camera.Parameters params;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLedbg = (ImageView) findViewById(R.id.led_bg);
        mLedbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLedOnOffButton();
            }
        });

        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCamera();
        if(!mLedOnOff){
            if(!mWakeLock.isHeld()){
                mWakeLock.acquire();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mLedOnOff){
            if(mWakeLock.isHeld()){
                mWakeLock.release();

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"====onDestroy=====");
        if (LEDON == mLedState)
        {
            TurnOnOffLed(LEDOFF);
            mLedOnOff = false;
            mLedbg.setImageResource(R.drawable.torch_off);
        }
        if(mWakeLock.isHeld()){
            mWakeLock.release();
        }
    }

    private void onClickLedOnOffButton()
    {
        Log.i(TAG , "enter onClickLedOnOffButton") ;
        if (!mLedOnOff)
        {
            TurnOnOffLed(LEDON);
            mLedOnOff = true;
            mLedbg.setImageResource(R.drawable.torch_on);
        }else{
            TurnOnOffLed(LEDOFF);
            mLedOnOff = false;
            mLedbg.setImageResource(R.drawable.torch_off);
        }
    }

    private void TurnOnOffLed(final int on)
    {
        Log.i(TAG , "enter TurnOnOffLed function....")   ;
        if (camera == null || params == null) {
            return;
        }
        if (on == LEDON){
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
        }else{
            camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
        }
        mLedState = on;
    }

    private void getCamera(){
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e(TAG ,"Camera Error. Failed to Open. Error: " + e.getMessage());
            }
        }
    }

    private void releaseCamera(){
        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
