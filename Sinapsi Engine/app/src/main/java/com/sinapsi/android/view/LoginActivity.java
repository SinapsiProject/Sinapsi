package com.sinapsi.android.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.sinapsi.android.Lol;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.enginesystem.AndroidDeviceInfo;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.client.AppConsts;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.android.R;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.CommunicationInfo;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.User;
import com.sinapsi.utils.Pair;

import retrofit.RetrofitError;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends SinapsiActionBarActivity implements LoaderCallbacks<Cursor> {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private AndroidDeviceInfo adi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adi = new AndroidDeviceInfo(this);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    requestLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(AppConsts.DEBUG_TEST_CREDENTIALS) {
            mEmailView.setText(AppConsts.DEBUG_TEST_EMAIL);
            mPasswordView.setText(AppConsts.DEBUG_TEST_PASSWORD);
        }

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void requestLogin() {
        if (!isServiceConnected()) return;
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            // first, request login
            service.getWeb().requestLogin(email, adi.getDeviceName(), adi.getDeviceModel(), new SinapsiWebServiceFacade.WebServiceCallback<Pair<byte[], byte[]>>() {
                @Override
                public void success(Pair<byte[], byte[]> stringStringSimpleEntry, Object response) {
                    attemptLogin();
                }

                @Override
                public void failure(Throwable t) {
                    showProgress(false);
                    if (!(t instanceof RetrofitError)) {
                        Lol.d(LoginActivity.class, "User does not exist or is not active");
                        mEmailView.setError(getString(R.string.username_does_not_exist));
                        return;
                    }

                    DialogUtils.handleRetrofitError(t, LoginActivity.this, false);

                }
            });

        }
    }

    public void attemptLogin() {
        if (!isServiceConnected()) return;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        service.getWeb().login(email, password, adi.getDeviceName(), adi.getDeviceModel(), new SinapsiWebServiceFacade.WebServiceCallback<User>() {
            @Override
            public void success(User user, Object response) {
                if (user == null) {
                    showProgress(false);
                    Lol.d("LOGIN->SUCCESS", "USER IS NULL");
                    DialogUtils.showOkDialog(
                            LoginActivity.this,
                            "Login Error",
                            "There was an error while communicating with the server",
                            false);
                    return;
                }

                Lol.d(this, "Success! user id received: " + user.getId());
                registerDeviceAndComplete(user);

            }

            @Override
            public void failure(Throwable error) {

                showProgress(false);
                if (!(error instanceof RetrofitError)) {
                    RuntimeException re = (RuntimeException) error;
                    Lol.d(LoginActivity.class, "Error! Server reason:" + re.getMessage());
                    DialogUtils.showOkDialog(
                            LoginActivity.this,
                            "Login Error",
                            re.getMessage(),
                            false);
                    return;
                }

                DialogUtils.handleRetrofitError(error, LoginActivity.this, false);
            }
        });

    }

    public void registerDeviceAndComplete(UserInterface user){

        service.getWeb().registerDevice(
                user,
                user.getEmail(),
                adi.getDeviceName(),
                adi.getDeviceModel(),
                adi.getDeviceType(),
                adi.getVersion(),
                new SinapsiWebServiceFacade.WebServiceCallback<Device>() {
                    @Override
                    public void success(Device device, Object response) {

                        service.setDevice(device);
                        service.initEngine();
                        service.getWSClient().establishConnection();

                        service.getWeb().setAvailableComponents(
                                device,
                                service.getEngine().getAvailableTriggerDescriptors(),
                                service.getEngine().getAvailableActionDescriptors(),
                                new SinapsiWebServiceFacade.WebServiceCallback<CommunicationInfo>() {
                                    @Override
                                    public void success(CommunicationInfo communicationInfo, Object response) {
                                        if(communicationInfo.isErrorOccured()){
                                            failure(new RuntimeException(communicationInfo.getErrorDescription()));
                                            return;
                                        }
                                        service.syncMacrosAndStartEngine();
                                        startMainActivity();
                                        showProgress(false);
                                    }

                                    @Override
                                    public void failure(Throwable error) {
                                        if(!(error instanceof RetrofitError)){
                                            RuntimeException re = (RuntimeException) error;
                                            Lol.d(LoginActivity.class, "setAvailableComponents Error! Server reason: " + re.getMessage());
                                            DialogUtils.showOkDialog(
                                                    LoginActivity.this,
                                                    "Device components availability update error",
                                                    "Server message:\n" + re.getMessage(),
                                                    false);

                                            showProgress(false);
                                            return;
                                        }
                                        DialogUtils.handleRetrofitError(error, LoginActivity.this, false);
                                        showProgress(false);
                                    }
                                });


                    }

                    @Override
                    public void failure(Throwable error) {
                        if (!(error instanceof RetrofitError)) {
                            RuntimeException re = (RuntimeException) error;
                            Lol.d(LoginActivity.class, "RegisterDevice Error! Server reason: " + re.getMessage());
                            DialogUtils.showOkDialog(
                                    LoginActivity.this,
                                    "Device login error",
                                    re.getMessage(),
                                    false);
                            showProgress(false);
                            return;
                        }
                        DialogUtils.handleRetrofitError(error, LoginActivity.this, false);
                        showProgress(false);
                    }
                });
    }



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void startMainActivity(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}