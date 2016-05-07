package mindprobe.mindprobe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class Definicoes extends Fragment {
    public static EditText mHostIP;
    public static EditText mHostPort;
    public static String user_host_ip;
    public static String user_host_port;

    public Definicoes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_definicoes, container, false);

        mHostIP = (EditText) rootView.findViewById(R.id.host_ip);
        mHostPort = (EditText) rootView.findViewById(R.id.host_port);
        return rootView;
    }

}
