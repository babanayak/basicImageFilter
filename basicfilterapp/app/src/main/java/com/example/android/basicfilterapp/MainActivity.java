package com.example.android.basicfilterapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.basicfilterapp.Adapter.ViewPagerAdapter;
import com.example.android.basicfilterapp.Interface.EditImageFragemntListener;
import com.example.android.basicfilterapp.Interface.FilterListFragmentListener;
import com.example.android.basicfilterapp.Utils.BitMapUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.SubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.security.acl.Permission;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements FilterListFragmentListener,EditImageFragemntListener{
    public static final String Picture_name="abcd.jpg";
    public static final int PERMISSION_PICK_IMAGE=1000;

    ImageView img_preview;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

     Bitmap originalBitmap,filteredBitmap,finalBitmap;
     FiltersListFragment filtersListFragment;
     EditImageFragment editImageFragment;

     int brightnessFinal=0;
     float saturationFinal=1.0f;
     float constrantFinal=1.0f;

     static {
         System.loadLibrary("NativeImageProcessor");
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Basic Filter");



        img_preview=(ImageView)findViewById(R.id.image_preview);
        tabLayout=(TabLayout) findViewById(R.id.tabs);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        coordinatorLayout=(CoordinatorLayout) findViewById(R.id.coordinate);

        loadImage();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);



    }

    private void loadImage() {
         originalBitmap= BitMapUtils.getBitmapFromAssets(this,Picture_name,300,300);
         filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
         finalBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
         img_preview.setImageBitmap(originalBitmap);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        filtersListFragment=new FiltersListFragment();
        filtersListFragment.setListener(this);
        editImageFragment =new EditImageFragment();
        editImageFragment.setListener(this);
        adapter.addFragment(filtersListFragment,"Filters");
        adapter.addFragment(editImageFragment,"Edit");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void OnBrightnessChanged(int brightness) {
         brightnessFinal=brightness;
         Filter myFilter=new Filter();
         myFilter.addSubFilter(new BrightnessSubFilter(brightness));
         img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
}

    @Override
    public void OnContrastChanged(float Contrast) {
        constrantFinal=Contrast;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(Contrast));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void OnSaturationChanged(float Saturation) {
        saturationFinal=Saturation;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(Saturation));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
         Bitmap bitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
         Filter myFilter=new Filter();
         myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
         myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
         myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));


         finalBitmap=myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
         resetcontrol();
        filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        img_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);


    }

    private void resetcontrol() {
         if (editImageFragment!=null){
             editImageFragment.resetcontrols();}
             brightnessFinal=0;
             saturationFinal=1.0f;
             constrantFinal=1.0f;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id =item.getItemId();
         if(id==R.id.action_open){
             openImageFromGallery();
             return true;
         }
        if(id==R.id.action_save){
            SaveImagetoGallery();
            return true;
        }
        else{
            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that
            // changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    };

            // Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }
    }

    private void SaveImagetoGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            try {
                                final String path=BitMapUtils.insertImage(getContentResolver(),finalBitmap,System.currentTimeMillis()+".jpg",null);
                                 if(!TextUtils.isEmpty(path)){
                                     Snackbar snackbar= Snackbar.make(coordinatorLayout,"Image Saved to gallery!",Snackbar.LENGTH_LONG).setAction("OPEN", new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                           openImage(path);
                                         }
                                     });
                                     snackbar.show();

                                 }
                                 else{
                                     Snackbar snackbar= Snackbar.make(coordinatorLayout,"Unable to Save Image",Snackbar.LENGTH_LONG);
                                     snackbar.show();
                                 }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                              token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void openImage(String path) {
         Intent intent=new Intent();
         intent.setAction(Intent.ACTION_VIEW);
         intent.setDataAndType(Uri.parse(path),"image/*");
         startActivity(intent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if ((report.areAllPermissionsGranted())){
                            Intent intent =new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,PERMISSION_PICK_IMAGE);


                        }
                        else{
                            Toast.makeText(MainActivity.this,"Permission denied!",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                      token.continuePermissionRequest();
                    }
                })
                .check();
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         if(resultCode==RESULT_OK && requestCode==PERMISSION_PICK_IMAGE){
             Bitmap bitmap=BitMapUtils.getBitmapfromgallery(this,data.getData(),800,800);


             originalBitmap.recycle();
             filteredBitmap.recycle();
             finalBitmap.recycle();
             originalBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
             filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
             finalBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
             img_preview.setImageBitmap(originalBitmap);
             bitmap.recycle();
             filtersListFragment.displayThumbnail(originalBitmap);
         }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
