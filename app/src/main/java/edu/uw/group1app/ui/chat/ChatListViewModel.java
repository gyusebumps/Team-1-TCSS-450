package edu.uw.group1app.ui.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.group1app.R;
import edu.uw.group1app.io.RequestQueueSingleton;
import edu.uw.group1app.model.UserInfoViewModel;

public class ChatListViewModel extends AndroidViewModel {

    private MutableLiveData<List<ChatRoom>> mChatRoomList;
    private final MutableLiveData<JSONObject> mResponse;
    private UserInfoViewModel userInfoViewModel;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatRoomList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    public void setUserInfoViewModel(UserInfoViewModel theUserInfoViewModel) {
        userInfoViewModel = theUserInfoViewModel;
    }

    /**
     * An observer on the HTTP Response from the web server.
     *
     * @param owner    LifecycleOwner object.
     * @param observer Observer object of type List<ChatRoom>.
     */
    public void addChatListObserver(@NonNull LifecycleOwner owner, @NonNull Observer<? super List<ChatRoom>> observer) {
        mChatRoomList.observe(owner, observer);
    }

    public void connectGet(String jwt) {
        String url = "https://mobileapp-group-backend.herokuapp.com/chatrooms";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // no body
                this::handleSuccess,
                this::handleError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    private void handleSuccess(final JSONObject result) {
        ArrayList<ChatRoom> temp = new ArrayList<>();
        try {
            JSONArray chats = result.getJSONArray("chats");
            for (int i = 0; i < chats.length(); i++) {
                JSONObject chat = chats.getJSONObject(i);
                int chatid = chat.getInt("chat");
                String chatTitle = chat.getString("name");
                ChatRoom post = new ChatRoom(chatid, chatTitle);
                temp.add(post);
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mChatRoomList.setValue(temp);
    }

    /**
     * connect to the webservice and request for a chat deletion
     */
    public void deleteChat(final int chatId) {
        String url = "https://mobileapp-group-backend.herokuapp.com/chats/"
                + chatId + "/" + userInfoViewModel.getEmail();

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mResponse::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", userInfoViewModel.getmJwt());
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }

    public void addChat(final String jwt, final String name) {
        String url = getApplication().getResources().getString(R.string.base_url) + "chats";

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //push token found in the JSONObject body
                //mResponse::setValue,
                response -> handleAddChat(jwt, response),
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }

    private void handleAddChat(final String jwt, final JSONObject response) {
        ArrayList<ChatRoom> temp = new ArrayList<>();
        try {
            int chatID = response.getInt("chatID");
            putMembers(jwt, chatID);
            connectGet(jwt);
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }


    public void putMembers(final String jwt, int chatID) throws JSONException {
        String url = "https://mobileapp-group-backend.herokuapp.com/chats/" + chatID;
        System.out.println("Adding Members");
        JSONObject body = new JSONObject();
        try {
            body.put("chatid", chatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(body.toString());

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body, //push token found in the JSONObject body
                mResponse::setValue,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }


    /**
     * handle a failure connection to the back-end
     * @param error the error.
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "No Chat Info");
    }

}

