package edu.sfsu.napkin.api;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * Created by leethomas on 10/30/15.
 */


/** Singleton class which holds a Volley RequestQueue **/
public class APIRequestQueue {
    private final int CACHE_SIZE = 1024 * 1024;
    private static RequestQueue requestQueue;
    private static APIRequestQueue self;

    /**
     * Constructor for APIRequestQueue
     * @param cacheDir The on-device cache directory to use.
     **/

    public APIRequestQueue(File cacheDir) {

        // I'm defining diskbasedcache & network objects here b/c
        // I don't think they'll ever change/we'll ever need to
        // configure them beyond this point. If we do though we can always declare
        // them outside of the constructor

        Cache cache = new DiskBasedCache(cacheDir, CACHE_SIZE);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
    }

    /**
     *
     * @return RequestQueue Get the Volley RequestQueue held by this class.
     */
    public RequestQueue getRequestQueue() {
        return self.requestQueue;
    }

    /**
     *
     * @param cacheDir Cache directory to use if instance is uninitialized
     * @return APIRequestQueue Return the singleton instance of APIRequestQueue
     */
    public static APIRequestQueue getInstance(File cacheDir) {
        if (self == null) {
            self = new APIRequestQueue(cacheDir);
        }

        return self;
    }
}
