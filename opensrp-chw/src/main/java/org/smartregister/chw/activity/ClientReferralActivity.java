package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.ReferralTypeAdapter;
import org.smartregister.chw.contract.ClientReferralContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ClientReferralActivity extends AppCompatActivity implements ClientReferralContract.View, View.OnClickListener {

    private ReferralTypeAdapter referralTypeAdapter;
    private FormUtils formUtils;
    private String baseEntityId;
    private Map<String, String> encounterTypeToTableMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_referral);
        referralTypeAdapter = new ReferralTypeAdapter();
        encounterTypeToTableMap = new HashMap<>();
        mapEncounterTypeToTable();
        referralTypeAdapter.setOnClickListener(this);
        setUpView();
    }

    private void mapEncounterTypeToTable() {
        encounterTypeToTableMap.put(Constants.EncounterType.SICK_CHILD, CoreConstants.TABLE_NAME.CHILD_REFERRAL);
        encounterTypeToTableMap.put(Constants.EncounterType.PNC_REFERRAL, CoreConstants.TABLE_NAME.PNC_REFERRAL);
        encounterTypeToTableMap.put(Constants.EncounterType.ANC_REFERRAL, CoreConstants.TABLE_NAME.ANC_REFERRAL);
    }

    @Override
    public void setUpView() {
        List<ReferralTypeModel> referralTypeModels = null;
        RecyclerView referralTypesRecyclerView = findViewById(R.id.referralTypeRecyclerView);

        ImageView closeImageView = findViewById(R.id.close);
        closeImageView.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            referralTypeModels = getIntent().getExtras().getParcelableArrayList(Constants.REFERRAL_TYPES);
            baseEntityId = getIntent().getStringExtra(Constants.ENTITY_ID);
        }

        referralTypeAdapter.setReferralTypes(referralTypeModels);
        referralTypesRecyclerView.setAdapter(referralTypeAdapter);
        referralTypesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void startReferralForm(JSONObject jsonObject) {
        ReferralRegistrationActivity.startGeneralReferralFormActivityForResults(this,baseEntityId,jsonObject);
    }

    @Override
    public FormUtils getFormUtils() throws Exception {
        if (this.formUtils == null) {
            this.formUtils = new FormUtils(this.getApplicationContext());
        }
        return this.formUtils;
    }

    @Override
    public boolean isReferralForm(String encounterType) {
        switch (encounterType) {
            case CoreConstants.EventType.CHILD_REFERRAL:
            case CoreConstants.EventType.PNC_REFERRAL:
            case CoreConstants.EventType.ANC_REFERRAL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            finish();
        } else if (v.getTag() instanceof ReferralTypeAdapter.ReferralTypeViewHolder) {
            ReferralTypeAdapter.ReferralTypeViewHolder referralTypeViewHolder = (ReferralTypeAdapter.ReferralTypeViewHolder) v.getTag();
            ReferralTypeModel referralTypeModel = referralTypeAdapter.getReferralTypes().get(referralTypeViewHolder.getAdapterPosition());
            try {
                if (referralTypeModel.getFormName() == null) {
                    org.smartregister.util.Utils.showShortToast(this, getString(R.string.open_referral_form, referralTypeModel.getReferralType()));
                }
                startReferralForm(getFormUtils().getFormJson(referralTypeModel.getFormName()));
            } catch (Exception e) {
                Timber.e(e, "ClientReferralActivity --> onActivityResult");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON)
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (isReferralForm(encounterType)) {
                    CoreReferralUtils.createReferralEvent(Utils.getAllSharedPreferences(),
                            jsonString, encounterTypeToTableMap.get(encounterType), baseEntityId);
                    Utils.showToast(this, this.getString(R.string.referral_submitted));
                }
            } catch (Exception e) {
                Timber.e(e, "ClientReferralActivity --> onActivityResult");
            }
    }
}

