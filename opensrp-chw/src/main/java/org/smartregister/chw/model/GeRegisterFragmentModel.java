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
        return "ec_gender_equality";
    }

    @Override
    public String mainSelect(String mainCondition) {
        return "SELECT g.id as _id, fm.*,f.village_town FROM "+getTableName()+" g JOIN ec_family_member fm on fm.base_entity_id = g.base_entity_id join ec_family f on f.base_entity_id = fm.relational_id WHERE g."+mainCondition;
    }

    @Override
    public String countSelect(String mainCondition) {
        return "SELECT COUNT(*) FROM "+getTableName()+" WHERE "+mainCondition;

    }
}
