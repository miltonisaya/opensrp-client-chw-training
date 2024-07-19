package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.chw.kvp.util.Constants;
import org.smartregister.chw.presenter.GeRegisterActivityPresenter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class GeRegisterActivity extends BaseRegisterActivity {


    public static void startRegistration(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, GeRegisterActivity.class);
        intent.putExtra("baseEntityId", baseEntityId);
        intent.putExtra("payloadType", "REGISTRATION");
        intent.putExtra("formName", "iccm_enrollment");

        activity.startActivity(intent);
    }

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


        String baseEntityId = getIntent().getStringExtra("baseEntityId");
        String payloadType = getIntent().getStringExtra("payloadType");
        String formName = getIntent().getStringExtra("formName");

        if (payloadType.equalsIgnoreCase("registration")) {
            startFormActivity(formName, baseEntityId, null);
        }


    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            JSONObject jsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(GeRegisterActivity.this, formName);
            String locationId = Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            org.smartregister.chw.anc.util.JsonFormUtils.getRegistrationForm(jsonObject, entityId, locationId);
            startFormActivity(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonObject.toString());
        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setName("GE Registration");
        form.setWizard(true);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

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
