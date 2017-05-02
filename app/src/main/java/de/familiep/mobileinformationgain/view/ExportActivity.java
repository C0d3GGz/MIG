package de.familiep.mobileinformationgain.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

import de.familiep.mobileinformationgain.BuildConfig;
import de.familiep.mobileinformationgain.R;
import de.familiep.mobileinformationgain.data_evaluation.ExportHelperService;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.persistence2.DbReadAccessHelper;

public class ExportActivity extends AppCompatActivity {

    private ExportHelperService exportHelper;
    private FileHelper fileHelper;
    private DbReadAccessHelper dbReadAccessHelper;
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        fileHelper = new FileHelper(activity.getFilesDir());
        dbReadAccessHelper = new DbReadAccessHelper(activity);
//        exportHelper = new ExportHelperService(fileHelper, dbReadAccessHelper);

        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.button);
        button.setText("export");

        button.setOnClickListener(exportAction);
    }

    private View.OnClickListener exportAction = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
//            File file = exportHelper.export();
//            fireShareIntent(activity, file, "text/csv");
        }
    };

    public static void fireShareIntent(Activity activity, File data, String contentType){
        Uri uri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                data);

        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType(contentType)
                .setSubject(data.getName())
                .setStream(uri)
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbReadAccessHelper.close();
    }
}
