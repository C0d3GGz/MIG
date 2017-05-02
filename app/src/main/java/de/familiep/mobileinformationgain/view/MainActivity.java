package de.familiep.mobileinformationgain.view;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import de.familiep.mobileinformationgain.BuildConfig;
import de.familiep.mobileinformationgain.CustomAccessibilityService;
import de.familiep.mobileinformationgain.R;
import de.familiep.mobileinformationgain.data_evaluation.ExportHelperService;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

public class MainActivity extends AppCompatActivity {

    private ViewGroup outerLayout;
    private CustomCardView welcome, uuid, optimize, service, export;
    private SharedprefsHelper sharedprefsHelper;

    public static final String EXTRA_SHOW_SHARE_DIALOG = "showsharedialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        sharedprefsHelper = new SharedprefsHelper(this);

        //setup layout
        outerLayout = (ViewGroup) findViewById(R.id.main_layout);

        welcome = new CustomCardView(this, R.string.welcome_card_button,
                R.string.welcome_card_title, R.string.welcome_card_description);
        outerLayout.addView(welcome, 0);

        uuid = new CustomCardView(this, null, getString(R.string.uuid_card_title),
                sharedprefsHelper.getUUID());
        outerLayout.addView(uuid, 1);

        optimize = new CustomCardView(this, R.string.optimizer_card_button,
                R.string.optimizer_card_title, R.string.optimizer_card_description);
        outerLayout.addView(optimize, 2);

        service = new CustomCardView(this, R.string.startservice_card_button,
                R.string.startservice_card_title, R.string.startservice_card_description);
        outerLayout.addView(service, 3);

        export = new CustomCardView(this, R.string.export_card_button,
                R.string.export_card_title, R.string.export_card_description);
        outerLayout.addView(export, 4);

        boolean showShareDialog = getIntent().getBooleanExtra(EXTRA_SHOW_SHARE_DIALOG, false);

        if(showShareDialog){
            showShareDialog();
        }

//        sharedprefsHelper.setExporting(false);
//        sharedprefsHelper.setFinishedExporting(false);
//        sharedprefsHelper.setCustomEvalFinished(false);
//        sharedprefsHelper.setDeflateFinished(false);
//        sharedprefsHelper.setCustomEvalUnprocessedEntry(1L);
//        sharedprefsHelper.setDeflateUnprocessedEntry(0L);
//        sharedprefsHelper.setDeflateCsvFileName("");
//        sharedprefsHelper.setCustomEvalCsvFileName("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //if something went wrong with the alarm manager, because of "optimizing" apps, check if 7 days is over by hand
        long studyFinishedWhenMillisReached = sharedprefsHelper.getDataCollectionFinishedMillis();
        if(studyFinishedWhenMillisReached != 0L){ //data collection is active
            if(System.currentTimeMillis() > studyFinishedWhenMillisReached){
                sharedprefsHelper.setDataCollectionFinished(true);
            }
        }

        setupWelcomeCard();
        setupBatteryOptimizationCard();
        setupStartServiceCard();
        setupExportCard();
    }

    public void setupWelcomeCard(){

        if(!sharedprefsHelper.shouldShowWelcomeCard()){
            outerLayout.removeView(welcome);
            return;
        }

        welcome.setButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcome.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {

                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationCancel(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}
                    @Override public void onAnimationEnd(Animator animation) {
                        outerLayout.removeView(welcome);
                        sharedprefsHelper.setShouldShowWelcomeCard(false);
                    }

                });
            }
        });
    }

    @SuppressLint("NewApi")
    public void setupBatteryOptimizationCard() {

        //doze only for M+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            outerLayout.removeView(optimize);
            return;
        }

        if(sharedprefsHelper.isDataCollectionFinished()){
            outerLayout.removeView(optimize);
            return;
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if(pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName())) {
            optimize.setDescriptionText(R.string.optimizer_card_description_running);
            optimize.setButtonEnabled(false);
        } else {
            optimize.setButtonEnabled(true);
            optimize.setDescriptionText(R.string.optimizer_card_description);
            optimize.setButtonAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
            });
        }
    }

    public void setupStartServiceCard(){

        service.setButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        if(isAccessibilitySettingsOn() && sharedprefsHelper.isDataCollectionFinished()){
            service.setButtonEnabled(true);
            service.setTitleText(R.string.startservice_card_title_turnoff);
            service.setDescriptionText(R.string.startservice_card_description_finished);
            return;
        }
        else if(!isAccessibilitySettingsOn() && sharedprefsHelper.isDataCollectionFinished()){
            service.setButtonEnabled(false);
            service.setTitleText(R.string.startservice_data_card_title_collection_finished);
            service.setDescriptionText(R.string.startservice_data_card_description_final);
            return;
        }

        if(isAccessibilitySettingsOn()){
            service.setButtonEnabled(false);
            service.setDescriptionText(R.string.startservice_card_description_running);
        } else{
            service.setButtonEnabled(true);
            service.setDescriptionText(R.string.startservice_card_description);
        }
    }

    public void setupExportCard(){

        export.setButtonEnabled(false);

        if(sharedprefsHelper.hasFinishedExporting()){
            //export behavior
            export.setButtonEnabled(true);
            export.setButtonText(R.string.export_card_button_send);
            export.setDescriptionText(R.string.export_card_description_finished);
            export.setButtonAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShareDialog();
                }
            });
            return;
        }

        if(sharedprefsHelper.isExporting()){
            export.setButtonEnabled(false);
            export.setDescriptionText(R.string.export_card_description_running);
            return;
        }

        boolean dataCollectionFinished = sharedprefsHelper.isDataCollectionFinished();
        if(!isAccessibilitySettingsOn() && dataCollectionFinished){
            export.setButtonEnabled(true);
            export.setDescriptionText(R.string.export_card_description_ready);
            export.setButtonAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedprefsHelper.setExporting(true);
                    setupAndStartExporting();
                    Snackbar.make(outerLayout, R.string.snack_export_started, Snackbar.LENGTH_LONG).show();
                    setupExportCard();
                }
            });
        }
    }

    private void setupAndStartExporting(){
        startService(new Intent(this, ExportHelperService.class));
    }

    //http://stackoverflow.com/a/18095283
    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + CustomAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
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

    private void showShareDialog(){

        File data = new File(sharedprefsHelper.getExportedDataPath());
        if(!data.exists()){
            Snackbar.make(outerLayout, "Error", Snackbar.LENGTH_LONG).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",
                data);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("application/zip")
                .setSubject(data.getName())
                .setStream(uri)
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }
}
