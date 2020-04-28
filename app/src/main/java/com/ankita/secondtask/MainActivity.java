package com.ankita.secondtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ankita.secondtask.adapter.UserItemAdapter;
import com.ankita.secondtask.modals.CreateResponse;
import com.ankita.secondtask.repository.APIClient;
import com.ankita.secondtask.repository.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private boolean isLoading=false;
    private APIInterface apiInterface;
    private ArrayList<CreateResponse.User> userList;
    private RecyclerView rvUserItem;
    private UserItemAdapter userItemAdapter;
    private List<CreateResponse.User> list;
    private int itemLoad=10,lim=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        callGetWs(itemLoad,lim);
        setAdapter();
        initScrollListener();
    }

    private void initScrollListener() {
        rvUserItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == userList.size() - 1) {
                        //bottom of list!
                        loadMore();

                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
            pbLoading.setVisibility(View.VISIBLE);
           Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    int scrollPosition = userList.size(); // 10
                    int currentSize = scrollPosition; // 10
                    int nextLimit = currentSize + 10; // 20

                    while (currentSize < nextLimit) { // 20<40
                        currentSize++;

                    }
                    itemLoad=currentSize;
                    callGetWs(itemLoad,lim);
                    Log.d("###",""+itemLoad);
                    pbLoading.setVisibility(View.GONE);
                    isLoading = false;

                }
            }, 2550);

        }


    private void setAdapter() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rvUserItem.setLayoutManager(linearLayoutManager);
        userItemAdapter=new UserItemAdapter(this,userList);
        rvUserItem.setAdapter(userItemAdapter);

    }

    private void callGetWs(int offcet,int lim) {
        Call<CreateResponse> responseCall=apiInterface.doGetListResour(offcet,lim);
        responseCall.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                CreateResponse createResponse=response.body();
                if(createResponse!=null){
                    list = createResponse.data.userArrayList;
                    if (list != null) {
                        populateData(list);

                    } else {
                        Log.d("###", "list is null");
                    }

                }
            }

            @Override
            public void onFailure(Call<CreateResponse> call, Throwable t) {

            }
        });
    }

    private void populateData(List<CreateResponse.User> list) {
        for (int i = 0; i < 10; i++) {
            if(list.size()!=0){
                CreateResponse.User user = list.get(i);
                userList.add(user);
                Log.d("###", "list seted");
            }
            else {
                Toast.makeText(this,"data ended",Toast.LENGTH_SHORT).show();
            }


        }
       userItemAdapter.notifyItemChanged(userItemAdapter.getItemCount() - 1);
    }

    private void init() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        userList = new ArrayList<>();
        rvUserItem = findViewById(R.id.rvUserItem);
        pbLoading=findViewById(R.id.pbLoading);
    }
}
