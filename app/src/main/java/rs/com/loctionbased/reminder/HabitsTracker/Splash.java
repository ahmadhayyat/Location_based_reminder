package rs.com.loctionbased.reminder.HabitsTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import rs.com.loctionbased.reminder.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if( sharedPreferences.getInt("new", 0) == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("new", 1);
            editor.apply();
            setContentView(R.layout.activity_splash);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void doneClick (View view){
        Log.i("Done Clicked", "Bringing to main page.");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
