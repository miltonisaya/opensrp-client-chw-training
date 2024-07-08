package org.smartregister.chw.model;


import org.smartregister.chw.contract.GeRegisterFragmentContract;

public class GeRegisterFragmentModel implements GeRegisterFragmentContract.Model {
    @Override
    public String mainCondition() {
        return " is_closed = 0 ";
    }

    @Override
    public String defaultSortQuery() {
        return " last_interacted_with DESC ";
    }

    @Override
    public String getTableName() {
        return "ec_family_member";
    }

    @Override
    public String mainSelect(String mainCondition) {
        return "SELECT id as _id, * FROM "+getTableName()+" WHERE "+mainCondition;
    }

    @Override
    public String countSelect(String mainCondition) {
        return "SELECT COUNT(*) FROM "+getTableName()+" WHERE "+mainCondition;

    }
}
