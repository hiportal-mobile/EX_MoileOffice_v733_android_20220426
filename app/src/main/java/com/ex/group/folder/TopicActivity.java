package com.ex.group.folder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ex.group.folder.dialog.CommonDialog_oneButton;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.TopicListInfo;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.dateCalculater;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicActivity extends BaseActivity {

    private ImageView button_prev;
    private RecyclerView topic_list;
    private TopicAdapter topicAdapter;
    private static String TAG ="==TopicActivity==";
    private static String TAG(String men){return TAG+"("+men+")";}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        setInitUI();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewsList();
    }

    public void setInitUI(){

        button_prev=findViewById(R.id.fab_back);
        topic_list=findViewById(R.id.topic_list);
        topic_list.setLayoutManager(new LinearLayoutManager(TopicActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(TopicActivity.this,new LinearLayoutManager(TopicActivity.this).getOrientation());
        topic_list.addItemDecoration(dividerItemDecoration);
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }
    public void getNewsList(){

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        /*final Call<RequestMailCount> requestMailCountCall = apiInterface.request_unread_mail_count(getSharedString("USERID"), "A");
        requestMailCountCall.enqueue(new Callback<RequestMailCount>()*/
        final Call<TopicListInfo> topicListInfoCall =apiInterface.topic_list_info(getSharedString("USERID"));
       topicListInfoCall.enqueue(new Callback<TopicListInfo>() {
           @Override
           public void onResponse(Call<TopicListInfo> call, Response<TopicListInfo> response) {


               if(response.body().resultMsg.equals("OK")) {
                   ArrayList<TopicListInfo.NewsList> newslist = new ArrayList<TopicListInfo.NewsList>();
                   for (int i = 0; i < response.body().newsList.size(); i++) {

                       newslist.add(response.body().newsList.get(i));
                   }

                   Log.e(TAG("newslistSIZE"), String.valueOf(newslist.size()));

                   topicAdapter = new TopicAdapter(newslist);
                   topic_list.setAdapter(topicAdapter);
               }else{
                   CommonDialog_oneButton cdo =  new CommonDialog_oneButton(TopicActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                   cdo.show();

               }
           }

           @Override
           public void onFailure(Call<TopicListInfo> call, Throwable t) {
               CommonDialog_oneButton cdo =  new CommonDialog_oneButton(TopicActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
               cdo.show();
           }
       });









    }
    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ItemRowHolder>{


        private ArrayList<TopicListInfo.NewsList> newsList;


        public TopicAdapter(ArrayList<TopicListInfo.NewsList> newslist){
            this.newsList = newslist;
        }


        @NonNull
        @Override
        public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_chart,null);
            ItemRowHolder myHolder = new ItemRowHolder(contentView);

            return myHolder;
        }

        @Override
        public void onBindViewHolder(ItemRowHolder holder, int position) {


            String title = newsList.get(position).getTitle();
            String listedTime = newsList.get(position).getServiceDt();
            String contentsURl = newsList.get(position).getContentsUrl();

            holder.txt_title.setText(title);
            holder.txt_name.setVisibility(View.INVISIBLE);
            holder.txt_list_time.setText(listedTime);
            holder.txt_url.setText(contentsURl);
            if(dateCalculater.isNew(newsList.get(position).getServiceDt())){
            holder.txt_content_new.setVisibility(View.VISIBLE);
            }else{
                holder.txt_content_new.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            protected TextView txt_title;
            protected TextView txt_name;
            protected TextView txt_list_time;
            protected ImageView txt_content_new;
            protected TextView txt_url;



            public ItemRowHolder(View view)
            {
                super(view);
                view.setClickable(true);
                itemView.setOnClickListener(this);
                this.txt_title=(TextView)view.findViewById(R.id.txt_title);
                this.txt_name=(TextView)view.findViewById(R.id.txt_name);
                this.txt_list_time=(TextView)view.findViewById(R.id.txt_list_time);
                this.txt_content_new=(ImageView)view.findViewById(R.id.txt_content_new);
                this.txt_url =(TextView)view.findViewById(R.id.txt_url);
            }

            @Override
            public void onClick(View v) {
                vibrate(15);
                    Intent intent = new Intent(TopicActivity.this, TopicWebViewActivity.class);
                    intent.putExtra("contentsUrl",this.txt_url.getText().toString());
                    startActivity(intent);

            }
        }
    }
}
