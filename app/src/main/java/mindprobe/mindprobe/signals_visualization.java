package mindprobe.mindprobe;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class signals_visualization extends Fragment{
    static LinearLayout graph_view_hr, graph_view_gsr;
    static LinearLayout.LayoutParams lp;
    static TextView bpm, gsr;
    static Button start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.signals_layout, container, false);
        graph_view_hr = (LinearLayout)rootView.findViewById(R.id.hr_graphic);
        graph_view_gsr = (LinearLayout)rootView.findViewById(R.id.gsr_graphic);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); // Verbose!
        bpm = (TextView)rootView.findViewById(R.id.hr_value);
        gsr = (TextView)rootView.findViewById(R.id.gsr_value);
        start = (Button)rootView.findViewById(R.id.start);

        if (MainActivity.start_acquisition == true || MainActivity.mScanning == true){
            start.setClickable(false);
        }else{
            start.setClickable(true);
        }

        Timer timing = new Timer();
        timing.schedule(new TimerTask() {

            @Override
            public void run() {

                graph_view_hr.post(new Runnable() {

                    public void run() {
                        graph_view_hr.removeAllViews();
                        //LinearLayout A = new LinearLayout(MainActivity.context);
                        //plot2d graph = new plot2d(MainActivity.context, MainActivity.xvalues, MainActivity.yvalues_hr, MainActivity.cycles, MainActivity.pos); //editar isto, n?o s?o precisos os dois ultimos par?metros
                        //A.addView(graph, lp);
                        //graph_view_hr.addView(A, lp);
                        //graph_view_hr.postInvalidate();
                        if (MainActivity.start_acquisition) {
                            bpm.setText(Integer.toString(MainActivity.hr_value));
                        }
                    }
                });

                graph_view_gsr.post(new Runnable() {

                    public void run() {
                        graph_view_gsr.removeAllViews();
                        //LinearLayout A = new LinearLayout(MainActivity.context);
                        //plot2d graph = new plot2d(MainActivity.context, MainActivity.xvalues, MainActivity.yvalues_gsr, MainActivity.cycles, MainActivity.pos); //editar isto, n?o s?o precisos os dois ultimos par?metros
                        //A.addView(graph, lp);
                        //graph_view_gsr.addView(A, lp);
                        //graph_view_gsr.postInvalidate();
                        if (MainActivity.start_acquisition) {
                            gsr.setText(Integer.toString(MainActivity.gsr_value));
                        }
                    }
                });
            }
        }, 100, 500);
        return rootView;
    }
}
