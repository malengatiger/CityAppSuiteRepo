package com.boha.citizenapp.msunduzi.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.citizenapp.msunduzi.R;
import com.boha.library.activities.CityApplication;
import com.boha.library.dto.AccountDTO;
import com.boha.library.dto.GcmDeviceDTO;
import com.boha.library.dto.MunicipalityDTO;
import com.boha.library.dto.ProfileInfoDTO;
import com.boha.library.dto.UserDTO;
import com.boha.library.transfer.RequestDTO;
import com.boha.library.transfer.ResponseDTO;
import com.boha.library.util.CacheUtil;
import com.boha.library.util.GCMUtil;
import com.boha.library.util.NetUtil;
import com.boha.library.util.SharedUtil;
import com.boha.library.util.ThemeChooser;
import com.boha.library.util.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SigninActivity extends AppCompatActivity {

    ImageView heroImage;
    Timer timer;
    TextView txtWelcome;
    View handle, editView;
    RadioButton radioYes, radioNo;
    Context ctx;
    ProgressBar progressBar;
    Activity activity;
    Button btnSend;
    EditText editID, editPassword;
    static final String LOG = SigninActivity.class.getSimpleName();
    ResponseDTO response;
    ProfileInfoDTO profileInfo;
    Spinner spinner;
    GcmDeviceDTO gcmDevice;
    MunicipalityDTO municipality;
//3301045068086 mavis1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "#### onCreate");
        ThemeChooser.setTheme(this);
        setContentView(R.layout.activity_signin);
        ctx = getApplicationContext();
        activity = this;

        municipality = SharedUtil.getMunicipality(ctx);
        int logo = getIntent().getIntExtra("logo", R.drawable.ic_action_globe);
        registerGCMDevice();

        setFields();
        getEmail();

        ActionBar actionBar = getSupportActionBar();
        Util.setCustomActionBar(ctx,
                actionBar,
                municipality.getMunicipalityName(),
                ctx.getResources().getDrawable(R.drawable.logo), logo);
        getSupportActionBar().setTitle("");
        //Track Signin
        CityApplication ca = (CityApplication) getApplication();
        Tracker t = ca.getTracker(
                CityApplication.TrackerName.APP_TRACKER);
        t.setScreenName(SigninActivity.class.getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());
        //
    }

    @Override
    public void onResume() {
        Log.w(LOG, "##### onResume");
        super.onResume();
    }


    private void setFields() {
        editView = findViewById(R.id.SIGNIN_editLayout);
        editView.setVisibility(View.GONE);
        radioNo = (RadioButton)findViewById(R.id.SIGNIN_radioNo);
        radioYes = (RadioButton)findViewById(R.id.SIGNIN_radioYes);
        btnSend = (Button) findViewById(R.id.SIGNIN_btnSignin);
        editID = (EditText) findViewById(R.id.SIGNIN_editUserID);
        spinner = (Spinner)findViewById(R.id.SIGNIN_emailSpinner);

        editPassword = (EditText) findViewById(R.id.SIGNIN_editPIN);
        progressBar = (ProgressBar) findViewById(R.id.SIGNIN_progress);
        heroImage = (ImageView) findViewById(R.id.SIGNIN_heroImage);
        txtWelcome = (TextView) findViewById(R.id.SIGNIN_welcome);
        handle = findViewById(R.id.SIGNIN_handle);
        progressBar.setVisibility(View.GONE);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnSend, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (radioNo.isChecked()) {
                            sendSignInUser();
                        } else {
                            sendSignInCitizen();
                        }
                    }
                });
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        heroImage.setImageDrawable(SplashActivity.getImage(0));
                    }
                });

            }
        }, 1000, 5000);

        radioNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFieldsUser();
                }
            }
        });
        radioYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFieldsCitizen();
                }
            }
        });

    }

    private void setFieldsCitizen() {
        Util.expand(editView, 500, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {
                spinner.setVisibility(View.GONE);
                //todo - remove after testing
//                editID.setText("3301045068086");
//                editPassword.setText("mavis1");
            }
        });
    }
    private void setFieldsUser() {
        Util.collapse(editView, 500, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {
                spinner.setVisibility(View.VISIBLE);
            }
        });
    }
    private void makeFakeData() {
        ResponseDTO w = new ResponseDTO();

        ProfileInfoDTO a = new ProfileInfoDTO();
        a.setAccountList(new ArrayList<AccountDTO>());
        a.setFirstName("Africa");
        a.setLastName("Tau");
        a.setPassword("tau1");
        a.setMunicipalityID(3);
        a.setiDNumber("123456789");

        AccountDTO x = new AccountDTO();
        x.setAccountID(1);
        x.setAccountNumber("123456-34");
        x.setBillDay(25);
        x.setCurrentArrears(1500.56);
        x.setCurrentBalance(5467.87);
        x.setDateLastUpdated(new Date().getTime());

        DateTime dt = new DateTime();
        DateTime then = dt.plusMonths(1);
        x.setNextBillDate(then.toDate().getTime());
        x.setPreviousBillDate(dt.minusMonths(1).toDate().getTime());
        a.getAccountList().add(x);



    }
    public void sendSignInCitizen() {

        if (editID.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_id));
            return;
        }
        if (editPassword.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_pswd));
            return;
        }
        if (email == null) {
            if (tarList.size() > 1) {
                email = tarList.get(1);
            }
        }


        RequestDTO w = new RequestDTO(RequestDTO.SIGN_IN_CITIZEN);
        w.setUserName(editID.getText().toString());
        w.setPassword(editPassword.getText().toString());
        w.setEmail(email);
        w.setGcmDevice(gcmDevice);
        w.setMunicipalityID(municipality.getMunicipalityID());

        setRefreshActionButtonState(true);
        NetUtil.sendRequest(ctx, w, new NetUtil.NetUtilListener() {
            @Override
            public void onResponse(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (resp.isMunicipalityAccessFailed()) {
                            if (resp.getProfileInfoList() == null || resp.getProfileInfoList().isEmpty()) {
                                Util.showErrorToast(ctx,getString(R.string.services_not_available));
                                finish();
                                return;
                            } else {
                                Util.showErrorToast(ctx,getString(com.boha.library.R.string.unable_connect_muni));
                            }
                        }
                        response = resp;
                        if (response.getProfileInfoList() != null && !response.getProfileInfoList().isEmpty()) {
                            profileInfo = response.getProfileInfoList().get(0);

                            ProfileInfoDTO sp = new ProfileInfoDTO();
                            sp.setProfileInfoID(profileInfo.getProfileInfoID());
                            sp.setFirstName(profileInfo.getFirstName());
                            sp.setLastName(profileInfo.getLastName());
                            sp.setiDNumber(profileInfo.getiDNumber());
                            sp.setPassword(profileInfo.getPassword());

                            SharedUtil.saveProfile(ctx, sp);
                            CacheUtil.cacheLoginData(ctx, response, new CacheUtil.CacheListener() {
                                @Override
                                public void onDataCached() {
                                    onBackPressed();
                                }

                                @Override
                                public void onError() {
                                    Util.showErrorToast(ctx, "Problem saving data");
                                }
                            });
                        } else {
                            Gson gson = new Gson();
                            Log.e(LOG,"-- sendSignInCitizen - some kind of error, json from server: " + gson.toJson(resp));
                        }

                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }

            @Override
            public void onWebSocketClose() {

            }
        });
    }
    public void sendSignInUser() {

        if (email == null) {
            if (tarList.size() > 1) {
                email = tarList.get(1);
            }
        }

        RequestDTO w = new RequestDTO(RequestDTO.SIGN_IN_USER);
        UserDTO u = new UserDTO();
        u.setMunicipalityID(municipality.getMunicipalityID());
        u.setEmail(email);
        u.setGcmDevice(gcmDevice);
        w.setUser(u);
        w.setMunicipalityID(municipality.getMunicipalityID());

        setRefreshActionButtonState(true);
        NetUtil.sendRequest(ctx, w, new NetUtil.NetUtilListener() {
            @Override
            public void onResponse(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (resp.isMunicipalityAccessFailed()) {
                            Util.showErrorToast(ctx,ctx.getString(com.boha.library.R.string.unable_connect_muni));
                            if (response.getUserList() == null || response.getUserList().isEmpty()) {
                                return;
                            }
                        }
                        response = resp;

                        SharedUtil.saveUser(ctx, response.getUserList().get(0));
                        CacheUtil.cacheLoginData(ctx, response, new CacheUtil.CacheListener() {
                            @Override
                            public void onDataCached() {
                                onBackPressed();
                            }

                            @Override
                            public void onError() {
                                Util.showErrorToast(ctx, "Problem saving data");
                            }
                        });

                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }

            @Override
            public void onWebSocketClose() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.w(LOG, "########### onBackPressed");
        profileInfo = SharedUtil.getProfile(ctx);
        UserDTO user = SharedUtil.getUser(ctx);
        if (profileInfo != null || user != null) {
            setResult(RESULT_OK);
            finish();
            Intent i = new Intent(ctx, MainDrawerActivity.class);
            startActivity(i);
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }

    }
    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Util.showErrorToast(ctx, "No Accounts found. Please create one and try again");
            finish();
            return;
        }
        if (accts != null) {
            if (accts.length == 1) {
                email = accts[0].name;
                spinner.setVisibility(View.GONE);
                return;
            }
            tarList.add("Select account for communications");
            for (int i = 0; i < accts.length; i++) {
                tarList.add(accts[i].name);
            }
            setSpinner();

        }

    }

    ArrayList<String> tarList = new ArrayList<String>();
    String email;
    Menu mMenu;

    private void setSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, tarList);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    email = null;
                } else {
                    email = tarList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_pager, menu);
        menu.getItem(0).setVisible(false);
        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Util.showToast(ctx,getString(R.string.under_cons));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    private void registerGCMDevice() {

        Log.e(LOG, "############# Starting Google Cloud Messaging registration");
        GCMUtil.startGCMRegistration(ctx, new GCMUtil.GCMUtilListener() {
            @Override
            public void onDeviceRegistered(String id) {
                Log.e(LOG, "############# GCM - we cool, cool.....: " + id);
                gcmDevice = new GcmDeviceDTO();
                gcmDevice.setManufacturer(Build.MANUFACTURER);
                gcmDevice.setModel(Build.MODEL);
                gcmDevice.setSerialNumber(Build.SERIAL);
                gcmDevice.setAndroidVersion(Build.VERSION.RELEASE);
                gcmDevice.setGcmRegistrationID(id);

            }

            @Override
            public void onGCMError() {
                Log.e(LOG, "############# onGCMError --- we got GCM problems");

            }
        });

    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(com.boha.library.R.id.action_help);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(com.boha.library.R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
//static final String url = "http://41.160.126.146/esbapi/V2/userlogin?username=7406190168080&password=vatawa"
//        + "&latitude=-29.859701442126745&longitude=31.014404296875 ";