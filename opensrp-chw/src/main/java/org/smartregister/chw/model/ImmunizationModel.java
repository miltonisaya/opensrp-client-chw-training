package org.smartregister.chw.model;

import org.joda.time.DateTime;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class ImmunizationModel {
    private List<Vaccine> vaccines;
    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public ArrayList<HomeVisitVaccineGroup> determineAllHomeVisitVaccineGroup(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch) {
        if(this.vaccines!=null) this.vaccines.clear();
        this.vaccines = vaccines;
        Map<String, Date> receivedvaccines = receivedVaccines(vaccines);
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());

        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupArrayList = new ArrayList<>();
        LinkedHashMap<String, Integer> vaccineGroupMap = new LinkedHashMap<>();
        for (VaccineRepo.Vaccine vaccine : vList) {
            if (vaccine.category().equalsIgnoreCase("child")) {

                String stateKey = VaccinateActionUtils.stateKey(vaccine);
                if (isNotBlank(stateKey)) {

                    Integer position = vaccineGroupMap.get(stateKey);
                    // create a group if missing
                    if (position == null) {
                        HomeVisitVaccineGroup homeVisitVaccineGroup = new HomeVisitVaccineGroup();
                        homeVisitVaccineGroup.setGroup(stateKey);

                        homeVisitVaccineGroupArrayList.add(homeVisitVaccineGroup);

                        // get item location
                        position = homeVisitVaccineGroupArrayList.indexOf(homeVisitVaccineGroup);

                        vaccineGroupMap.put(stateKey, position);
                    }

                    // add due date
                    computeDueDate(position, vaccine, alerts, receivedvaccines, homeVisitVaccineGroupArrayList, sch);
                }
            }
        }

        for (int x = 0; x < homeVisitVaccineGroupArrayList.size(); x++) {
            // compute not given vaccines
            homeVisitVaccineGroupArrayList.get(x).calculateNotGivenVaccines();
            for (int i = 0; i < homeVisitVaccineGroupArrayList.get(x).getDueVaccines().size(); i++) {
                for (VaccineWrapper notgivenVaccine : notGivenVaccines) {
                    if (homeVisitVaccineGroupArrayList.get(x).getDueVaccines().get(i).display().equalsIgnoreCase(notgivenVaccine.getName())) {
                        homeVisitVaccineGroupArrayList.get(x).getNotGivenInThisVisitVaccines().add(notgivenVaccine.getVaccine());
                    }
                }
            }

        }

        return homeVisitVaccineGroupArrayList;
    }

    private void computeDueDate(
            Integer position, VaccineRepo.Vaccine vaccine, List<Alert> alerts, Map<String, Date> receivedvaccines,
            ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupArrayList, List<Map<String, Object>> sch
    ) {
        if (hasAlert(vaccine, alerts)) {

            // add vaccine
            homeVisitVaccineGroupArrayList.get(position).getDueVaccines().add(vaccine);

            // add alert
            ImmunizationState state = assignAlert(vaccine, alerts);
            if (state == ImmunizationState.DUE || state == ImmunizationState.OVERDUE || state == ImmunizationState.UPCOMING || state == ImmunizationState.EXPIRED) {
                homeVisitVaccineGroupArrayList.get(position).setAlert(assignAlert(vaccine, alerts));
            }

            // check if vaccine is received and record as given
            if (isReceived(vaccine.display(), receivedvaccines)) {
                homeVisitVaccineGroupArrayList.get(position).getGivenVaccines().add(vaccine);
            }

            // compute due date
            for (Map<String, Object> toprocess : sch) {
                if (((VaccineRepo.Vaccine) (toprocess.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
                    DateTime dueDate = (DateTime) toprocess.get(DATE);
                    if (dueDate != null) {
                        homeVisitVaccineGroupArrayList.get(position).setDueDate(dueDate.toLocalDate() + "");
                        homeVisitVaccineGroupArrayList.get(position).setDueDisplayDate(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
                        //add to date wise vaccine list.needed to display vaccine name with date in adapter
                        if(homeVisitVaccineGroupArrayList.get(position).getGroupedByDate().get(dueDate) == null){
                            ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
                            vaccineArrayList.add(vaccine);
                            homeVisitVaccineGroupArrayList.get(position).getGroupedByDate().put(dueDate,vaccineArrayList);
                        }else{
                            homeVisitVaccineGroupArrayList.get(position).getGroupedByDate().get(dueDate).add(vaccine);
                        }

                    }
                }
            }
        }
    }

    private boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return true;
            }
        }
        return false;
    }

    private ImmunizationState alertState(Alert toProcess) {
        if (toProcess == null) {
            return ImmunizationState.NO_ALERT;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
            return ImmunizationState.DUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
            return ImmunizationState.UPCOMING;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
            return ImmunizationState.OVERDUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
            return ImmunizationState.EXPIRED;
        }
        return ImmunizationState.NO_ALERT;
    }

    private boolean isReceived(String s, Map<String, Date> receivedvaccines) {
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return alertState(alert);
            }
        }
        return ImmunizationState.NO_ALERT;
    }
}
