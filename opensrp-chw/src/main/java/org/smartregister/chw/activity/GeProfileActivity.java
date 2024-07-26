package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class GeProfileActivity extends BaseProfileActivity {
    private CommonPersonObjectClient clientDetails;

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setupViews() {
        super.setupViews();
        Intent intent = getIntent();
        clientDetails = (CommonPersonObjectClient) intent.getSerializableExtra("client_details");

        CircleImageView avator = findViewById(R.id.imageview_profile);
        CustomFontTextView textViewName = findViewById(R.id.textview_name);
        CustomFontTextView textViewSex = findViewById(R.id.textview_detail_one);
        CustomFontTextView textViewLocation = findViewById(R.id.textview_detail_two);
        CustomFontTextView textViewClientId = findViewById(R.id.textview_detail_three);
        Button btnProfileServices = findViewById(R.id.btn_profile_registration_info);

        if (clientDetails != null) {
            String fullName = clientDetails.getColumnmaps().get("first_name") + " " + clientDetails.getColumnmaps().get("middle_name") + " " + clientDetails.getColumnmaps().get("last_name");
            int age = Utils.getAgeFromDate(clientDetails.getColumnmaps().get("dob"));
            String clientId = clientDetails.getColumnmaps().get("unique_id");

            textViewName.setText(fullName + ", " + age);
            textViewSex.setText(clientDetails.getColumnmaps().get("gender"));
            textViewLocation.setText(clientDetails.getColumnmaps().get("village_town"));
            textViewClientId.setText("ID: " + clientId);
        }
        avator.setImageDrawable(getResources().getDrawable(R.mipmap.ic_member));
        btnProfileServices.setText(R.string.provide_ge_services);

        btnProfileServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject myJsonObject = new FormUtils().getFormJsonFromRepositoryOrAssets(GeProfileActivity.this, "ge_individual_services_form");
                    assert myJsonObject != null;
                    myJsonObject.put("entity_id", clientDetails.getColumnmaps().get("base_entity_id"));

                    Intent intent = new Intent(GeProfileActivity.this, JsonFormActivity.class);
                    intent.putExtra("json", myJsonObject.toString());
                    startActivityForResult(intent, 700);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 700 && resultCode == RESULT_OK) {
            String jsonObject = intent.getStringExtra("json");
            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();

            //1. Convert the JSON data into an event
            Event event = JsonFormUtils.processJsonForm(allSharedPreferences, jsonObject, "ec_ge_services");

            //2. Save and process the created event
            String jsonString = JsonFormUtils.gson.toJson(event);
            try {
                NCUtils.processEvent(event.getBaseEntityId(), new JSONObject(jsonString));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
