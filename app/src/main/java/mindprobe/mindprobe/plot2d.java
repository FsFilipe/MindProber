package mindprobe.mindprobe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class plot2d extends View {

    private Paint paint;
    private int cycles, pos;
    private float[] yvalues, yvalues_ordered,  xvalues, xvalues_ordered;
    //private long[] xvalues, xvalues_ordered;
    private float maxy,miny,locxAxis,locyAxis, maxx, minx;
    //private long maxx, minx;
    private int vectorLength;
    private int axes = 1;

    public plot2d(Context context, float[] xvalues, float[] yvalues, int cycles, int pos) {
        super(context);
        this.xvalues=xvalues;
        this.yvalues=yvalues;
        this.cycles = cycles;
        this.pos = pos;
        paint = new Paint();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        float canvasHeight = getHeight();
        float canvasWidth = getWidth();

        if (cycles == 0){
            vectorLength = pos+1;
            xvalues_ordered = new float[vectorLength];
            yvalues_ordered = new float[vectorLength];

            for (int i = 0; i < vectorLength; i++){
                xvalues_ordered[i] = xvalues[i];
                yvalues_ordered[i] = yvalues[i];
            }
        }else{
            int pos_start = 0;

            for (int i = 0; i < 60; i++){
                int x;
                if (i+1 == 60){
                    x = 0;
                }else{
                    x = i+1;
                }
                if (xvalues[i] > xvalues[x]){
                    pos_start = x;
                }

            }

            int pos_array = 0;

            vectorLength = 60;
            xvalues_ordered = new float[vectorLength];
            yvalues_ordered = new float[vectorLength];

            for (int i = pos_start; i < 60; i++){
                xvalues_ordered[pos_array] = xvalues[i];
                yvalues_ordered[pos_array] = yvalues[i];
                pos_array++;
            }
            for (int j = 0; j < pos_start; j++){
                xvalues_ordered[pos_array] = xvalues[j];
                yvalues_ordered[pos_array] = yvalues[j];
                pos_array++;
            }
        }

        int pos_start = 0;

        for (int i = 0; i < 60; i++){
            int x;
            if (i+1 == 60){
                x = 0;
            }else{
                x = i+1;
            }
            if (xvalues[i] > xvalues[x]){
                pos_start = x;
            }

        }

        int pos_array = 0;

        vectorLength = 60;
        xvalues_ordered = new float[vectorLength];
        yvalues_ordered = new float[vectorLength];

        for (int i = pos_start; i < 60; i++){
            xvalues_ordered[pos_array] = xvalues[i];
            yvalues_ordered[pos_array] = yvalues[i];
            pos_array++;
        }
        for (int j = 0; j < pos_start; j++){
            xvalues_ordered[pos_array] = xvalues[j];
            yvalues_ordered[pos_array] = yvalues[j];
            pos_array++;
        }

        getAxes(xvalues_ordered, yvalues_ordered);


        int[] xvaluesInPixels = toPixel(canvasWidth, minx, maxx, xvalues_ordered);
        int[] yvaluesInPixels = toPixel(canvasHeight, miny, maxy, yvalues_ordered);
        int locxAxisInPixels = toPixelInt(canvasHeight, miny, maxy, locxAxis);
        int locyAxisInPixels = toPixelInt(canvasWidth, minx, maxx, locyAxis);
        //String xAxis = "x-axis";
        //String yAxis = "y-axis";

        paint.setStrokeWidth(2);
        canvas.drawARGB(255, 255, 255, 255);
        for (int i = 0; i < vectorLength-1; i++) {
            paint.setColor(Color.RED);
            canvas.drawLine(xvaluesInPixels[i],canvasHeight-yvaluesInPixels[i],xvaluesInPixels[i+1],canvasHeight-yvaluesInPixels[i+1],paint);
            //canvas.drawLine(xvaluesInPixels[i],canvasHeight-yvaluesInPixels[i],xvaluesInPixels[i],canvasHeight-yvaluesInPixels[i],paint);

        }

        paint.setColor(Color.BLACK);
        //canvas.drawLine(0,canvasHeight-locxAxisInPixels,canvasWidth,canvasHeight-locxAxisInPixels,paint);
        //canvas.drawLine(locyAxisInPixels,0,locyAxisInPixels,canvasHeight,paint);

        //Automatic axes markings, modify n to control the number of axes labels
        if (axes!=0){
            float temp = 0.0f;
            int n=3;
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(20.0f);
            for (int i=1;i<=n;i++){
                //temp = Math.round(10*(minx+(i-1)*(maxx-minx)/n))/10;
                //canvas.drawText(""+temp, (float)toPixelInt(canvasWidth, minx, maxx, temp),canvasHeight-locxAxisInPixels+20, paint);
                temp = Math.round(10*(miny+(i-1)*(maxy-miny)/n))/10;
                canvas.drawText(""+temp, locyAxisInPixels+20,canvasHeight-(float)toPixelInt(canvasHeight, miny, maxy, temp), paint);
            }
			/*String text = null;
			if (maxx > (float)60){
				text = Integer.toString((int)maxx/60) + "m " + Float.toString(maxx-((int)maxx/60)*60) + "s";
			}else{
				text = Float.toString(maxx) + "s";
			}*/
            //canvas.drawText(text , (float)toPixelInt(canvasWidth, minx, maxx, maxx),canvasHeight-locxAxisInPixels+20, paint);
            canvas.drawText(""+maxy, locyAxisInPixels+20,canvasHeight-(float)toPixelInt(canvasHeight, miny, maxy, maxy), paint);
            //canvas.drawText(xAxis, canvasWidth/2,canvasHeight-locxAxisInPixels+45, paint);
            //canvas.drawText(yAxis, locyAxisInPixels-40,canvasHeight/2, paint);
        }


    }

    private int[] toPixel(float pixels, float min, float max, float[] value) {

        double[] p = new double[value.length];
        int[] pint = new int[value.length];

        for (int i = 0; i < value.length; i++) {
            p[i] = .1*pixels+((value[i]-min)/(max-min))*.8*pixels;
            pint[i] = (int)p[i];
        }

        return (pint);
    }



    //this function gets the min and the max value to print the graph
    private void getAxes(float[] xvalues2, float[] yvalues) {

        minx=getMin(xvalues2);
        miny=getMin(yvalues);
        maxx=getMax(xvalues2);
        maxy=getMax(yvalues);

        if (minx>=0)
            locyAxis=minx;
        else if (minx<0 && maxx>=0)
            locyAxis=0;
        else
            locyAxis=maxx;

        if (miny>=0)
            locxAxis=miny;
        else if (miny<0 && maxy>=0)
            locxAxis=0;
        else
            locxAxis=maxy;

    }

    private int toPixelInt(float pixels, float min, float max, float value) {

        double p;
        int pint;
        p = .1*pixels+((value-min)/(max-min))*.8*pixels;
        pint = (int)p;
        return (pint);
    }

    private float getMax(float[] v) {
        float largest = v[0];
        for (int i = 0; i < v.length; i++)
            if (v[i] > largest)
                largest = v[i];
        return largest;
    }

    private float getMin(float[] v) {
        float smallest = v[0];
        for (int i = 0; i < v.length; i++)
            if (v[i] < smallest)
                smallest = v[i];
        return smallest;
    }


}
