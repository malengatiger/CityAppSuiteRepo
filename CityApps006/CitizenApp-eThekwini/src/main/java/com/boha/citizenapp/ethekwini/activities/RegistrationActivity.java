package com.boha.citizenapp.ethekwini.activities;

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

import com.boha.citizenapp.ethekwini.R;
import com.boha.library.activities.CityApplication;
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
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;


public class RegistrationActivity extends AppCompatActivity {

    ImageView heroImage, logo;
    Timer timer;
    TextView txtWelcome;
    View handle, editView;
    Context ctx;
    ProgressBar progressBar;
    Activity activity;
    Button btnSend;
    EditText editID, editPassword, editFirstName, editLastName;
    static final String LOG = SigninActivity.class.getSimpleName();
    ResponseDTO response;
    ProfileInfoDTO profileInfo;
    Spinner spinner;
    GcmDeviceDTO gcmDevice;
    MunicipalityDTO municipality;
    RadioButton radioYes, radioNo;
    public static final int
            ONE_SECOND = 1000,
            FIVE_SECONDS = ONE_SECOND * 5,
            QUICK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "#### onCreate");
        ThemeChooser.setTheme(this);
        setContentView(R.layout.activity_registration);
        ctx = getApplicationContext();
        activity = this;

        municipality = SharedUtil.getMunicipality(ctx);
        int logo = getIntent().getIntExtra("logo", R.drawable.ic_action_globe);
        registerGCMDevice();

        setFields();
        getEmail();

        //Track RegistrationActivity
