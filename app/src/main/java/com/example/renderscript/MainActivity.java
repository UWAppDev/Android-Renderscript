package com.example.renderscript;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.fivemileview.photospheretoplanets.ScriptC_compute;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewOut;

    public RenderScript myRender;
    public ScriptC_compute myScript;
    Bitmap[] bitmapsOut;
    public Allocation allocationIn;
    public Allocation[] allocationsOut;

    private int whichBitmap;

    private static final int NUM_OUTS = 2;

    private boolean computing;
    private int next;

    private static final int NOTHING = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValues();
        initViews();
        initRenderscript();
    }

    private void initValues() {
        computing = false;
        next = NOTHING;
    }

    private void initViews() {
        ConstraintLayout containerMain = (ConstraintLayout)findViewById(R.id.container_main);
        imageViewOut = (ImageView)findViewById(R.id.image_view_output);
        SeekBar seekBarAdjust = (SeekBar)findViewById(R.id.seek_bar_adjust);

        seekBarAdjust.setProgress(0); // init adjust
        seekBarAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                compute(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

        containerMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout(){
                containerMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                compute(seekBarAdjust.getProgress());
            }
        });
    }

    private void initRenderscript() {
        boolean releaseUpdated = false;
        boolean debug = true;
        if (releaseUpdated || debug){
            // force recompile of renderscript.
            final File[] files = getCacheDir().listFiles();
            if(files != null){
                for(File file: files){
                    FileUtils.deleteQuietly(file);
                }
            }
        }

        myRender = RenderScript.create(MainActivity.this);
        myScript = new ScriptC_compute(myRender);

        Bitmap bitmapIn = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        allocationIn = Allocation.createFromBitmap(myRender, bitmapIn);

        bitmapsOut = new Bitmap[NUM_OUTS];
        allocationsOut = new Allocation[NUM_OUTS];
        for (int i = 0; i < NUM_OUTS; i++) {
            bitmapsOut[i] = Bitmap.createBitmap(bitmapIn.getWidth(), bitmapIn.getHeight(), Bitmap.Config.ARGB_8888);
            allocationsOut[i] = Allocation.createFromBitmap(myRender, bitmapsOut[i]);
        }

        whichBitmap = 0;
    }

    private void compute(int adjust) {
        if (computing) {
            next = adjust;
        } else {
            computing = true;
            ComputeThread computeThread = new ComputeThread();
            ComputeInput input = new ComputeInput(MainActivity.this, whichBitmap, adjust);
            computeThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
            whichBitmap = (whichBitmap + 1)%NUM_OUTS;
        }
    }

    public void display(int whichBitmap) {
        computing = false;
        if (next != NOTHING) {
            compute(next);
            next = NOTHING;
        }
        imageViewOut.setImageBitmap(bitmapsOut[whichBitmap]);
    }

}