package org.smartregister.chw.actionhelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.Utils;

import java.text.MessageFormat;

import timber.log.Timber;

public class ChildNewBornCareIntroductionActionHelper extends HomeVisitActionHelper {
    private String premature_baby;

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            premature_baby = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "premature_baby");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (!premature_baby.isEmpty()) {
            return MessageFormat.format("{0}: {1}", context.getString(R.string.baby_premature), StringUtils.capitalize(getTranslatedValue(premature_baby.trim().toLowerCase())));
        }
        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(premature_baby)) {
            return BaseAncHomeVisitAction.Status.PENDING;
        }else if (premature_baby.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }

    private String getTranslatedValue(String name) {
        if (StringUtils.isBlank(name))
            return name;
        String val = "pnc_" + name;
        return Utils.getStringResourceByName(val, context);
    }
}
