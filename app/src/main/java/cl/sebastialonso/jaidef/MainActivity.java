package cl.sebastialonso.jaidef;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //For dealing with the cards
    private Context mContext;
    private RecyclerView mRecyclerView;
    private static final String TAG = "MyActivity";
    private List<Post> mPosts;
    FontContainer mFonts = new FontContainer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //Hide default title

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(mFonts.lightComfortaa());

        //Set the context
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mPosts = new ArrayList<>();
        //First load of posts
        populateWithPosts();
    }

    public void setPostAdapter(){
        PostAdapter mPostAdapter = new PostAdapter(mPosts, mContext);
        mRecyclerView.setAdapter(mPostAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //Hide default title

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(mFonts.lightComfortaa());
    }

    private void populateWithPosts() {
        //Llamar con Vollet a gg.jaidefinichon.com
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://gg.jaidefinichon.com";

        //Request a string response from the provided URL
        StringRequest stringRequest =  new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hereTakePosts(response);
                        setPostAdapter();
                        Snackbar.make(findViewById(android.R.id.content), "Post cargados", Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Snackbar.make(findViewById(android.R.id.content), "Ocurrió un error. " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

        //Make that fucking call
        queue.add(stringRequest);

    }

    public void hereTakePosts(String response) {
        Document doc = Jsoup.parse(response);
        Element content = doc.select("div#poto > div#content").first();
        Elements posts = content.getElementsByClass("post");

        for (Element currentPost : posts) {
            //Log.d(TAG, "post actual");
            //Log.d(TAG, currentPost.toString());
            //Log.d(TAG, "buscando elemento img-wrap. Es vacio?" + currentPost.getElementsByClass("img-wrap").isEmpty());
            //Log.d(TAG, "buscando elemento media. Es vacio?" + currentPost.getElementsByClass("media").isEmpty());
            //Hasta el momento existen 4 casos distintos
            /*
               .img-wrap > .media -> imagen JPEG o PNG o GIF
               .media > .html_photoset > iframe src=URL con mas fotos -> Photoset
            */
            if (!currentPost.getElementsByClass("img-wrap").isEmpty()) {
                Post newPost = new Post("Titulo", "descripcion", currentPost.getElementsByClass("media").select("img").first().absUrl("src"), "image");
                mPosts.add(newPost);
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
