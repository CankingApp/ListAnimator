package net.canking.myanimtest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;

public class MainActivity extends Activity {

    private static List<MyCell> mAnimList = new ArrayList<MyCell>();
    private MyAnimListAdapter mMyAnimListAdapter;
    private ListView mAniListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 50; i++) {
            MyCell cell = new MyCell();
            cell.name = "Cell No." + Integer.toString(i);
            mAnimList.add(cell);
        }

        mMyAnimListAdapter = new MyAnimListAdapter(this, R.layout.chain_cell, mAnimList);
        mAniListView = (ListView) findViewById(R.id.chainListView);
        mAniListView.setAdapter(mMyAnimListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        final Handler mymHandler = new Handler();
        Runnable aRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int index = mAniListView.getCount();
                if (index <= 0) return;
                deleteCell(mAniListView.getChildAt(0), 0);
                mymHandler.postDelayed(this, AnimationBuilde.ANIMATION_DURATION * 3
                        );
            }
        };
        mymHandler.post(aRunnable);
        return super.onOptionsItemSelected(item);
    }

    private void deleteCell(final View v, final int index) {
        AnimatorSet animatorSet = AnimationBuilde.buildListRemoveAnimator(v, mAnimList, mMyAnimListAdapter,
                index);
        animatorSet.start();
    }

    private class MyCell {
        public String name;
    }

    public class MyAnimListAdapter extends ArrayAdapter<MyCell> {
        private LayoutInflater mInflater;
        private int resId;

        public MyAnimListAdapter(Context context, int textViewResourceId, List<MyCell> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder vh;
            MyCell cell = (MyCell) getItem(position);

            if (convertView == null) {
                view = mInflater.inflate(R.layout.chain_cell, parent, false);
                setViewHolder(view);
            }
            else if (((ViewHolder) convertView.getTag()).needInflate) {
                view = mInflater.inflate(R.layout.chain_cell, parent, false);
                setViewHolder(view);
            }
            else {
                view = convertView;
            }

            vh = (ViewHolder) view.getTag();
            vh.text.setText(cell.name);
            vh.imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCell(view, position);
                }
            });

            return view;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.text = (TextView) view.findViewById(R.id.cell_name_textview);
            vh.imageButton = (ImageButton) view.findViewById(R.id.cell_trash_button);
            vh.needInflate = false;
            view.setTag(vh);
        }
    }

    public class ViewHolder {
        public boolean needInflate;
        public TextView text;
        ImageButton imageButton;
    }
}
