package edu.uw.group1app.ui.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.IntFunction;

public class ZipcodeViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mDetails;


    public ZipcodeViewModel(@NonNull Application application) {
        super(application);
        mDetails = new MutableLiveData<>();
        mDetails.setValue(new JSONObject());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mDetails.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());

        //throw new IllegalStateException(error.getMessage());
    }


    public void connect(final String zipCode){
        String url = "https://mobileapp-group-backend.herokuapp.com/zipcode";

        JSONObject body = new JSONObject();
        try{
            body.put("zipcode", zipCode);
        } catch (JSONException e){
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mDetails::setValue,
                this::handleError
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}
