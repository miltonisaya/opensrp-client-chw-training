package org.smartregister.chw.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.chw.presenter.GeRegisterActivityPresenter;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Collections;
import java.util.List;

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
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

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
