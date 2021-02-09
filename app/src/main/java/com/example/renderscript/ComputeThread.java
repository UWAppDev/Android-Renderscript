package com.example.renderscript;

import android.os.AsyncTask;

public class ComputeThread extends AsyncTask<ComputeInput, Void, ComputeOutput> {

    @Override
    protected ComputeOutput doInBackground(ComputeInput...inputs){
        ComputeInput input = inputs[0];
        MainActivity main = input.main;
        int whichBitmap = input.whichBitmap;

        main.myScript.set_adjust(input.adjust);
        main.myScript.forEach_compute(main.allocationIn, main.allocationsOut[whichBitmap]);
        main.allocationsOut[whichBitmap].copyTo(main.bitmapsOut[whichBitmap]);

        return new ComputeOutput(main, whichBitmap);
    }

    @Override
    protected void onPostExecute(ComputeOutput output){
        output.main.display(output.whichBitmap);
    }

}
