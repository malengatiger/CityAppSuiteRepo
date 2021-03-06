package com.boha.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.library.R;
import com.boha.library.activities.AccountDetailActivity;
import com.boha.library.adapters.StatementRecyclerAdapter;
import com.boha.library.dto.AccountDTO;
import com.boha.library.transfer.RequestDTO;
import com.boha.library.transfer.ResponseDTO;
import com.boha.library.util.NetUtil;
import com.boha.library.util.SharedUtil;
import com.boha.library.util.Util;
import com.boha.library.util.WebCheck;
import com.boha.library.util.WebCheckResult;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.joda.time.DateTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StatementListFragment extends Fragment implements PageFragment {

    public interface StatementFragmentListener {
        void onPDFDownloaded(String fileName);

        void setBusy(boolean busy);
    }

    StatementFragmentListener statementFragmentListener;
    ResponseDTO response;
    TextView txtTitle, txtDate, txtCount, txtAccount, txtLabel;
    View view, fab, topView, handle;
    Context ctx;
    ImageView heroImage;
    Activity activity;
    String pageTitle;
    int primaryColor, darkColor, month, year, count;
    FragmentManager fragmentManager;
    List<AccountDTO> accountList;
    AccountDTO account;
    StatementRecyclerAdapter statementAdapter;
    RecyclerView recyclerView;
    FloatingActionButton faButton;

    public void setAccount(AccountDTO account) {
        Log.d(LOG, "### setAccount");
        this.account = account;
        txtAccount.setText(account.getAccountNumber());
        getCachedStatements();
    }


    static final String LOG = StatementListFragment.class.getSimpleName();

    public static StatementListFragment newInstance() {
        StatementListFragment fragment = new StatementListFragment();

        return fragment;
    }

    public StatementListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        if (getArguments() != null) {
//            response = (ResponseDTO) getArguments().getSerializable("response");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "### onCreateView");
        view = inflater.inflate(R.layout.fragment_statement, container, false);
        fragmentManager = getFragmentManager();
        ctx = getActivity();
        activity = getActivity();
        setFields();

        animateSomething();
        return view;
    }


    List<String> filePathList = new ArrayList<>();

    private void getCachedStatements() {

        File dir = Environment.getExternalStorageDirectory();
        File myDir = new File(dir, "smartCity");
        if (!myDir.exists()) {
            myDir.mkdir();
        }
        File[] files = myDir.listFiles();
        filePathList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(account.getAccountNumber())) {
                    if (file.getName().contains("statement.pdf")) {
                        filePathList.add(file.getAbsolutePath());
                    }
                }
            }
        }

        if (filePathList.isEmpty()) {
            Log.d(LOG, "getCachedStatements: filePathList is empty");
        } else {
            fileContainerList.clear();
            for (String d : filePathList) {
                fileContainerList.add(new FileContainer(d));
            }
            Collections.sort(fileContainerList);
            filePathList.clear();
            for (FileContainer f : fileContainerList) {
                filePathList.add(f.fileName);
            }
            setList();
        }
    }

    private void setList() {
        txtCount.setText("" + filePathList.size());
        String p = filePathList.get(0);
        try {
            Pattern pat = Pattern.compile("-");
            String[] keyParts = pat.split(p);
            String year = keyParts[1];
            String month = keyParts[2];

            DateTime dateTime = new DateTime(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
            txtDate.setText(sdf.format(dateTime.toDate()));
        } catch (Exception e) {
            txtDate.setText("Unavailable Date");
        }

        // suggest pdf reader: https://play.google.com/store/apps/details?id=com.adobe.reader
        statementAdapter = new StatementRecyclerAdapter(filePathList, getActivity(),
                new StatementRecyclerAdapter.StatementAdapterListener() {
            @Override
            public void onStatementClicked(int position) {
                if (busyDownloading) {
                    Util.showToast(ctx, "Still downloading statements, please wait");
                    return;
                }
                File file = new File(filePathList.get(position));
                Log.e(LOG, "pdf file: " + file.getAbsolutePath() + " length: " + file.length());
                if (file.exists()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                } else {
                    Util.showToast(ctx, "Statement is still downloading");
                }
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(statementAdapter);



    }

    private void setFields() {
        topView = view.findViewById(R.id.ST_top);
        handle = view.findViewById(R.id.ST_handle);
        txtDate = (TextView) view.findViewById(R.id.ST_subtitle);
        txtCount = (TextView) view.findViewById(R.id.ST_count);
        txtAccount = (TextView) view.findViewById(R.id.ST_account);
        heroImage = (ImageView) view.findViewById(R.id.ST_hero);
        heroImage.setVisibility(View.GONE);
        txtTitle = (TextView) view.findViewById(R.id.ST_title);
        recyclerView = (RecyclerView) view.findViewById(R.id.ST_list);
        txtLabel = (TextView) view.findViewById(R.id.ST_label);

         if (filePathList.size() == 0){
            txtLabel.setText(R.string.download_statement);
        } else  {
            txtLabel.setText(R.string.downloaded_statement);
        }

        txtDate.setText("Not Downloaded");
        txtCount.setText("0");
        faButton = (FloatingActionButton) view.findViewById(R.id.fab);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(faButton, 300, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        showDatePicker();
                    }
                });
            }
        });
        fab = view.findViewById(R.id.FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(fab, 300, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        showDatePicker();
                    }
                });
            }
        });
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(txtDate, 300, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        showDatePicker();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(activity);
        if (a instanceof StatementFragmentListener) {
            statementFragmentListener = (StatementFragmentListener) a;
        } else {
            throw new ClassCastException("Host activity " + a.getLocalClassName() + " must implement StatementFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = CityApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    @Override
    public void animateSomething() {
      /*  final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.cancel();
                        heroImage.setImageDrawable(Util.getRandomBackgroundImage(ctx));
                        Util.expand(heroImage, 1000, new Util.UtilAnimationListener() {
                            @Override
                            public void onAnimationEnded() {
                                Util.flashOnce(fab, 300, new Util.UtilAnimationListener() {
                                    @Override
                                    public void onAnimationEnded() {
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }, 500);*/
    }

    @Override
    public void setThemeColors(int primaryColor, int primaryDarkColor) {
        this.primaryColor = primaryColor;
        this.darkColor = primaryDarkColor;
        if (topView != null) {
            topView.setBackgroundColor(primaryColor);
        }
    }

    @Override
    public String getPageTitle() {
        return pageTitle;
    }

    @Override
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    boolean busyDownloading;

    public void getPDFStatement() {
        Log.i(LOG, "getPDFStatement.....");
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);
        if (!wcr.isWifiConnected() && !wcr.isMobileConnected()) {
            Util.showSnackBar(fab,"You are currently not connected to the network","OK", Color.parseColor("red"));
            //   Toast.makeText(SplashActivity.this,"You are currently not connected to the network",Toast.LENGTH_LONG).show();
            return;
        }
        if (busyDownloading) {
            return;
        }

        busyDownloading = true;
        snackbar = Util.showSnackBar(fab,"Municipality services downloading your "
                + year + ":" + month + " statement ...", "OK", Color.parseColor("cyan"));
        if (year == 0 || month == 0) {
            DateTime dateTime = new DateTime();
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear();
        }
        Log.d(LOG, "*** year: " + year
                + " month: " + month +  " count: " + count + " selected for statement download");

        RequestDTO w = new RequestDTO(RequestDTO.GET_PDF_STATEMENT);
        w.setAccountNumber(account.getAccountNumber());
        w.setYear(year);
        w.setMonth(month);
        w.setCount(count);
        w.setMunicipalityID(SharedUtil.getMunicipality(ctx).getMunicipalityID());

        statementFragmentListener.setBusy(true);
        disableFab();
        NetUtil.sendRequest(ctx, w, new NetUtil.NetUtilListener() {
            @Override
            public void onResponse(final ResponseDTO response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statementFragmentListener.setBusy(false);
                            busyDownloading = false;
                            snackbar.dismiss();
                            enableFab();
                            if (response.getStatusCode() == 0) {
                                Log.i(LOG, "+++ statement(s) from server, we cool, cool ...");

                                if (response.getPdfHashMap() != null && !response.getPdfHashMap().isEmpty()) {
                                    Log.i(LOG, "Statements found on server: " + response.getPdfHashMap().size());

                                    for (String key : response.getPdfHashMap().keySet()) {
                                        byte[] data = response.getPdfHashMap().get(key);
                                        savePDF(key, data);

                                    }
                                    setAnalyticsEvent("statement","StatementDownloaded");

                                } else {
                                    if (response.isMunicipalityAccessFailed()) {
                                        Util.showSnackBar(txtTitle, ctx.getString(R.string.unable_connect_muni),"OK",
                                                Color.parseColor("RED"));
                                    } else {
                                        FirebaseCrash.report(new Exception("No statements found for: " + year + ":" + month));
                                        Util.showSnackBar(txtTitle, "No statements found for the month selected","OK", Color.parseColor("YELLOW"));
                                    }
                                }
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(final String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            busyDownloading = false;
                            statementFragmentListener.setBusy(false);
                            FirebaseCrash.report(new Exception("Statement download failed: " + message));
                            enableFab();
                            snackbar = Util.showSnackBar(txtTitle, message, "OK", Color.parseColor("RED"));
                            Log.e(LOG, message);
                        }
                    });
                }
            }

            @Override
            public void onWebSocketClose() {

            }
        });
    }

    private void savePDF(String key, byte[] data) {
        Log.d(LOG, "savePDF: data length: " + data.length);
            File directory = Environment.getExternalStorageDirectory();
            File myDir = new File(directory, "smartCity");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            File pdfFile = new File(myDir, key + "-statement.pdf");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pdfFile));
                bos.write(data);
                bos.flush();
                bos.close();

                Log.w(LOG, "savePDF: monthly Statement pdf file: " + pdfFile.getAbsolutePath() + " written to disk" );
                getCachedStatements();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    FirebaseAnalytics mFirebaseAnalytics;
    private void setAnalyticsEvent(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);

        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.w(LOG,"analytics event sent .....");


    }
    Snackbar snackbar;

    private void enableFab() {
        fab.setAlpha(1.0f);
        fab.setEnabled(true);
        txtDate.setAlpha(1.0f);
        txtDate.setEnabled(true);
    }

    private void disableFab() {
        fab.setAlpha(0.4f);
        fab.setEnabled(false);
        txtDate.setAlpha(0.4f);
        txtDate.setEnabled(false);
    }

    private void showDatePicker() {

        MonthPickerDialogFragment monthPicker = new MonthPickerDialogFragment();
        monthPicker.setListener(new MonthPickerDialogFragment.MonthPickerListener() {
            @Override
            public void onDatePicked(int month, int year, int count) {

                StatementListFragment.this.count = count;
                StatementListFragment.this.year = year;
                StatementListFragment.this.month = month + 1;

                getPDFStatement();
                int index = 0;
                boolean found = false;
                final Pattern patt = Pattern.compile("-");

                for (String s : filePathList) {
                    if (s.contains(account.getAccountNumber())) {
                        String[] parts = patt.split(s);
                        int y = Integer.parseInt(parts[1]);
                        int m = Integer.parseInt(parts[2]);
                        if (y == year && m == month) {
                            found = true;
                            //
                            File file = new File(filePathList.get(index));
                            Log.e(LOG, "pdf file: " + file.getAbsolutePath() + " length: " + file.length());
                            if (file.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ctx.startActivity(intent);
                            }

                            break;
                        }

                    }
                    index++;
                }
                if (!found) {

                } else {
//                    recyclerView.setSelection(index);
                    Util.showSnackBar(txtAccount,ctx.getString(R.string.stmt_exists), "OK", Color.parseColor("BLUE"));
                }
            }
        });

        monthPicker.show(fragmentManager, "monthpicker");

    }


    static final Locale d = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", d);

    List<FileContainer> fileContainerList = new ArrayList<>();

    class FileContainer implements Comparable<FileContainer> {
        String fileName;
        Date date;

        public FileContainer(String filename) {
            this.fileName = filename;
            try {
                Pattern patt = Pattern.compile("-");
                String[] parts = patt.split(filename);

                DateTime dateTime = new DateTime(Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]), 1, 0, 0);
                date = dateTime.toDate();
            } catch (Exception e) {
                Log.e("FileContainer", "problem", e);
                date = new Date();
            }
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public int compareTo(FileContainer another) {
            if (this.date.before(another.date)) {
                return 1;
            }
            if (this.date.after(another.date)) {
                return -1;
            }
            return 0;
        }
    }
}
