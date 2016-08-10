package org.tbadg.bottrust;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private final static int MSECS_BETWEEN_STEPS = 500;
    private final static int MSECS_BETWEEN_SIMS = 1000;
    private String mStatus;

    private enum Color {
        ORANGE, BLUE
    }

    private View mContent;
    private TextView mInputSetTxt;
    private TextView mSimTxt;
    private TextView mStatusTxt;
    private TextView mTimeTxt;
    private FloatingActionButton mPlayFab;

    private int mInputSet = R.raw.small_input_set;
    private boolean mSimulateAll = false;
    private boolean mQuickMode = false;

    private List<String> mSims;
    private List<Color> mSteps;
    private List<Integer> mOrangeButtons;
    private List<Integer> mBlueButtons;
    private int mCurrentSim = 0;

    private int mMaxPos;
    private int mStepInc;

    private boolean mRunning;
    private int mDelayTime;
    private int mTime;
    private int mOrangePos;
    private int mBluePos;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContent = findViewById(R.id.content);
        mInputSetTxt = (TextView) findViewById(R.id.input_set);
        mSimTxt = (TextView) findViewById(R.id.sim_txt);
        mStatusTxt = (TextView) findViewById(R.id.status);
        mTimeTxt = (TextView) findViewById(R.id.time);

        final FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.previous);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPreviousSim();
            }
        });

        mPlayFab = (FloatingActionButton) findViewById(R.id.play);
        mPlayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRunning) {
                    cancelRunningSim();

                } else {
                    runSim();
                }
            }
        });

        final FloatingActionButton next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNextSim();
            }
        });

        mInputSet = R.raw.small_input_set;
        loadAllSims(mInputSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_small_set:
                item.setChecked(true);
                if (mInputSet != R.raw.small_input_set) {
                    mInputSet = R.raw.small_input_set;
                    loadAllSims(R.raw.small_input_set);
                }
                return true;

            case R.id.action_large_set:
                item.setChecked(true);
                if (mInputSet != R.raw.large_input_set) {
                    mInputSet = R.raw.large_input_set;
                    loadAllSims(R.raw.large_input_set);
                }
                return true;

            case R.id.action_sim_all:
                item.setChecked(!item.isChecked());
                mSimulateAll = item.isChecked();
                return true;

            case R.id.action_quick:
                item.setChecked(!item.isChecked());
                mQuickMode = item.isChecked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAllSims(int input_set) {
        InputStream is = getResources().openRawResource(input_set);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        mSims = new ArrayList<>();

        try {
            mSims.add(br.readLine());
            int numSims = Integer.valueOf(mSims.get(0));
            for (int indx = 0; indx < numSims; ++indx) {
                if ((line = br.readLine()) != null) {
                    mSims.add(line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("Bad input file: %s\n%s", e,
                                     Arrays.toString(e.getStackTrace())));
        }

        Log.i(TAG, String.format("Number of sims: %d", mSims.size()));
        mCurrentSim = 0;
        selectNextSim();
    }

    private boolean selectPreviousSim() {
        cancelRunningSim();
        if (mCurrentSim > 1) {
            --mCurrentSim;
            Log.v(TAG, String.format("Current sim is now '%s'", mSims.get(mCurrentSim)));
            setInputAndSimTxt();

            return true;
        }
        return false;
    }

    private boolean selectNextSim() {
        cancelRunningSim();
        if (mCurrentSim < mSims.size() - 1) {
            ++mCurrentSim;
            Log.v(TAG, String.format("Current sim is now '%s'", mSims.get(mCurrentSim)));
            setInputAndSimTxt();

            return true;
        }
        return false;
    }

    private void setInputAndSimTxt() {
        if (mInputSet == R.raw.small_input_set) {
            mInputSetTxt.setText(String.format(Locale.US, getString(R.string.small_set),
                                               mCurrentSim, mSims.get(0)));
        } else {
            mInputSetTxt.setText(String.format(Locale.US, getString(R.string.large_set),
                                               mCurrentSim, mSims.get(0)));
        }
        mSimTxt.setText(mSims.get(mCurrentSim));
    }

    private void runSim() {
        Log.v(TAG, "Starting sim...");

        try {
            mMaxPos = parseSequence(mSims.get(mCurrentSim));
        } catch (Exception e) {
            Log.e(TAG, String.format("Bad input file: %s\n%s", e,
                                     Arrays.toString(e.getStackTrace())));
        }

        mStepInc = getStepInc(mMaxPos);
        ((TextView) findViewById(R.id.orange_max)).setText(String.valueOf(mMaxPos));
        ((TextView) findViewById(R.id.blue_max)).setText(String.valueOf(mMaxPos));

        cancelRunningSim();

        mPlayFab.setImageResource(R.drawable.ic_media_stop);
        mRunning = true;
        mHandler.post(simulateStep);
    }

    private void cancelRunningSim() {
        mRunning = false;
        mHandler.removeCallbacks(simulateStep);

        mTime = 0;
        mDelayTime = MSECS_BETWEEN_STEPS;
        mOrangePos = 1;
        mBluePos = 1;

        setOrangeViewPos(0);
        setBlueViewPos(0);

        mStatusTxt.setText(null);
        mTimeTxt.setVisibility(View.INVISIBLE);
        mPlayFab.setImageResource(R.drawable.ic_media_play);
    }

    private final Runnable simulateStep = new Runnable() {
        @Override
        public void run() {
            if (mSteps.isEmpty()) {
                finishSim(mTime);

            } else {
                if (!mQuickMode) {
                    try {
                        Thread.sleep(mDelayTime);
                    } catch (InterruptedException e) {
                        // Ignore interruptions
                    }
                }
                mTime++;
                mStatus = String.format(Locale.US, "At time %d", mTime);

                int nextOrange = (mOrangeButtons.isEmpty()) ? 0 : mOrangeButtons.get(0);
                int nextBlue = (mBlueButtons.isEmpty()) ? 0 : mBlueButtons.get(0);

                if (mSteps.get(0) == Color.ORANGE) {
                    if (mOrangePos == nextOrange) {
                        mDelayTime = MSECS_BETWEEN_STEPS;
                        pushOrangeButton();
                    } else {
                        if (mDelayTime == MSECS_BETWEEN_STEPS) {
                            mDelayTime = MSECS_BETWEEN_STEPS / Math.abs(mOrangePos - nextOrange);
                        }
                        moveOrange(nextOrange);
                    }
                    if (nextBlue != 0 && mBluePos != nextBlue) {
                        moveBlue(nextBlue);
                    }

                } else {
                    if (mBluePos == nextBlue) {
                        mDelayTime = MSECS_BETWEEN_STEPS;
                        pushBlueButton();
                    } else {
                        if (mDelayTime == MSECS_BETWEEN_STEPS) {
                            mDelayTime = MSECS_BETWEEN_STEPS / Math.abs(mBluePos - nextBlue);
                        }
                        moveBlue(nextBlue);
                    }
                    if (nextOrange != 0 && mOrangePos != nextOrange) {
                        moveOrange(nextOrange);
                    }
                }

                mStatusTxt.setText(mStatus);
                Log.v(TAG, mStatus);

                mHandler.post(simulateStep);
            }
        }
    };

    private void finishSim(int time) {
        // ToDo: Write the output lines to a file to avoid having to scrape it from logcat:
        Log.e(TAG, String.format(getString(R.string.output_txt), mCurrentSim, time));

        mTimeTxt.setText(getResources().getQuantityString(R.plurals.total_time, mTime, mTime));
        mTimeTxt.setVisibility(View.VISIBLE);
        mPlayFab.setImageResource(R.drawable.ic_media_play);

        if (mSimulateAll) {
            if (!mQuickMode) {
                try {
                    Thread.sleep(MSECS_BETWEEN_SIMS);
                } catch (InterruptedException e) {
                    // Ignore interruptions
                }
            }

            if (selectNextSim()) {
                runSim();
            } else {
                mStatusTxt.setText(R.string.finished);
            }
        }
    }

    private void pushOrangeButton() {
        mStatus += String.format(Locale.US, ", Orange has pushed button %d", mOrangeButtons.get(0));

        mOrangeButtons.remove(0);
        mSteps.remove(0);
    }

    private void pushBlueButton() {
        mStatus += String.format(Locale.US, ", Blue has pushed button %d", mBlueButtons.get(0));

        mBlueButtons.remove(0);
        mSteps.remove(0);
    }

    private void moveOrange(int next) {
        // Which direction does the bot need to travel?
        if (next > mOrangePos) {
            ++mOrangePos;
        } else {
            --mOrangePos;
        }

        setOrangeViewPos((mOrangePos - 1) * mStepInc);
        mStatus += String.format(Locale.US, ", Orange has moved to %d", mOrangePos);
    }

    private void moveBlue(int next) {
        // Which direction does the bot need to travel?
        if (next > mBluePos) {
            ++mBluePos;
        } else {
            --mBluePos;
        }

        setBlueViewPos((mBluePos - 1) * mStepInc);
        mStatus += String.format(Locale.US, ", Blue has moved to %d", mOrangePos);
    }

    private int parseSequence(String inputSequence) {
        String tokens[] = inputSequence.split(" ");
        int numSteps = Integer.valueOf(tokens[0]);

        mSteps = new ArrayList<>(numSteps);
        mOrangeButtons = new ArrayList<>(numSteps);
        mBlueButtons = new ArrayList<>(numSteps);

        int maxPos = 0;
        int ch = 1;
        for (int step = 0; step < numSteps; ++step) {
            char color = tokens[ch++].charAt(0);
            int position = Integer.valueOf(tokens[ch++]);
            if (position > maxPos) {
                maxPos = position;
            }
            if (color == 'O') {
                mSteps.add(Color.ORANGE);
                mOrangeButtons.add(position);
            } else {
                mSteps.add(Color.BLUE);
                mBlueButtons.add(position);
            }
        }

        Log.v(TAG, String.format("mSteps = %s,\nmOrangeButtons = %s,\nmBlueButtons = %s",
                                 mSteps, mOrangeButtons, mBlueButtons));
        return maxPos;
    }

    public int getStepInc(int maxPos) {
        if (mMaxPos == 1) {
            // Degenerate case where there's only 1 button:
            // ToDo: Work out an elegant way to avoid the 75 magic number:
            return mContent.getWidth() - 75;
        } else {
            return (mContent.getWidth() - 75) / (mMaxPos - 1);
        }
    }

    public void setOrangeViewPos(float pos) {
        Log.v(TAG, String.format("Setting orange view to %f", pos));
        TextView mOrangePos = (TextView) findViewById(R.id.orange_pos);
        ImageView mOrangeImg = (ImageView) findViewById(R.id.orange_img);

        mOrangePos.setX(pos);
        mOrangePos.setText(String.valueOf(this.mOrangePos));
        mOrangeImg.setX(pos);
    }

    public void setBlueViewPos(float pos) {
        Log.v(TAG, String.format("Setting blue view to %f", pos));
        TextView mBluePos = (TextView) findViewById(R.id.blue_pos);
        ImageView mBlueImg = (ImageView) findViewById(R.id.blue_img);

        mBluePos.setX(pos);
        mBluePos.setText(String.valueOf(this.mBluePos));
        mBlueImg.setX(pos);
    }
}
