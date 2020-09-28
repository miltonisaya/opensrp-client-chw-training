package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.hl7.fhir.r4.model.Base;
import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.adapter.BaseUpcomingServiceAdapter;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.rules.LmhHomeAlertRule;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

public class UpcomingServicesActivity extends CoreUpcomingServicesActivity {

    private RecyclerView dueTodayRV;
    private CustomFontTextView todayServicesTV;

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, UpcomingServicesActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public void setUpView() {
        super.setUpView();

        dueTodayRV = findViewById(R.id.today_services_recyclerView);
        todayServicesTV = findViewById(R.id.today_services);
    }

    @Override
    public void refreshServices(List<BaseUpcomingService> serviceList) {
        if (ChwApplication.getApplicationFlavor().splitUpcomingServicesView()) {

            boolean visitDue = isVisitDue();
            // get all eligible vaccines
            List<BaseUpcomingService> eligibleServiceList = new ArrayList<>();
            for (BaseUpcomingService filterService : serviceList) {
                List<BaseUpcomingService> eligibleVaccines = new ArrayList<>();
                for (BaseUpcomingService vaccine : filterService.getUpcomingServiceList()) {
                    if (vaccine.getExpiryDate() == null || new LocalDate(vaccine.getExpiryDate()).isAfter(new LocalDate())) {
                        eligibleVaccines.add(vaccine);
                    }
                }
                filterService.setUpcomingServiceList(eligibleVaccines);
                if (filterService.getUpcomingServiceList().size() > 0) {
                    eligibleServiceList.add(filterService);
                }
            }

            List<BaseUpcomingService> dueNowServiceList = new ArrayList<>();
            for (BaseUpcomingService service : eligibleServiceList) {
                if (service.getServiceDate() != null && visitDue) {
                    serviceList.removeAll(dueNowServiceList);
                    if (serviceList.isEmpty())
                        eligibleServiceList.remove(service);

                    dueNowServiceList.add(service);
                }
            }

            if (!dueNowServiceList.isEmpty()) {
                todayServicesTV.setVisibility(View.VISIBLE);
                dueTodayRV.setVisibility(View.VISIBLE);
                RecyclerView.Adapter<?> dueTodayAdapter = new BaseUpcomingServiceAdapter(this, dueNowServiceList);
                dueTodayRV.setAdapter(dueTodayAdapter);
            }

        }
        super.refreshServices(serviceList);
    }

    private boolean isVisitDue() {
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, memberObject.getBaseEntityId());
        String yearOfBirth = PersonDao.getDob(memberObject.getBaseEntityId());

        LmhHomeAlertRule alertRule = new LmhHomeAlertRule(
                ChwApplication.getInstance().getApplicationContext(), yearOfBirth, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());
         return ((CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(alertRule, CoreConstants.RULE_FILE.HOME_VISIT)).equalsIgnoreCase("Due"));
    }

    private List<BaseUpcomingService> getServiceList(List<BaseUpcomingService> serviceList) {
        List<BaseUpcomingService> eligibleServiceList = new ArrayList<>();
        for (BaseUpcomingService filterService : serviceList) {
            List<BaseUpcomingService> eligibleVaccines = new ArrayList<>();
            for (BaseUpcomingService vaccine : filterService.getUpcomingServiceList()) {
                if (vaccine.getExpiryDate() == null || new LocalDate(vaccine.getExpiryDate()).isAfter(new LocalDate())) {
                    eligibleVaccines.add(vaccine);
                }
            }
            filterService.setUpcomingServiceList(eligibleVaccines);
            if (filterService.getUpcomingServiceList().size() > 0) {
                eligibleServiceList.add(filterService);
            }
        }
        return eligibleServiceList;
    }

    protected List<BaseUpcomingService> filterDueTodayServices(List<BaseUpcomingService> serviceList) {
        List<BaseUpcomingService> dueNowServiceList = new ArrayList<>();

        for (BaseUpcomingService service : getServiceList(serviceList)) {
            if (service.getServiceDate() != null && isVisitDue()
            ) {
                dueNowServiceList.add(service);
            }
        }
        return dueNowServiceList;
    }
}
