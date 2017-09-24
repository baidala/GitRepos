package ua.itstep.android11.gitrepos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

import ua.itstep.android11.gitrepos.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;


public class ItemListActivity extends AppCompatActivity {

    List<Model> results;
    RecyclerView recyclerView;
    EditText editTextSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

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

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
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
                    search(editable.toString());
                }
            }
        });

    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(results));
        recyclerView.setHasFixedSize(true);
    }

    private void search(String text) {
        Snackbar.make(recyclerView, "Sending request ...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        //text = "fase"; //only for debug
        String query = text + "+type:org";

        MainApp.getApi().getData(query).enqueue(new Callback<ModelsList>() {
            @Override
            public void onResponse(Call<ModelsList> call, Response<ModelsList> response) {
                Snackbar.make(recyclerView, "Fetching results ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

                    Snackbar.make(recyclerView, "Response code: "+ response.code()+" Results: " + response.body().getTotalCount(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    results.clear();
                    results.addAll(response.body().getItems());
                    recyclerView.getAdapter().notifyDataSetChanged();

                    if (Prefs.DEBUG) {
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse size: " + response.body().getTotalCount());
                        Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onResponse body: " + response.raw());
                        for (Model m: results) {
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
            public void onFailure(Call<ModelsList> call, Throwable t) {
                Snackbar.make(recyclerView, "ERROR: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onFailure "+ t.getMessage());

            }
        });

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


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Model> values;


        public SimpleItemRecyclerViewAdapter(List<Model> items) {
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
            holder.mContentView.setText(values.get(position).getLogin());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onBindViewHolder onClick");
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());

                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, fragment)
                            //.show(fragment)
                            .commit();

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
