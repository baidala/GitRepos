package ua.itstep.android11.gitrepos;

import android.net.Uri;

/**
 * Created by Maksim Baydala on 21/09/17.
 */

public class Prefs {

    static final boolean DEBUG = false;
    static final String LOG_TAG = "GitRepos>>>>>>>>>>>>>>";
    static final String KEY_RECYCLER_STATE = "recycler_state";
    static final String KEY_RECYCLER_DATASET_STATE  = "items_state";
    static final int ORGANIZATION = 1;
    static final int REPOS = 2;

    static final String TITLE = "title";
    static final String EDTEXT_IS_SHOWN = "is_visible";
    static final int DB_CURRENT_VERSION = 1;
    static final String TABLE_RESULTS = "results";
    static final String FIELD_GIT_ID = "git_id";
    static final String FIELD_LOGIN = "login";
    static final String FIELD_HTML_URL = "html_url";
    static final String FIELD_AVATAR_URL = "avatar_url";
    static final String URI_RESULTS_AUTHORITIES = "ua.itstep.android11.gitrepos";
    static final Uri URI_RESULTS = Uri.parse("content://" + URI_RESULTS_AUTHORITIES + "/" + TABLE_RESULTS);

}

