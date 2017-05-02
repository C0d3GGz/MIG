package de.familiep.mobileinformationgain.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import de.familiep.mobileinformationgain.CustomAccessibilityService;
import de.familiep.mobileinformationgain.R;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

public class LaunchingActivity extends AppCompatActivity{

    private TextView textView, errorText, exportText;
    private Button button, shareButtonStacktrace, exportButton;
    private FileHelper fileHelper;
    private Context con;

    private SharedprefsHelper sharedprefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sharedprefsHelper = new SharedprefsHelper(getApplicationContext());

        con = this;
        fileHelper = new FileHelper(getFilesDir());
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        errorText = (TextView) findViewById(R.id.errorText);
        shareButtonStacktrace = (Button) findViewById(R.id.shareButton);
        shareButtonStacktrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareIntent();
            }
        });

        Button errorButton = (Button) findViewById(R.id.error_button);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException();
            }
        });

        exportText = (TextView) findViewById(R.id.exportText);
        exportButton = (Button) findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(con, ExportActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAccessibilitySettingsOn(this)){
            textView.setText("Service is running.");
            exportButton.setVisibility(View.GONE);
            exportText.setVisibility(View.GONE);
        }
        else{
            textView.setText("Please enable accessibility service, use button below.");
            exportButton.setVisibility(View.VISIBLE);
            exportText.setVisibility(View.VISIBLE);
        }

        if(didApplicationCrash()){

            errorText.setVisibility(View.VISIBLE);
            shareButtonStacktrace.setVisibility(View.VISIBLE);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(con);
            dialogBuilder.setTitle("Application crash detected");
            dialogBuilder.setMessage("It apperars that the application crashed. " +
                    "Please share the errorlog with me.");

            dialogBuilder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showShareIntent();
                    dialog.dismiss();
                    sharedprefsHelper.appCrashed(false);
                }
            });

            dialogBuilder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sharedprefsHelper.appCrashed(false);
                    dialog.dismiss();
                }
            });

            dialogBuilder.show();

        }
    }

    //http://stackoverflow.com/a/18095283
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + CustomAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) { }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showShareIntent(){
        File crashlog = fileHelper.getErrorLog();
        ExportActivity.fireShareIntent(this, crashlog, "text/plain");
    }

    private boolean didApplicationCrash(){
        return sharedprefsHelper.didAppCrash();
    }
}
