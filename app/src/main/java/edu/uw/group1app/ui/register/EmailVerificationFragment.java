package edu.uw.group1app.ui.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.group1app.R;

import edu.uw.group1app.databinding.FragmentEmailVerificationBinding;
import edu.uw.group1app.model.PushyTokenViewModel;
import edu.uw.group1app.model.UserInfoViewModel;

import edu.uw.group1app.ui.utils.PasswordValidator;

/**
 * A simple {@link Fragment} subclass.

 */
public class EmailVerificationFragment extends Fragment {

    public EmailVerificationFragment() {
        // Required empty public constructor
    }
    private FragmentEmailVerificationBinding binding;
    private EmailVerificationViewModel mEmailVerificationModel;
    private EmailVerificationFragmentArgs mArgs;

    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));

    private PasswordValidator mPassWordValidator = PasswordValidator.checkPwdLength(1)
            .and(PasswordValidator.checkExcludeWhiteSpace());


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmailVerificationModel = new ViewModelProvider(getActivity())
                .get(EmailVerificationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailVerificationBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmailVerificationModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeEmailVerificationResponse);


            mArgs = EmailVerificationFragmentArgs.fromBundle(getArguments());
    }



    private void verifyAuthWithServer() {
        mEmailVerificationModel.connect();
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Helper to abstract the navigation to the sign in fragment.
     */
    private void navigateToSignIn() {

        Navigation.findNavController(getView())
                .navigate(EmailVerificationFragmentDirections
                        .actionEmailVerificationFragmentToSignInFragment());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to EmailVerificationViewModel.
     *
     * @param response the Response from the server
     */
    private void observeEmailVerificationResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    Log.d("Error code received",
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToSignIn();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}