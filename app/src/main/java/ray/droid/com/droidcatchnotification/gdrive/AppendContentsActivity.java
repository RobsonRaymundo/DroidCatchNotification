/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ray.droid.com.droidcatchnotification.gdrive;

import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import ray.droid.com.droidcatchnotification.R;
import ray.droid.com.droidcatchnotification.common.DroidConstantes;
import ray.droid.com.droidcatchnotification.common.DroidMethods;

/**
 * An activity to illustrate how to edit contents of a Drive file.
 */
public class AppendContentsActivity extends BaseDemoActivity {
    private static final String TAG = "AppendContentsActivity";

    @Override
    protected void onDriveClientReady() {

            // [START query_title]
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, DroidMethods.getDateFormated() + ".txt"))
                    .build();
            // [END query_title]
            Task<MetadataBuffer> queryTask =
                    getDriveResourceClient()
                            .query(query)
                            .addOnSuccessListener(this,
                                    new OnSuccessListener<MetadataBuffer>() {
                                        @Override
                                        public void onSuccess(MetadataBuffer metadataBuffer) {

                                            try {
                                                if (metadataBuffer.getCount() > 0)
                                                {
                                                    DriveId id = metadataBuffer.get(0).getDriveId();
                                                    appendContents(id.asDriveFile());
                                                }
                                                else {
                                                    Intent mIntent = new Intent(getBaseContext(), CreateFileActivity.class);
                                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    String msg =  getIntent().getStringExtra(DroidConstantes.MESSAGE);
                                                    mIntent.putExtra(DroidConstantes.MESSAGE, msg);
                                                    startActivity(mIntent);

                                                }
                                            }
                                            catch (Exception ex)
                                            {

                                            }


                                        }
                                    })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {



                                }
                            });

    }

    private void appendContents(DriveFile file) {


        // [START open_for_append]
        Task<DriveContents> openTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_WRITE);
        // [END open_for_append]
        // [START append_contents]
        openTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents driveContents = task.getResult();
                        ParcelFileDescriptor pfd = driveContents.getParcelFileDescriptor();
                        long bytesToSkip = pfd.getStatSize();
                        try (InputStream in = new FileInputStream(pfd.getFileDescriptor())) {
                            // Skip to end of file
                            while (bytesToSkip > 0) {
                                long skipped = in.skip(bytesToSkip);
                                bytesToSkip -= skipped;
                            }
                        }
                        try (OutputStream out = new FileOutputStream(pfd.getFileDescriptor())) {
                            String msg = getIntent().getStringExtra(DroidConstantes.MESSAGE);

                            out.write('\n');
                            out.write(msg.getBytes());
                        }
                        // [START commit_contents_with_metadata]
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                              .setStarred(true)
                                                              .setLastViewedByMeDate(new Date())
                                                              .build();
                        Task<Void> commitTask =
                                getDriveResourceClient().commitContents(driveContents, changeSet);
                        // [END commit_contents_with_metadata]
                        return commitTask;
                    }
                })
                .addOnSuccessListener(this,
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessage(getString(R.string.content_updated));
                                finish();
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to update contents", e);
                        showMessage(getString(R.string.content_update_failed));
                        finish();
                    }
                });
        // [END append_contents]
    }
}
