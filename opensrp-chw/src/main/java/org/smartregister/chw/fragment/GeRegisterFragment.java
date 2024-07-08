package org.smartregister.chw.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.chw.R;
import org.smartregister.chw.model.GeRegisterFragmentModel;
import org.smartregister.chw.presenter.GeRegisterFragmentPresenter;
import org.smartregister.chw.provider.OpdRegisterProvider;
import org.smartregister.chw.sbc.provider.SbcRegisterProvider;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class GeRegisterFragment extends BaseRegisterFragment {

    public void initializeAdapter() {
        OpdRegisterProvider childRegisterProvider = new OpdRegisterProvider(getActivity(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("GE");
    }

    @Override
    protected void initializePresenter() {
        presenter = new GeRegisterFragmentPresenter(GeRegisterFragment.this, new GeRegisterFragmentModel());
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return ((GeRegisterFragmentPresenter) presenter).getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return ((GeRegisterFragmentPresenter) presenter).getDefaultSortQuery();
    }


    @Override
    public String getTablename() {
        return ((GeRegisterFragmentPresenter)presenter).getTableName();
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }
}
