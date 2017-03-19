package com.landenlabs.allperfimages.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.OutputStream;

/**
 * Created by Dennis Lang on 2/25/17.
 */

public class ShareUtil {

    private static final String TAG = ShareUtil.class.getName();

    private static final String JPEG_MIME_TYPE = "image/jpeg";
    private static final String TEXT_MIME_TYPE = "text/plain";

    public static <typ> String join(typ path1, typ path2) {
        return new File(path1.toString(), path2.toString()).toString();
    }

    public static Bitmap grabScreen(Activity activity) {
        View view = activity.getWindow().getDecorView().getRootView();
        // View v1 = iv.getRootView(); //even this works
        // View v1 = findViewById(android.R.id.content); //this works too
        // but gives only content
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    /**
     * Starts share {@link Intent} with {@link Intent#ACTION_SEND} action for all mail applications
     *
     * @param activity
     *            instance of {@link Context}
     * @param emailTo
     *            recipient email
     * @param subject
     *            subject value
     * @param body
     *            body value
     * @param jpegUri
     *            {@link Uri} link to media content
     */
    public static final void shareViaEmail(Activity activity, String emailTo, String subject, String body, Uri jpegUri) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(TEXT_MIME_TYPE);
        if (null != jpegUri) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, jpegUri);
            shareIntent.setType(JPEG_MIME_TYPE);
        }
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);

        Intent mailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:" + emailTo));
        activity.startActivity(Intent.createChooser(shareIntent, "Share"));

        // List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(mailIntent, 0);
        // createShareDialog(activities, context, shareIntent).show();
    }

    /**
     * Sends email message, prompts for email client if multiple choices are available.
     *
     * @param activity
     *            instance of {@link Context}
     * @param emailTo
     *            recipient email
     * @param subject
     *            subject value
     * @param body
     *            body value
     * @param jpegUri
     *            {@link Uri} link to media content
     */
    public static final void shareSendEmail(Activity activity, String emailTo, String subject, String body, Uri jpegUri) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        // shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

        shareIntent.setType(TEXT_MIME_TYPE);
        if (null != jpegUri) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, jpegUri);
            shareIntent.setType(JPEG_MIME_TYPE);
        }

        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);

        Intent mailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:" + emailTo));
        activity.startActivity(Intent.createChooser(shareIntent, "Share"));

        // List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(mailIntent, 0);
        // createShareDialog(activities, context, shareIntent).show();
    }

    // =============================================================================================
    /**
     * Saves snapshot bitmap to cache folder.
     *
     * @author Nazar Ivanchuk
     *
     */
    private class SaveJpeg extends AsyncTask<Bitmap, Void, Uri> {
        Context mContext;

        public SaveJpeg(Context context) {
            mContext = context;
        }

        @Override
        protected Uri doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            Uri uri = null;
            OutputStream ostream;
            try {

                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                uri = mContext.getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                ostream = mContext.getContentResolver().openOutputStream(uri);

                // Jpeg format about 20x faster to export then PNG and smaller image.
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ostream);
                ostream.close();
            } catch (Exception ex) {
                Log.w(TAG, "Save jpeg failed " + ex.getMessage());
            }

            if (isCancelled()) {
                return null;
            }
            return uri;
        }

        @Override
        protected void onPostExecute(Uri jpegUri) {
            // shareSendEmail(.... jpegUri)
        }
    }
}
