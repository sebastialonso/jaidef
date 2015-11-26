package cl.sebastialonso.jaidef;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by seba on 10/7/15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private static final String TAG = "MyActivity";
    private List<Post> mPostList;
    private Context mContext;
    private static final int VIEW_TYPE_FOOTER  = 0;
    private static final int VIEW_TYPE_CELL = 1;
    private int currentPage = 2;

    public PostAdapter(List<Post> posts,Context context) {
        this.mPostList = posts;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        Button mButton;
        public TextView vTitle;
        public TextView vSubtitle;
        public ImageView mImage;

        public ViewHolder(View itemView, int viewType){
            super(itemView);
            if (viewType == VIEW_TYPE_FOOTER){
                mButton = (Button) itemView.findViewById(R.id.more_posts);
            }
            else if (viewType == VIEW_TYPE_CELL){
                mCardView =  (CardView)itemView.findViewById(R.id.cardview);
                //Image
                mImage = (ImageView) itemView.findViewById(R.id.post_pic);
                //Otros elementos
                //vTitle = (TextView) itemView.findViewById(R.id.post_title);
                //vSubtitle = (TextView) itemView.findViewById(R.id.post_description);
            }

        }
    }

    @Override
    public int getItemCount(){
        return mPostList.size() + 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        if (position < mPostList.size()){
            Post post = mPostList.get(position);
            //postViewHolder.vTitle.setText(post.title);
            //postViewHolder.vSubtitle.setText(post.description);
            switch (post.type){
                case "image":
                    Ion.with(viewHolder.mImage)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .load(post.imageUrl);
                    break;
                default:
                    Log.d(TAG, "DEFAULT");
                    break;
            }
        }
        else {
            viewHolder.mButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                        fetchNextPage(currentPage);
                     currentPage++;
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View mViewItem;
        ViewHolder mViewHolder;
        if (viewType == VIEW_TYPE_CELL){
            mViewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
            mViewHolder = new ViewHolder(mViewItem, viewType);
            return mViewHolder;
        } else {
            mViewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_footer_layout, viewGroup, false);
            mViewHolder = new ViewHolder(mViewItem, viewType);
            return  mViewHolder;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position){
        return (position == mPostList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public void fetchNextPage(int pageNumber){
        final ProgressDialog loading = new ProgressDialog(mContext);
        loading.setIndeterminate(true);
        loading.setTitle("Trayendo más cáncer...");
        loading.setMessage("Esperate un poco, por favor");
        loading.show();
        String url = "http://gg.jaidefinichon.com/page/" + String.valueOf(pageNumber);
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Feed the posts to mPostList
                        loading.dismiss();
                        hereTakePost(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        loading.dismiss();
                    }
                });

        //make the call
        queue.add(stringRequest);
    };

    public void hereTakePost(String response){
        Document doc = Jsoup.parse(response);
        Element content = doc.select("div#poto > div#content").first();
        Elements rawPosts = content.getElementsByClass("post");

        for (Element currentPost: rawPosts ) {
            if (!currentPost.getElementsByClass("img-wrap").isEmpty()) {
                Post newPost = new Post("Titulo", "descripcion", currentPost.getElementsByClass("media").select("img").first().absUrl("src"), "image");
                mPostList.add(newPost);
                notifyDataSetChanged();
            } /*else if (!currentPost.getElementsByClass("media").isEmpty()) {
                                Log.d(TAG, "El elemento es un video con URL " + currentPost.getElementsByClass("media").select("iframe").first().absUrl("src"));
                                Post newPost = new Post("Video", "descripcion de video", currentPost.getElementsByClass("media").select("iframe").first().absUrl("src"), "video");
                                mPosts.add(newPost);
                            }*/
            else {
                Log.d(TAG, "" + currentPost.nextElementSibling().toString());
            }
        }
    }
}