package org.smartregister.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface GeRegisterFragmentContract extends BaseRegisterFragmentContract {

    interface Model {
        String mainCondition();

        String defaultSortQuery();

        String mainSelect(String tableName, String mainCondition);

        String countSelect(String tableName, String mainCondition);
    }

}
