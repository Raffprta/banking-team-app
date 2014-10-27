package raffprta.eu.achievementsexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;

import java.util.concurrent.TimeUnit;


/***
 * @author Raffaello Perrotta
 * Class showcasing a mockup using the achievements API.
 */
public class AchievementsActivity extends BaseGameManagerActivity{

    private Intent leaderBoardIntent;
    AchievementBuffer buff = null;
    // Let's make some custom toast!
    private Toaster toaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leaderBoardIntent = new Intent(getApplicationContext(), LeaderboardActivity.class);
        toaster = new Toaster(this);

        // Simple listeners for the buttons.
        final Button achievementsUnlockOne = (Button) findViewById(R.id.button);
        final Button achievementsUnlockTwo = (Button) findViewById(R.id.button2);
        Button achievementsView = (Button) findViewById(R.id.button3);
        ImageView turnThePage = (ImageView) findViewById(R.id.imageView);


        // Attempt to asycnhronously load the data to a buffer.
        runOnUiThread(new Runnable(){

            @Override
            public void run() {
                new AchievementsGrabber().execute();
            }

        });


        achievementsUnlockOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAchievementUnlock(R.string.ach_id_1, R.string.ach1, R.drawable.ach1, 0, false, 0);
                // Lock this button.
                achievementsUnlockOne.setEnabled(false);

            }

        });

        achievementsUnlockTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                attemptAchievementUnlock(R.string.ach_id_2, R.string.ach2, R.drawable.ach2, 1, true, 1);
            }

        });

        achievementsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(getGoogleAPI().isConnected()) {
                    // 2 seems to be the request code to show ALL achievements. 1 is for just unlocked.
                    // I can't seem to find the constant used in the Games API for this anywhere :-(
                    startActivityForResult(Games.Achievements.getAchievementsIntent(getGoogleAPI()), 2);
                }else{
                    displayErrorMessage();
                }
            }

        });

        turnThePage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(leaderBoardIntent);
                overridePendingTransition(R.anim.slide_out, R.anim.slide_out);
            }

        });

    }

    /**
     * Attempts to unlock an achievement, displays message informing the user if there was an error in the process
     * or if the achievement was already previously unlocked.
     * @param achievementId the id as is shown in the Google dev console
     * @param stringIdResource the string name of the achievement
     * @param achievementDrawable the drawable icon of the achievement
     * @param listNumber where in the buffer it will be (refer to the listing in your Google play console 0 -> n-1)
     * @param isIncrementable true if it's one to increment
     * @param incrementValue the value to increment but only if isIncrementable is true, otherwise this value is unused
     */
    private void attemptAchievementUnlock(int achievementId, int stringIdResource, int achievementDrawable, int listNumber,
                                          boolean isIncrementable, int incrementValue){
        if (getGoogleAPI().isConnected()){
            if(buff == null){
                Toast.makeText(getApplicationContext(), R.string.net_err, Toast.LENGTH_LONG).show();
                return;
            }

            if(buff.get(listNumber).getState() != Achievement.STATE_UNLOCKED) {
                // Unlock and show a message to the user!
                if(isIncrementable)
                    Games.Achievements.increment(getGoogleAPI(), getString(achievementId), incrementValue);
                else
                    Games.Achievements.unlock(getGoogleAPI(), getString(achievementId));
                toaster.grabToast(getString(stringIdResource),achievementDrawable);
            }else{
                Toast.makeText(getApplicationContext(), R.string.already_unlocked, Toast.LENGTH_LONG).show();
            }
        }else{
            displayErrorMessage();

        }
    }

    /**
     * @author Raffaello Perrotta
     * Asynchronous class which returns an AchievementBuffer of the achievements if possible.
     */
    private class AchievementsGrabber extends AsyncTask<Void, Void, AchievementBuffer> {

        @Override
        protected AchievementBuffer doInBackground(Void... params) {
            // False checks the achievements cache first
            PendingResult res = Games.Achievements.load(getGoogleAPI(), false);
            // Call, with 30 seconds of wait time
            Achievements.LoadAchievementsResult r = (Achievements.LoadAchievementsResult)res.await(30, TimeUnit.SECONDS );

            // If the network is kaput or we have no fall back on cached data.
            if((r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK &&
                r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA)){
                Log.w("Error: ", GamesStatusCodes.getStatusString(r.getStatus().getStatusCode()));
                return null;
            }

            AchievementBuffer buffer = r.getAchievements();
            buff = buffer;
            return buffer;
        }
    }





}
