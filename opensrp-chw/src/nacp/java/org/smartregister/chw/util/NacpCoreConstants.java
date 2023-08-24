package org.smartregister.chw.util;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.assetManager;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.locale;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;

public class NacpCoreConstants extends CoreConstants {
    public static class PNC_HOME_VISIT{
        private static final String LOCATION = "pnc_hv_location";

        public static String getLocation() {
            return Utils.getLocalForm(LOCATION, locale, assetManager);
        }
    }
}
