package algonquin.cst2335_yikaiwang_finalproject.nasaphotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

/**
 * An Singleton Object of Volley
 * @see <a href=https://developer.android.com/training/volley/requestqueue?hl=zh-cn>Volley set
 *     request queue</a>
 * @author developer.android.com
 * @since 1.0
 * @version 1.0
 */
public class VolleySingleton {
    private static VolleySingleton instance;
    private static Context ctx;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader =
                new ImageLoader(
                        requestQueue,
                        new ImageCache() {
                            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                            @Override
                            public Bitmap getBitmap(String url) {
                                return cache.get(url);
                            }

                            @Override
                            public void putBitmap(String url, Bitmap bitmap) {
                                cache.put(url, bitmap);
                            }
                        });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}