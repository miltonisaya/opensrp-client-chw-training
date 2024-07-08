package org.smartregister.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface GeRegisterFragmentContract extends BaseRegisterFragmentContract {

    interface Model {
        String mainCondition();

        String defaultSortQuery();

        String getTableName();

        String mainSelect(String mainCondition);

        String countSelect(String mainCondition);
    }

}
