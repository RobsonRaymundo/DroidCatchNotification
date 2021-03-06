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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ray.droid.com.droidcatchnotification.R;
import ray.droid.com.droidcatchnotification.common.DroidCommon;
import ray.droid.com.droidcatchnotification.common.DroidPreferences;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateFileActivity extends BaseDemoActivity {
    private static final String TAG = "CreateFileActivity";
    private Context context;

    @Override
    protected void onDriveClientReady() {
        createFile();
    }

    private void createFile() {
        context = getBaseContext();
        // [START create_file]
        final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {

                            writer.write(DroidCommon.MESSAGE);
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("CatchNotification-" +DroidCommon.getNameDevice(context) + ".txt")
                                .setMimeType("text/plain")
                                .setStarred(true)

                                .build();

                        return getDriveResourceClient().createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveFile>() {
                            @Override
                            public void onSuccess(DriveFile driveFile) {
                                //    DroidCommon.SetDriveFile(driveFile);
                                DroidPreferences.SetString(context, "DriveId", driveFile.getDriveId().encodeToString());
                                showMessage(getString(R.string.file_created, driveFile.getDriveId().encodeToString()));
                                finish();
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                        showMessage(getString(R.string.file_create_error));
                        finish();
                    }
                });
        // [END create_file]

    }
}
