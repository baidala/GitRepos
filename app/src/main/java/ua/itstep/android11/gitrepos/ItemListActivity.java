package ua.itstep.android11.gitrepos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;


public class ItemListActivity extends AppCompatActivity {

    private static Bundle bundleState;
    ArrayList<Model> results;
    RecyclerView recyclerView;
    EditText editTextSearch;
    Toolbar toolbar;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        results = new ArrayList<>();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        actionBar = getSupportActionBar();


        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        if (savedInstanceState != null){
            restorePreviousState(savedInstanceState);
        } else {
            toolbar.setTitle(getTitle());
            editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" afterTextChanged length: " + editable.length() );

                    if (editable.length() >= 3){
                        search(editable.toString(), Prefs.ORGANIZATION);
                    }
                }
            });

            restoreFromDB();
        }




    }

    private void restorePreviousState(Bundle savedInstanceState){
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + ".restorePreviousState " );
        setTitle(savedInstanceState.getString(Prefs.TITLE));
        //setTitle("restored");
        if (!savedInstanceState.getBoolean(Prefs.EDTEXT_IS_SHOWN)) {
            editTextSearch.setVisibility(View.GONE);
        }
        Parcelable listState = savedInstanceState.getParcelable(Prefs.KEY_RECYCLER_STATE);
        ArrayList<Model> dataSet = savedInstanceState.getParcelableArrayList(Prefs.KEY_RECYCLER_DATASET_STATE);
        results.clear();
        if ( dataSet != null) {
            results.addAll(dataSet);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        recyclerView.getLayoutManager().onRestoreInstanceState(listState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + ".onPause " );
        bundleState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        bundleState.putParcelable(Prefs.KEY_RECYCLER_STATE, listState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + ".onResume " );
        if(bundleState != null){
            Parcelable listState = bundleState.getParcelable(Prefs.KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + ".onSaveInstanceState");
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(Prefs.KEY_RECYCLER_STATE, listState);
        outState.putParcelableArrayList(Prefs.KEY_RECYCLER_DATASET_STATE, ((SimpleItemRecyclerViewAdapter)recyclerView.getAdapter()).getParcelableList()  );
        outState.putString(Prefs.TITLE, toolbar.getTitle().toString());
        outState.putBoolean(Prefs.EDTEXT_IS_SHOWN, editTextSearch.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onBackPressed");

        if (editTextSearch.isShown()){
            super.onBackPressed();
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            editTextSearch.setVisibility(View.VISIBLE);
            toolbar.setTitle(getTitle());
            restoreFromDB();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onOptionsItemSelected ");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(results));
        recyclerView.setHasFixedSize(true);
    }


    private void saveToDB(ArrayList<OrgModel> list){
        ContentResolver cr = getContentResolver();
        ContentValues cv = new ContentValues();

        cr.delete(Prefs.URI_RESULTS, null, null);

        for (OrgModel model: list) {
            cv.put(Prefs.FIELD_GIT_ID, model.getId());
            cv.put(Prefs.FIELD_LOGIN, model.getLogin());
            cv.put(Prefs.FIELD_HTML_URL, model.getHtmlUrl());
            cv.put(Prefs.FIELD_AVATAR_URL, model.getAvatarUrl());

            Uri resultId = cr.insert(Prefs.URI_RESULTS, cv);
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" saveToDB: " + resultId);
        }

    }

    private void restoreFromDB() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Prefs.URI_RESULTS, null, null, null, null);
        results.clear();

        if ( (cursor != null) && cursor.moveToFirst() ) {
            do {
                OrgModel result = new OrgModel();
                result.setId( cursor.getInt(cursor.getColumnIndex(Prefs.FIELD_GIT_ID)));
                result.setLogin(cursor.getString(cursor.getColumnIndex(Prefs.FIELD_LOGIN)));
                result.setHtmlUrl(cursor.getString(cursor.getColumnIndex(Prefs.FIELD_HTML_URL)));
                result.setAvatarUrl(cursor.getString(cursor.getColumnIndex(Prefs.FIELD_AVATAR_URL)));

                results.add(result);
            } while (cursor.moveToNext());

            recyclerView.getAdapter().notifyDataSetChanged();

            cursor.close();
        }
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" restoreFromDB ");
    }

    private void search(String text, int param) {
        Snackbar.make(recyclerView, "Sending request ...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        switch (param) {
            case Prefs.ORGANIZATION:
                searchOrg(text);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "search ORGANIZATION");
                break;
            case Prefs.REPOS:
                searchRepos(text);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "search REPOS");
                break;
            default:
                break;

        }






        /*
        //new array list that will hold the filtered data
        ArrayList<String> filteredNames = new ArrayList<>();

        //looping through existing elements
        for (String s : results) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filteredNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        recyclerView.getAdapter().notifyDataSetChanged();
        //adapter.filterList(filterdNames);
        */
    }

    private void searchOrg(String text){
        //text = "fase"; //only for debug
        String query = text + "+type:org";

        MainApp.getApi().getOrganization(query).enqueue(new Callback<OrgModelsList>() {
            @Override
            public void onResponse(Call<OrgModelsList> call, Response<OrgModelsList> response) {
                Snackbar.make(recyclerView, "Fetching results ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

                    Snackbar.make(recyclerView, "Response code: "+ response.code()+" Results: " + response.body().getTotalCount(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    ArrayList<OrgModel> items = (ArrayList<OrgModel>) response.body().getItems();
                    results.clear();
                    if ( items != null) {
                        results.addAll(items);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        saveToDB(items);
                    }


                    if (Prefs.DEBUG) {
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse size: " + response.body().getTotalCount());
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse body: " + response.raw());
                        for (OrgModel m: items) {
                            Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + "login: "+ m.getLogin());

                        }
                    }

                } else {
                    //request unsuccessful (like 400,401,403 etc)
                    Snackbar.make(recyclerView, "Response code: "+ response.code()+ " Failed to fetch results: " + response.message(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<OrgModelsList> call, Throwable t) {
                Snackbar.make(recyclerView, "ERROR: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onFailure "+ t.getMessage());

            }
        });
    }

    private void searchRepos(final String login){
        MainApp.getApi().getRepos(login).enqueue(new Callback<List<ReposModel>>() {
            @Override
            public void onResponse(Call<List<ReposModel>> call, Response<List<ReposModel>> response) {
                Snackbar.make(recyclerView, "Fetching results ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

                    Snackbar.make(recyclerView, "Response code: "+ response.code()+" Results: " + response.body().size(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    List<ReposModel> items = response.body() ;
                    results.clear();
                    if ( items != null) {
                        if (actionBar != null) {
                            actionBar.setDisplayHomeAsUpEnabled(true);
                        }

                        results.addAll(items);
                        toolbar.setTitle( login + " repositories "+"("+results.size()+")" );
                        recyclerView.getAdapter().notifyDataSetChanged();
                        editTextSearch.setVisibility(View.GONE);

                    }


                    if (Prefs.DEBUG) {
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse size: " + response.body().size());
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse body: " + response.raw());
                        for (ReposModel m: items) {
                            Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + "repos: "+ m.getFullName());

                        }
                    }

                } else {
                    //request unsuccessful (like 400,401,403 etc)
                    Snackbar.make(recyclerView, "Response code: "+ response.code()+ " Failed to fetch results: " + response.message(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReposModel>> call, Throwable t) {
                Snackbar.make(recyclerView, "ERROR: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onFailure "+ t.getMessage());

            }
        });
    }



    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Model> values;


        public SimpleItemRecyclerViewAdapter(ArrayList<Model> items) {
            values = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onBindViewHolder pos="+position);

            holder.mItem = values.get(position);

            if (holder.mItem == null) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onBindViewHolder mIten == NULL");

            holder.mIdView.setText(values.get(position).getId().toString());

            if (holder.mItem instanceof OrgModel) {
                holder.mContentView.setText(((OrgModel) holder.mItem).getLogin());
            } else {
                holder.mContentView.setText(((ReposModel) holder.mItem).getName());
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Prefs.DEBUG)
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + " onBindViewHolder onClick");
                    if (Prefs.DEBUG)
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + " onBindViewHolder onClick editTextSearch.isShown: " + editTextSearch.isShown());

                    if (holder.mItem instanceof OrgModel) {
                        //Bundle arguments = new Bundle();
                        //arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());

                        search(((OrgModel) holder.mItem).getLogin(), Prefs.REPOS);

                    }


                    //ItemDetailFragment fragment = new ItemDetailFragment();
                    //fragment.setArguments(arguments);
                    //getSupportFragmentManager().beginTransaction()
                    //       .replace(R.id.frameLayout, fragment)
                    //.show(fragment)
                    //       .commit();

                    /*
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                    */
                }
            });
        }

        public ArrayList<Model> getParcelableList() {
            Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +".getParcelableList ");
            return values;
        }

        public void setItems(ArrayList<Model> itemsList) {
            Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +".setItems size: " +itemsList.size());
            values = new ArrayList<Model>();
            values.addAll(itemsList);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Model mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    } //SimpleItemRecyclerViewAdapter

}
