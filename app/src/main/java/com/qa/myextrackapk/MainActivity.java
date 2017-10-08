package com.qa.myextrackapk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private ListView mAppListView;
    List<AppDetail> mAppDetails;
    List<AppDetail> mBackUpAppDetails;

    AppAdapter mAdapter;
    String appPath = "";
    File fileDir;

    File app;
    File copy;
    Handler mHandler;
    Context mContext;

    SearchView mSearchView;
    AppAdapter searchAdapter;

    private MenuContextual menuContextual;
    private int _id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchApp = new ArrayList<>();
        menuContextual = new MenuContextual();

        mContext = this;
        appPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.app_name);
        fileDir = new File(appPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                Toast.makeText(this, "co loi tao thu muc", Toast.LENGTH_SHORT).show();
            }
        }

        mHandler = new Handler();

        mAppListView = (ListView) findViewById(R.id.listApp);
        mAppDetails = Utils.getAppDetails(this);
        mBackUpAppDetails = Utils.getAppDetails(this);

        mAdapter = new AppAdapter(this, mAppDetails);
        mAppListView.setAdapter(mAdapter);

        mAppListView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);

        setupSearchView(item);
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (menuContextual == null) {
            menuContextual = new MenuContextual();
        }
        this.startActionMode(menuContextual);
        AppDetail appDetail = mAppDetails.get(position);
        _id = position;

        app = new File(appDetail.path);
        copy = new File(appPath + File.separator + appDetail.appName + ".apk");
        view.setSelected(true);
        //showConfirm(app, copy);

    }

    private boolean isOK = true;

    private void copyFile(final File src, final File dst) {


        final ProgressDialog dialog = ProgressDialog.show(mContext, "", "copying");
        new Thread() {
            @Override
            public void run() {
                super.run();

                Looper.prepare();
                try {
                    isOK = true;
                    FileUtils.copyFile(src, dst);
                } catch (IOException e) {
                    isOK = false;
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (isOK)
                            Toast.makeText(mContext, "OK copy", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "co loi copy", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();

    }

    private void showConfirm(final File src, final File dst) {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);

        Utils.showDialog(mContext, "Confirm", "Do you want to get apk of this ?", drawable, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copyFile(src, dst);
            }
        }
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shareApp(src);
            }
        });
    }

    private void shareApp(File src) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.fromFile(src);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Please select"));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    List<AppDetail> mSearchApp;

    @Override
    public boolean onQueryTextChange(String s) {

        mAppDetails.clear();
        if (TextUtils.isEmpty(s) || s.equals("")) {

            //Collections.copy(mAppDetails, mBackUpAppDetails);
            mAppDetails = new ArrayList<>(mBackUpAppDetails);
            mAdapter = new AppAdapter(this, mAppDetails);
            mAppListView.setAdapter(mAdapter);
            // mAppListView.setAdapter(mAdapter);

        } else {

            Toast.makeText(this, "data " + s, Toast.LENGTH_SHORT).show();
            for (AppDetail detail : mBackUpAppDetails) {

                if (detail.appName.toLowerCase().contains(s)) {
                    AppDetail appDetail = new AppDetail(detail.appName, detail.appSize, detail.appIcon, detail.path, detail.pkg);
                    mAppDetails.add(appDetail);
                }
            }

//            searchAdapter = new AppAdapter(this,mSearchApp);
//            mAppDetails = mSearchApp;
//            mAppListView.setAdapter(searchAdapter);
            mAdapter.notifyDataSetChanged();
        }


        return true;
    }

    private class MenuContextual implements ActionMode.Callback {

        public MenuContextual() {
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_about, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            AppDetail detail = mAppDetails.get(_id);

            switch (menuItem.getItemId()) {
                case R.id.action_launch:
                    Intent intent = new Intent(getPackageManager().getLaunchIntentForPackage(detail.pkg));
                    startActivity(intent);
                    menuContextual = null;
                    break;

                case R.id.action_share:
                    shareApp(app);
                    menuContextual = null;
                    break;

//                case R.id.action_uninstall:
//                    Uri uri = Uri.fromParts("package", detail.pkg, null);
//                    intent = new Intent(Intent.ACTION_DELETE, uri);
//                    startActivityForResult(intent, 200);
//                    break;

                case R.id.action_extract:
                    copyFile(app, copy);
                    menuContextual = null;
                    break;

                default:
                    break;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            menuContextual = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
//            mAppDetails.remove(_id);
//            mAdapter.notifyDataSetChanged();

//            mBackUpAppDetails = Utils.getAppDetails(this);
//
//            mAdapter = new AppAdapter(this, mAppDetails);
//            mAppListView.setAdapter(mAdapter);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
