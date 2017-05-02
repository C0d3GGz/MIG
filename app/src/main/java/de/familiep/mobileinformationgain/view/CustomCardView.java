package de.familiep.mobileinformationgain.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.familiep.mobileinformationgain.R;

public class CustomCardView extends CardView {

    private Button cardButton;
    private TextView cardHeading, cardDescription;
    private Context con;

    public CustomCardView(Context context, int buttonNameRes, int headingRes, int descriptionRes) {
        this(context, context.getString(buttonNameRes), context.getString(headingRes),
                context.getString(descriptionRes));
    }

    public CustomCardView(Context context, String buttonName, String heading, String description){
        super(context);
        con = context;

        createView();

        if(buttonName == null)
            cardButton.setVisibility(View.GONE);

        if(description.length() < 50)
            cardDescription.setGravity(Gravity.CENTER);

        cardButton.setText(buttonName);
        cardHeading.setText(heading);
        cardDescription.setText(description);
    }

    private void createView(){
        View.inflate(con, R.layout.cardview, this);
        cardButton = (Button) findViewById(R.id.cardButton);
        cardHeading = (TextView) findViewById(R.id.cardHeading);
        cardDescription = (TextView) findViewById(R.id.cardDescription);
    }

    public void setButtonAction(OnClickListener listener){
        cardButton.setOnClickListener(listener);
    }

    public void setButtonEnabled(boolean enabled){
        cardButton.setEnabled(enabled);

        if(enabled) {
            cardButton.setTextColor(ContextCompat.getColor(con, R.color.colorAccent));
        }
        else{
            cardButton.setTextColor(Color.GRAY);
        }
    }

    public void setTitleText(int stringRes){
        cardHeading.setText(getResources().getString(stringRes));
    }

    public void setDescriptionText(int stringRes){
        String text = getResources().getString(stringRes);
        cardDescription.setText(text);
    }

    public void setButtonText(int stringRes) {
        cardButton.setText(getResources().getString(stringRes));
    }
}
