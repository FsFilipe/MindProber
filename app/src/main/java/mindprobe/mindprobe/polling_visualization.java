package mindprobe.mindprobe;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class polling_visualization extends Fragment {

    DrawingView dv ;
    LinearLayout gradient;
    TextView score;
    private Paint mPaint;
    static LinearLayout.LayoutParams lp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.polling_layout, container, false);

        gradient = (LinearLayout)rootView.findViewById(R.id.gradient);

        score = (TextView)rootView.findViewById(R.id.score);
        mPaint = new Paint();
        dv = new DrawingView(MainActivity.context, gradient, score);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); // Verbose!
        gradient.addView(dv, lp);

        score.setText(Integer.toString(DrawingView.global_score));

        return rootView;
    }
}