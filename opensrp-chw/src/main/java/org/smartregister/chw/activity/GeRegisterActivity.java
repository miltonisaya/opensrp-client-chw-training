package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.chw.presenter.GeRegisterActivityPresenter;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class GeRegisterActivity extends BaseRegisterActivity {


    @Override
    protected void initializePresenter() {
        presenter = new GeRegisterActivityPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GeRegisterFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtain an instance of the Navigation menu within our activity
        NavigationMenu.getInstance(this, null, null);

        Intent geIntent = getIntent();
        String receivedBaseEntityId = geIntent.getStringExtra("BASE_ENTITY_ID");
        if (receivedBaseEntityId != null) {
//            Toast.makeText(this, "GeRegisterActivity : Open Enrollment Form", Toast.LENGTH_LONG).show();
            startFormActivity("ge_enrollment_consent_form", receivedBaseEntityId, null);
        }
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String formName, String baseEntityId, String metaData) {
        try {
            JSONObject myJsonObject = new FormUtils().getFormJsonFromRepositoryOrAssets(GeRegisterActivity.this, formName);
            assert myJsonObject != null;
            myJsonObject.put("entity_id", baseEntityId);
            startFormActivity(myJsonObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, JsonFormActivity.class);
        intent.putExtra("json", jsonObject.toString());
        startActivityForResult(intent, 700);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 700 && resultCode == RESULT_OK) {
            String jsonObject = intent.getStringExtra("json");
            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();
            //1. Convert the JSON data into an event
            Event event = JsonFormUtils.processJsonForm(allSharedPreferences, jsonObject, "ec_gender_equality");

            //2. Save and process the created event
            String jsonString = JsonFormUtils.gson.toJson(event);
            try {
                NCUtils.processEvent(event.getBaseEntityId(), new JSONObject(jsonString));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Collections.emptyList();
    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
    }
}
