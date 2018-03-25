package com.thecoolguy.rumaan.fileio.utils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import kotlin.Pair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Bunch of helper methods here.
 */
public class Utils {

  /**
   * Wrapper class for all Android related helper methods.
   */
  public static final class Android {

    /* Returns the state of current network info */
    public static boolean isConnectedToNetwork(Context context) {
      ConnectivityManager connectivityManager = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivityManager != null) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
      } else {
        return false;
      }
    }

    /* Returns new intent for the given action and category */
    public static Intent newIntent(String action, String category, String type) {
      Intent intent = new Intent();
      intent.setAction(action);
      intent.setType(type);
      intent.addCategory(category);
      return intent;
    }

    /* Show the fragment with the provided fragment manager */
    public static void showDialogFragment(Fragment fragment, FragmentManager fragmentManager,
        String tag) {
      ((DialogFragment) fragment).show(fragmentManager, tag);
    }

    /* Dismisses the given dialog */
    public static void dismissDialog(Dialog dialog) {
      if (dialog == null) {
        return;
      }
      if (dialog.isShowing()) {
        dialog.dismiss();
      }
    }

    /* Opens App info screen in settings */
    public static void showAppDetailsSettings(Context context) {
      try {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
          intent = new Intent(Intent.ACTION_APPLICATION_PREFERENCES);
          intent.setData(Uri.parse("package:" + context.getPackageName()));
          context.startActivity(intent);
        } else {
          intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
          intent.setData(Uri.parse("package: " + context.getPackageName()));
          context.startActivity(intent);
        }
      } catch (ActivityNotFoundException e) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
        context.startActivity(intent);
        e.printStackTrace();
      }
    }

    /* Copy the given text into clipboard */
    public static void copyToClipboard(final Context context, final String label,
        final String text) {
      ClipboardManager clipboardManager = (ClipboardManager) context
          .getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clipData = ClipData.newPlainText(label, text);
      if (clipboardManager != null) {
        clipboardManager.setPrimaryClip(clipData);
      }
    }
  }

  /**
   * Wrapper class for all methods that parse or handle file.io URLS.
   */
  public static final class URLParser {

    public static String getExpireUrl(String daysToExpire) {
      return Constants.BASE_URL + Constants.QUERY + Constants.EXPIRE_PARAM + daysToExpire;
    }

    /* Removes the '/dwnld' from the URL */
    public static String parseEncryptUrl(final String s) {
      int index = s.lastIndexOf(Constants.POSTFIX);
      return s.substring(0, index);
    }

  }

  /**
   * Wrapper class for all methods that parse the JSON.
   */
  public static final class JSONParser {

    /**
     * Parse the JSON received.
     *
     * @param response JSON String.
     * @return Pair object of Received Link and Expiry Days.
     */
    public static Pair<String, Integer> parseResults(String response) {
      if (response != null) {
        try {
          JSONObject jsonObject = new JSONObject(response);
          String link = jsonObject.getString("link");

          // append POSTFIX key to link
          link += Constants.POSTFIX;

          String expiry = jsonObject.getString("expiry");
          Integer days = getDays(expiry);
          return new Pair<>(link, days);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    /**
     * Returns Days from the string.
     * ex: "242 Days" ->  returns 242
     */
    private static Integer getDays(String expiry) {
      // FIXME: look out for months and years, if you seek to implement them in the future.
      // Split on spaces
      return Integer.parseInt(expiry.split(" ")[0]);
    }
  }
}