//        CityApplication ca = (CityApplication) getApplication();
//        Tracker t = ca.getTracker(
//                CityApplication.TrackerName.APP_TRACKER);
//        t.setScreenName(RegistrationActivity.class.getSimpleName());
//        t.send(new HitBuilders.ScreenViewBuilder().build());
        //

        ActionBar actionBar = getSupportActionBar();
        Util.setCustomActionBar(ctx,
                actionBar,
                municipality.getMunicipalityName(),
                ctx.getResources().getDrawable(R.drawable.logo), logo);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void setFields() {
        editView = findViewById(R.id.REG_editLayout);
        editView.setVisibility(View.GONE);
        radioNo = (RadioButton) findViewById(R.id.REG_radioNo);
        radioYes = (RadioButton) findViewById(R.id.REG_radioYes);
        btnSend = (Button) findViewById(R.id.REG_btnSignin);
        editID = (EditText) findViewById(R.id.REG_editUserID);
        editFirstName = (EditText) findViewById(R.id.REG_editFirstName);
        editLastName = (EditText) findViewById(R.id.REG_editLastName);
        spinner = (Spinner) findViewById(R.id.REG_emailSpinner);

        editPassword = (EditText) findViewById(R.id.REG_editPIN);
        progressBar = (ProgressBar) findViewById(R.id.REG_progress);
        heroImage = (ImageView) findViewById(R.id.REG_heroImage);
        txtWelcome = (TextView) findViewById(R.id.REG_welcome);
        handle = findViewById(R.id.REG_handle);
        progressBar.setVisibility(View.GONE);

        int h = Util.getWindowHeight(this);
        Log.w(LOG, "## window height: " + h);
        heroImage.getLayoutParams().height = h / 3;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnSend, QUICK, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (radioNo.isChecked()) {
                            sendUserRegistration();
                        } else {
                            sendCitizenRegistration();
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
        }, ONE_SECOND / 2, FIVE_SECONDS * 2);

        radioYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFieldsForCitizen();
                }
            }
        });
        radioNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Util.collapse(editView, 1000, new Util.UtilAnimationListener() {
                        @Override
                        public void onAnimationEnded() {
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }

    Random RANDOM = new Random(System.currentTimeMillis());
    private void setFieldsForCitizen() {
        editFirstName.setHint("Enter FirstName (Optional)");
        editLastName.setHint("Enter LastName (Optional)");
        editID.setHint("Enter ID Number");
        editPassword.setHint("Enter Password");
        editID.setVisibility(View.VISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        Util.expand(editView, 1000, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {
                spinner.setVisibility(View.GONE);
            }
        });
    }

    UserDTO user;

    private void sendUserRegistration() {

        if (email == null) {
            Util.showErrorToast(ctx, getString(R.string.select_email));
            return;
        }

        RequestDTO w = new RequestDTO(RequestDTO.REGISTER_USER);
        UserDTO p = new UserDTO();
        p.setMunicipalityID(SharedUtil.getMunicipality(ctx).getMunicipalityID());
        w.setMunicipalityID(p.getMunicipalityID());
        p.setEmail(email);
        p.setGcmDevice(gcmDevice);

        w.setUser(p);

        progressBar.setVisibility(View.VISIBLE);
        NetUtil.sendRequest(ctx, w, new NetUtil.NetUtilListener() {
            @Override
            public void onResponse(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (resp.getStatusCode() > 0) {
                            Util.showErrorToast(ctx, resp.getMessage());
                            return;
                        }
                        response = resp;
                        user = response.getUserList().get(0);
                        SharedUtil.saveUser(ctx, user);
                        CacheUtil.cacheLoginData(ctx, response, new CacheUtil.CacheListener() {
                            @Override
                            public void onDataCached()  {
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        Intent i = new Intent(ctx, CitizenDrawerActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }

            @Override
            public void onWebSocketClose() {

            }
        });
    }

    private void sendCitizenRegistration() {

        int x = 8;
        if (x == 8) {
            Util.showToast(ctx, ctx.getString(R.string.under_cons));
            return;
        }
        if (editID.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_id));
            return;
        }
        if (editPassword.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_pswd));
            return;
        }
        if (editFirstName.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_fname));
            return;
        }
        if (editLastName.getText().toString().isEmpty()) {
            Util.showErrorToast(ctx, getString(R.string.enter_lname));
            return;
        }
        if (email == null) {
            Util.showErrorToast(ctx, getString(R.string.select_email));
            return;
        }

        RequestDTO w = new RequestDTO(RequestDTO.REGISTER_CITIZEN);
        ProfileInfoDTO p = new ProfileInfoDTO();
        p.setFirstName(editFirstName.getText().toString());
        p.setLastName(editLastName.getText().toString());
        p.setMunicipalityID(municipality.getMunicipalityID());
        p.setiDNumber(editID.getText().toString());
        p.setPassword(editPassword.getText().toString());
        p.setEmail(email);
        p.setGcmDeviceList(new ArrayList<GcmDeviceDTO>());
        p.getGcmDeviceList().add(gcmDevice);

        w.setProfileInfo(p);

        progressBar.setVisibility(View.VISIBLE);
        NetUtil.sendRequest(ctx, w, new NetUtil.NetUtilListener() {
            @Override
            public void onResponse(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (resp.getStatusCode() > 0) {
                            Util.showErrorToast(ctx, resp.getMessage());
                            return;
                        }
                        response = resp;
                        Log.i(LOG, "### response OK from server");
                        profileInfo = response.getProfileInfoList().get(0);

                        ProfileInfoDTO sp = new ProfileInfoDTO();
                        sp.setProfileInfoID(profileInfo.getProfileInfoID());
                        sp.setFirstName(profileInfo.getFirstName());
                        sp.setLastName(profileInfo.getLastName());

                        SharedUtil.saveProfile(ctx, sp);
                        CacheUtil.cacheLoginData(ctx, response, new CacheUtil.CacheListener() {
                            @Override
                            public void onDataCached()  {
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        Intent i = new Intent(ctx, CitizenDrawerActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showErrorToast(ctx, message);
                    }
                });
            }

            @Override
            public void onWebSocketClose() {

            }
        });
    }

    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccountsByType("com.google");
        if (accts.length == 0) {
            Util.showErrorToast(ctx, getString(R.string.no_accts));
            finish();
            return;
        }
        if (accts != null) {
            tarList.add(getString(R.string.select_email));
            for (int i = 0; i < accts.length; i++) {
                tarList.add(accts[i].name);
            }
            setSpinner();

        }

    }

    ArrayList<String> tarList = new ArrayList<String>();
    String email;

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
        //getMenuInflater().inflate(R.menu.menu_signin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
    /*    if (id == R.id.action_settings) {
            return true;
        }*/

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
}
//static final String url = "http://41.160.126.146/esbapi/V2/userlogin?username=7406190168080&password=vatawa"
//        + "&latitude=-29.859701442126745&longitude=31.014404296875 ";