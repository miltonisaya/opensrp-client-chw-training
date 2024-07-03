package org.smartregister.chw.model;


import org.smartregister.chw.contract.GeRegisterFragmentContract;

public class GeRegisterFragmentModel implements GeRegisterFragmentContract.Model {
    @Override
    public String mainCondition() {
        return " gender = 'Female' ";
    }

    @Override
    public String defaultSortQuery() {
        return " last_interacted_with DESC ";
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        return "SELECT id as _id, * FROM " + tableName + " WHERE " + mainCondition;
    }

    @Override
    public String countSelect(String tableName, String mainCondition) {
        return "SELECT count(*) FROM " + tableName + " WHERE " + mainCondition;

    }
}
