package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.adapter.ReportsFragmentAdapter;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;
import org.smartregister.chw.presenter.ListPresenter;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment implements ListContract.View<ReportType> {

    private View view;
    private ListableAdapter<ReportType, ListableViewHolder<ReportType>> mAdapter;
    private ProgressBar progressBar;
    private ListContract.Presenter<ReportType> presenter;
    private List<ReportType> reportTypes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reports_fragment, container, false);

        bindLayout();
        if (presenter == null)
            presenter = withPresenter();

        presenter.fetchList(() -> {
            List<ReportType> list = new ArrayList<>();
            list.add(new ReportType(getString(R.string.eligible_children), getString(R.string.eligible_children)));
            list.add(new ReportType(getString(R.string.doses_needed), getString(R.string.doses_needed)));
            list.add(new ReportType(getString(R.string.community_activity), getString(R.string.community_activity)));
            return list;
        });

        return view;
    }

    @Override
    public void bindLayout() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        mAdapter = adapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void renderData(List<ReportType> identifiables) {
        this.reportTypes = identifiables;
        refreshView();
    }

    @Override
    public void refreshView() {
        mAdapter.reloadData(reportTypes);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        progressBar.setVisibility(loadingState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onListItemClicked(ReportType reportType, int layoutID) {
        if (reportType.getID().equals(getString(R.string.eligible_children))) {

        } else if (reportType.getID().equals(getString(R.string.doses_needed))) {

        } else if (reportType.getID().equals(getString(R.string.community_activity))) {

        }
    }

    @NonNull
    @Override
    public <VH extends ListableViewHolder<ReportType>> ListableAdapter<ReportType, VH> adapter() {
        return (ListableAdapter<ReportType, VH>) new ReportsFragmentAdapter(reportTypes, this);
    }

    @NonNull
    @Override
    public ListContract.Presenter<ReportType> withPresenter() {
        return new ListPresenter<ReportType>()
                .with(this);
    }
}
