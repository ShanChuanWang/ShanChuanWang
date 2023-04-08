package algonquin.cst2335_yikaiwang_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.lin_project.R.id;
import android.lin_project.R.layout;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //todo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        // bind the NasaPhotoActivity to btnNasa
        bindButtonClickListener(id.btnNasa, NasaOnlineActivity.class);
    }

    /**
     * Bind the searchButton click event to specific AppCompatActivity
     *
     * @param btnId the specific searchButton id
     * @param targetActivity the AppCompatActivity related to searchButton
     */
    private void bindButtonClickListener(
            int btnId, Class<? extends AppCompatActivity> targetActivity) {

        findViewById(btnId)
                .setOnClickListener(
                        click -> {
                            Intent nextPage = new Intent(MainActivity.this, targetActivity);
                            startActivity(nextPage);
                        });
    }
}