package com.boha.library.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Nkululeko on 2016/08/28.
 */
public class AlertReadRss extends AsyncTask<Void,Void,Void>  {
    Context context;
    ProgressDialog progressDialog;
    String address = "http://icsmnewsdev.oneconnectgroup.com/et/alerts/json/Alerts.json";
    URL url;

    public ArrayList<FeedItem> feedItems;
    RecyclerView recyclerView;
    public AlertReadRss(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
    }

    @Override
    protected void onPreExecute() {
//        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

   //     progressDialog.dismiss();
        AlertReadRssAdapter adapter = new AlertReadRssAdapter(context, feedItems, new AlertReadRssAdapter.NewsListListener() {
            @Override
            public void onNewsClicked() {

            }
        });
        /*ReadRssAdapter adapter = new ReadRssAdapter(context, feedItems, new ReadRssAdapter.NewsListListener() {
            @Override
            public void onNewsClicked() {

            }


        });*/
        recyclerView.setAdapter(adapter);
      //  adapter.notifyDataSetChanged();
    }




    @Override
    protected Void doInBackground(Void... params) {
        ProcessXml(getData());
        return null;
    }
    public String latitude, longitude;

    private void ProcessXml(Document data){
        if (data != null) {
            feedItems = new ArrayList<>();

            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0;i<items.getLength(); i++) {
                Node currentChild = items.item(i);
                if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                    FeedItem item = new FeedItem();
                    NodeList itemChilds = currentChild.getChildNodes();
                    for (int j = 0; j<itemChilds.getLength(); j++) {
                        Node current = itemChilds.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            item.setTitle(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("description")) {
                            item.setDescription(current.getTextContent().replaceAll("\u2019", "'")
                                    .replaceAll("\u201C", "\"").replaceAll("\u201D", "\"")

                                    .replaceAll("\u2018", "'").replaceAll("\u2026", "...")

                                    .replaceAll("\u2013", "-").replaceAll("\u2022", "&#8226; "));

                        } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                            item.setPubDate(current.getTextContent());
                        }/* else  if (current.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(current.getTextContent());
                        }*/ else if (current.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                            String url = current.getAttributes().item(0).getTextContent();
                            item.setThumbnailUrl(url);
                        } else if (current.getNodeName().equalsIgnoreCase("georss:point")) {
                            item.setPoint(current.getTextContent());
                            String [] parts = current.getTextContent().split(" ");
                            longitude = parts[0].substring(0, parts[0].length());
                             latitude = parts[1].substring(0, parts[1].length());
                        } else if (current.getNodeName().equalsIgnoreCase("category")) {
                            item.setCategory(current.getTextContent());
                        }



                    }
                    feedItems.add(item);
                    Log.d("itemTitle", item.getTitle());
                    Log.d("itemDescription", item.getDescription());

                    /*if (item.getLink() != null || !item.getLink().isEmpty()) {

                        Log.d("itemLink", item.getLink());
                    }*/
                    Log.d("itemPubDate", item.getPubDate());
                    Log.d("itemThumbnailUrl", item.getThumbnailUrl());
                    if (item.getPoint() !=null){
                        Log.d("itemGeoRss", item.getPoint());
                    } else{
                        Log.d("itemGeoRss", "empty");
                    }

                    Log.i(LOG,  "Latitude: " + latitude);
                    Log.i(LOG, "Longitude: " + longitude);
                    Log.d("itemCategory", item.getCategory());
                }
            }
            //  Log.d("ReadRSS", data.getDocumentElement().getNodeName());
        }
        Log.i(LOG, "feed items: " + feedItems.size());
    }

    public Document getData() {
        try{
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final String LOG = AlertReadRss.class.getSimpleName();

}

