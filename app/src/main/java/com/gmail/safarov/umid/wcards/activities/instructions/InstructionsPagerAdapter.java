package com.gmail.safarov.umid.wcards.activities.instructions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;

public class InstructionsPagerAdapter extends FragmentPagerAdapter {
    public InstructionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Fragment getItem(int position) {
        return PagerFragment.newInstance(position);
    }


    public static class PagerFragment extends Fragment {
        int position;

        static PagerFragment newInstance(int position) {
            PagerFragment f = new PagerFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt("position");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.adapter_instructions, container, false);
            View tv = v.findViewById(R.id.text);
            TextView textView = (TextView) tv;

            switch (position) {
                case 0:
                    textView.setText(R.string.instructions_1);
                    break;
                case 1:
                    textView.setText(R.string.instructions_2);
                    break;
                case 2:
                    textView.setText(R.string.instructions_3);
                    break;
                case 3:
                    textView.setText(R.string.instructions_4);
                    break;
                case 4:
                    textView.setText(R.string.instructions_5);
                    break;
                case 5:
                    textView.setText(R.string.instructions_6);
                    break;
            }

            return v;
        }
    }
}
