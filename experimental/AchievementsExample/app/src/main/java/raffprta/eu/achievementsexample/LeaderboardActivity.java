package raffprta.eu.achievementsexample;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

/**
 * @author Raffaello Perrotta
 * Class to demonstrate how to operate leaderboard functionality. It lets the user type in a number
 * for their score and submit it. This is purely for demonstration purposes as well as testing out
 * various views that the android libs have to offer.
 */
public class LeaderboardActivity extends BaseGameManagerActivity {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final Button submitButton = (Button) findViewById(R.id.button4);
        final Button displayAchievementsButton = (Button) findViewById(R.id.button5);
        final EditText submitText = (EditText) findViewById(R.id.editText);

        submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // If the login had failed universally at the start of the app
                if(!getGoogleAPI().isConnected()){
                    displayErrorMessage();
                    return;
                }

                int tempScore = -1;

                try{
                    tempScore = Integer.parseInt(submitText.getText().toString());
                }catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_format), Toast.LENGTH_LONG).show();
                    return;
                }

                final int score = tempScore;
                // Progress bar
                setProgressBarIndeterminate(true);
                setProgressBarVisibility(true);
                // Retrieve the score and set callback to notify us of the retrieval.
                Games.Leaderboards.loadCurrentPlayerLeaderboardScore(getGoogleAPI(), getString(R.string.lead_id),
                                     LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).
                                        setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                                            // When the result is sent, we check if it exceeds the P.B. and then submit it
                                            @Override
                                            public void onResult(Leaderboards.LoadPlayerScoreResult result) {
                                                LeaderboardScore retrievedScore = result.getScore();

                                                // If there's no data, airplane mode, or other issues with syncing to the web side
                                                if(result.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK){
                                                    displayLeaderboardError(result.getStatus());
                                                    // return; -- It's not a bad idea to save to a cached copy for it to auto-sync? so return commented out
                                                }

                                                // Null is returned the first time submitting a score as there's nothing to retrieve.
                                                if(retrievedScore == null || retrievedScore.getRawScore() < score){
                                                    Games.Leaderboards.submitScore(getGoogleAPI(), getString(R.string.lead_id), score);
                                                    if(result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK)
                                                        Toast.makeText(getApplicationContext(), getString(R.string.lead_submit), Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), getString(R.string.lead_submit_no), Toast.LENGTH_LONG).show();
                                                }

                                                submitText.setText("");
                                                submitButton.setEnabled(false);
                                                setProgressBarVisibility(false);

                                            }

                                        });

            }
        });

        displayAchievementsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // NOTE IF THE PLAYER HAS NOT SET THEIR  GAMES PROFILE TO SHARE INFO PUBLICALLY
                // THEN ONLY THE SOCIAL LEADERBOARD WILL BE SHOWN BY GOOGLE: SOCIAL LEADERBOARDS
                // WILL ALSO BE EMPTY WHEN AN APP IS UNPUBLISHED: https://developers.google.com/games/services/common/concepts/leaderboards
                if(getGoogleAPI().isConnected())
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getGoogleAPI(),
                                        getString(R.string.lead_id)), LeaderboardVariant.COLLECTION_PUBLIC);
                else
                    displayErrorMessage();

            }
        });

    }

    private void displayLeaderboardError(Status status) {
        Toast.makeText(getApplicationContext(), getString(R.string.net_err_no_prog) + " : " + GamesStatusCodes.getStatusString(status.getStatusCode()), Toast.LENGTH_LONG).show();
    }



}
