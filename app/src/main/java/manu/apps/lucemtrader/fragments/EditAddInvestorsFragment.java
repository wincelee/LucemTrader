package manu.apps.lucemtrader.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.CustomTextWatcher;
import manu.apps.lucemtrader.classes.Investor;
import manu.apps.lucemtrader.dao.FirebaseDao;
import manu.apps.lucemtrader.interfaces.ToolbarInterface;

public class EditAddInvestorsFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout tilName, tilUsername, tilCountry, tilPhoneNo, tilSecurityCode,
            tilDateJoined, tilSharedAgreed;
    private TextInputEditText etName, etUsername, etCountry, etPhoneNo, etSecurityCode,
            etDateJoined, etSharedAgreed;

    private String joinDate;

    private ProgressDialog progressDialog;

    private Dialog errorExceptionDialog;

    private NavController navController;

    private BottomSheetBehavior<View> cameraGalleryBottomSheetBehavior;

    private static final int CAMERA_REQUEST_CODE = 555;

    private Uri imageUri;
    private FirebaseDao firebaseDao;

    private StorageTask storageUploadsTask;

    MaterialCardView mcvCaptureFromCamera, mcvPickFromGallery;

    MaterialButton btnCaptureFromCamera, btnPickFromGallery;

    CircleImageView civInvestorAvatar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.edit_add_investors_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        firebaseDao = new FirebaseDao();

        TextView tvDisplayAccount = view.findViewById(R.id.tv_display_account);
        MaterialButton btnEdit = view.findViewById(R.id.btn_edit);
        mcvCaptureFromCamera = view.findViewById(R.id.mcv_capture_from_camera);
        mcvPickFromGallery = view.findViewById(R.id.mcv_pick_from_gallery);
        btnCaptureFromCamera = view.findViewById(R.id.btn_capture_from_camera);
        btnPickFromGallery = view.findViewById(R.id.btn_pick_from_gallery);
        tilName = view.findViewById(R.id.til_name);
        tilUsername = view.findViewById(R.id.til_username);
        tilCountry = view.findViewById(R.id.til_country);
        tilPhoneNo = view.findViewById(R.id.til_phone_no);
        tilSecurityCode = view.findViewById(R.id.til_security_code);
        tilDateJoined = view.findViewById(R.id.til_date_joined);
        tilSharedAgreed = view.findViewById(R.id.til_shared_agreed);
        etName = view.findViewById(R.id.et_name);
        etUsername = view.findViewById(R.id.et_username);
        etCountry = view.findViewById(R.id.et_country);
        etPhoneNo = view.findViewById(R.id.et_phone_no);
        etSecurityCode = view.findViewById(R.id.et_security_code);
        etDateJoined = view.findViewById(R.id.et_date_joined);
        etSharedAgreed = view.findViewById(R.id.et_shared_agreed);
        civInvestorAvatar = view.findViewById(R.id.civ_investor_avatar);
        civInvestorAvatar.setTag(R.drawable.ic_gallery);

        if (returnFragmentLaunchType().equalsIgnoreCase("editInvestor")) {

            ((ToolbarInterface) requireActivity()).setToolbarTitle(String.format("%s", "Edit Investor"));

            EditAddInvestorsFragmentArgs editAddInvestorsFragmentArgs =
                    EditAddInvestorsFragmentArgs.fromBundle(getArguments());

            etName.setText(editAddInvestorsFragmentArgs.getName());
            etUsername.setText(editAddInvestorsFragmentArgs.getUsername());
            etCountry.setText(editAddInvestorsFragmentArgs.getCountry());
            etPhoneNo.setText(editAddInvestorsFragmentArgs.getPhoneNo());
            etSecurityCode.setText(editAddInvestorsFragmentArgs.getSecurityCode());
            etDateJoined.setText(editAddInvestorsFragmentArgs.getDateJoined());
            joinDate = editAddInvestorsFragmentArgs.getDateJoined();
            etSharedAgreed.setText(editAddInvestorsFragmentArgs.getSharedAgreed());

        } else if (returnFragmentLaunchType().equalsIgnoreCase("addInvestor")) {

            ((ToolbarInterface) requireActivity()).setToolbarTitle(String.format("%s", "Add Investor"));

        } else {

            ((ToolbarInterface) requireActivity()).setToolbarTitle(String.format("%s", ""));
        }

        tvDisplayAccount.bringToFront();

        etDateJoined.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        mcvCaptureFromCamera.setOnClickListener(this);
        mcvPickFromGallery.setOnClickListener(this);
        btnCaptureFromCamera.setOnClickListener(this);
        btnPickFromGallery.setOnClickListener(this);
        civInvestorAvatar.setOnClickListener(this);

        etName.addTextChangedListener(new CustomTextWatcher(tilName));
        etUsername.addTextChangedListener(new CustomTextWatcher(tilUsername));
        etCountry.addTextChangedListener(new CustomTextWatcher(tilCountry));
        etPhoneNo.addTextChangedListener(new CustomTextWatcher(tilPhoneNo));
        etSecurityCode.addTextChangedListener(new CustomTextWatcher(tilSecurityCode));
        etDateJoined.addTextChangedListener(new CustomTextWatcher(tilDateJoined));
        etSharedAgreed.addTextChangedListener(new CustomTextWatcher(tilSharedAgreed));


        setEtOnFocusChangeListener(etName, requireActivity().getString(R.string.name_hint));
        setEtOnFocusChangeListener(etUsername, requireActivity().getString(R.string.username_hint));
        setEtOnFocusChangeListener(etCountry, requireActivity().getString(R.string.country_hint));
        setEtOnFocusChangeListener(etPhoneNo, requireActivity().getString(R.string.phone_number_hint));
        setEtOnFocusChangeListener(etSecurityCode, requireActivity().getString(R.string.security_code_hint));
        setEtOnFocusChangeListener(etDateJoined, requireActivity().getString(R.string.date_joined_hint));
        setEtOnFocusChangeListener(etSharedAgreed, requireActivity().getString(R.string.shared_agreed_hint));

        View cameraGalleryView = view.findViewById(R.id.camera_gallery_bottom_sheet);
        cameraGalleryBottomSheetBehavior = BottomSheetBehavior.from(cameraGalleryView);
        cameraGalleryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }


    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.et_date_joined) {

            showCalendar();

        }

        if (viewId == R.id.btn_edit) {

            cameraGalleryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

        if (viewId == R.id.mcv_capture_from_camera | viewId == R.id.btn_capture_from_camera) {

            checkAndRequestCameraPermissions();

        }

        if (viewId == R.id.mcv_pick_from_gallery | viewId == R.id.btn_pick_from_gallery) {

            pickImageFromGallery.launch("image/*");

        }

        if (viewId == R.id.civ_investor_avatar) {

            cameraGalleryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
    }

    private void checkAndRequestCameraPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // If The Permission has already been granted proceed with the action

                    captureImageFromCamera();


                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    showErrorExceptionDialog("cameraPermissionDenied", "Camera Permission Was Denied",
                            "We require camera permission to be able to capture investors avatar", R.drawable.ic_info);


                } else {

                    // If Permission has not been granted
                    // Request the permission first time

                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);


                }
            }
        }

    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                    // Permission is granted. Continue the action or workflow in your app.
                    captureImageFromCamera();

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.

                    showErrorExceptionDialog("cameraPermissionDeniedNoRetry", "Camera Permission Was Denied",
                            "You have denied Camera permissions you won't be able to capture" +
                                    "the investors avatar but you will continue using the app", R.drawable.ic_info);


                }
            });

    private void captureImageFromCamera() {

        cameraGalleryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (openCameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {

            startActivityForResult(openCameraIntent, CAMERA_REQUEST_CODE);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {

            if (resultCode == RESULT_OK && data != null) {

                // Bitmap captureImageBitmap = data.getParcelableExtra("data");
                // or use method below
                // Bitmap captureProfileImageBitmap = (Bitmap) data.getExtras().get("data");

                civInvestorAvatar.setImageBitmap((Bitmap) data.getExtras().get("data"));

                imageUri = Config.returnUri(requireActivity(), (Bitmap) data.getExtras().get("data"),
                        "avatar", null);

                if (imageUri != null) {

                    new AlertDialog.Builder(requireActivity())
                            .setMessage("ImageUri is not null")
                            .show();
                } else {

                    new AlertDialog.Builder(requireActivity())
                            .setMessage("Null")
                            .show();
                }

                civInvestorAvatar.setTag(1);

            }

        }
    }

    ActivityResultLauncher<String> pickImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {

                    try {

                        cameraGalleryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                        InputStream inputStream = requireActivity().getApplicationContext()
                                .getContentResolver().openInputStream(uri);

                        //imvId.setImageURI(uri);

                        imageUri = uri;

                        civInvestorAvatar.setImageBitmap(BitmapFactory.decodeStream(inputStream));

                        civInvestorAvatar.setTag(1);

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                        // Handle Image Not selected Errors Here

                    }


                }
            });

    private void setEtOnFocusChangeListener(TextInputEditText textInputEditText, String hint) {

        textInputEditText.setOnFocusChangeListener((etTouchView, b) -> {
            if (etTouchView.isFocused()) {
                textInputEditText.setHint(null);
            } else {
                textInputEditText.setHint(hint);
            }
        });

    }

    private void showCalendar() {

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Join Date")
                .setTheme(R.style.MaterialDatePickerTheme)
                .build();

        materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {

            etDateJoined.setText(materialDatePicker.getHeaderText());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

            Date date = new Date(selection);

            joinDate = simpleDateFormat.format(date);

        });
    }

    private String returnFragmentLaunchType() {

        return EditAddInvestorsFragmentArgs.fromBundle(getArguments()).getAddEditInvestorsLaunchType();
    }

    private String returnAvatarUrl() {

        return EditAddInvestorsFragmentArgs.fromBundle(getArguments()).getAvatarUrl();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (returnFragmentLaunchType().equalsIgnoreCase("editInvestor") |
                returnFragmentLaunchType().equalsIgnoreCase("addInvestor")) {

            inflater.inflate(R.menu.edit_add_investor_menu, menu);

        }

        menu.findItem(R.id.action_admin_account).setVisible(false);
        menu.findItem(R.id.action_admin_settings).setVisible(false);
        menu.findItem(R.id.action_admin_log_out).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_save_edit_add_investor) {

            investorValidation();

        }

        return super.onOptionsItemSelected(item);

    }

    private void investorValidation() {

        if (imageViewHasImage(civInvestorAvatar)) {

            if (getImageViewDrawableId(civInvestorAvatar) == R.drawable.ic_gallery) {

                Config.showSnackBar(requireActivity(), "Investor avatar has no image", 0,
                        Snackbar.LENGTH_LONG);

            } else {

                String name = Objects.requireNonNull(etName.getText()).toString().trim();
                String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
                String country = Objects.requireNonNull(etCountry.getText()).toString().trim();
                String phoneNo = Objects.requireNonNull(etPhoneNo.getText()).toString().trim();
                String securityCode = Objects.requireNonNull(etSecurityCode.getText()).toString().trim();
                String sharedAgreed = Objects.requireNonNull(etSharedAgreed.getText()).toString().trim();

                if (TextUtils.isEmpty(name)) {

                    tilName.setError("Name has not been entered");
                    etName.requestFocus();

                } else if (TextUtils.isEmpty(username)) {

                    tilUsername.setError("Username has not been entered");
                    etUsername.requestFocus();

                } else if (TextUtils.isEmpty(country)) {

                    tilCountry.setError("Country has not been entered");
                    etCountry.requestFocus();

                } else if (TextUtils.isEmpty(phoneNo)) {

                    tilPhoneNo.setError("Phone number has not been entered");
                    etPhoneNo.requestFocus();

                } else if (TextUtils.isEmpty(securityCode)) {

                    tilSecurityCode.setError("Security code has not been entered");
                    etSecurityCode.requestFocus();

                } else if (TextUtils.isEmpty(joinDate)) {

                    tilDateJoined.setError("Date joined has not been selected");
                    etDateJoined.requestFocus();

                } else if (TextUtils.isEmpty(sharedAgreed)) {

                    tilSharedAgreed.setError("Shared agreed has not been entered");
                    etSharedAgreed.requestFocus();

                } else {

                    Query findInvestorByPhoneNoQuery = firebaseDao.returnInvestorsDatabaseReference()
                            .orderByChild("phoneNo").equalTo(phoneNo);

                    findInvestorByPhoneNoQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                Config.showSnackBar(requireActivity(), "We already have an investor with the phone number", 0,
                                        Snackbar.LENGTH_LONG);

                            } else {

                                if (imageUri != null) {

                                }else {

                                    Config.showSnackBar(requireActivity(), "Investor avatar has no image", 0,
                                            Snackbar.LENGTH_LONG);

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

//                        Investor investor = new Investor(name, username, country, phoneNo,
//                                securityCode, joinDate, sharedAgreed, "");
//
//                        if (returnFragmentLaunchType().equalsIgnoreCase("addInvestor")) {
//
//
//                            initializeShowProgressDialog();
//
//                            firebaseDao.addInvestor(investor).addOnSuccessListener(success -> {
//
//                                dismissProgressDialog();
//
//                                etName.getText().clear();
//                                etUsername.getText().clear();
//                                etCountry.getText().clear();
//                                etPhoneNo.getText().clear();
//                                etSecurityCode.getText().clear();
//                                Objects.requireNonNull(etDateJoined.getText()).clear();
//                                etSharedAgreed.getText().clear();
//
//                                Config.showSnackBar(requireActivity(), "Investor was added successfully", 0,
//                                        Snackbar.LENGTH_LONG);
//
//                                navController.navigateUp();
//
//                            }).addOnFailureListener(e -> {
//
//                                dismissProgressDialog();
//
//                                showErrorExceptionDialog("addingInvestorException", e.getMessage(),
//                                        "We encountered an error while adding investor", R.drawable.ic_info);
//
//                            });
//
//                        } else if (returnFragmentLaunchType().equalsIgnoreCase("editInvestor")) {
//
//
//                /*
//                * HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("name", name);
//                hashMap.put("username", username);
//
//                firebaseDao.updateInvestor("",hashMap).addOnSuccessListener(success -> {
//
//                    dismissProgressDialog();
//
//                    Config.showSnackBar(requireActivity(), "Investor was updated successfully", 0,
//                            Snackbar.LENGTH_LONG);
//
//                }).addOnFailureListener(e -> {
//
//                    dismissProgressDialog();
//
//                    showErrorExceptionDialog("updatingInvestorException", e.getMessage(),
//                            "We encountered an error while updating investor", R.drawable.ic_info);
//
//                });
//
//                firebaseDao.removeInvestor("").addOnSuccessListener(success -> {
//
//                    dismissProgressDialog();
//
//                    Config.showSnackBar(requireActivity(), "Investor was added successfully", 0,
//                            Snackbar.LENGTH_LONG);
//
//                }).addOnFailureListener(e -> {
//
//                    dismissProgressDialog();
//
//                    showErrorExceptionDialog("removingInvestorException", e.getMessage(),
//                            "We encountered an error while removing investor", R.drawable.ic_info);
//
//                });*/
//                        }

                }

            }


        } else {

            Config.showSnackBar(requireActivity(), "Investor avatar has no image", 0,
                    Snackbar.LENGTH_LONG);
        }

    }

    private void addInvestor(String name, String username,
                             String country, String phoneNo,
                             String securityCode, String sharedAgreed){

        final StorageReference avatarStorageReference = firebaseDao.returnRootFirebaseStorage()
                .getReference("InvestorsAvatar")
                .child(firebaseDao.returnUserId())
                .child(username + "." + phoneNo + "." + Config.returnFileExtension(imageUri, requireActivity()));

        initializeShowProgressDialog();

        storageUploadsTask = avatarStorageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->

                        avatarStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                            Investor investor = new Investor(name, username, country, phoneNo,
                                    securityCode, joinDate, sharedAgreed, uri.toString());

                            if (returnFragmentLaunchType().equalsIgnoreCase("addInvestor")) {

                                firebaseDao.addInvestor(investor).addOnSuccessListener(success -> {

                                    etName.getText().clear();
                                    etUsername.getText().clear();
                                    etCountry.getText().clear();
                                    etPhoneNo.getText().clear();
                                    etSecurityCode.getText().clear();
                                    Objects.requireNonNull(etDateJoined.getText()).clear();
                                    etSharedAgreed.getText().clear();
                                    civInvestorAvatar.setImageResource(R.drawable.ic_gallery);

                                    dismissProgressDialog();

                                    Config.showSnackBar(requireActivity(), "Investor was added successfully...", 0,
                                            Snackbar.LENGTH_LONG);

//                                                    navController.navigateUp();

                                }).addOnFailureListener(e -> {

                                    dismissProgressDialog();

                                    showErrorExceptionDialog("addingInvestorException", e.getMessage(),
                                            "We encountered an error while adding investor", R.drawable.ic_info);

                                });

                            }
                        }))
                .addOnFailureListener(e -> {

                    dismissProgressDialog();

                    showErrorExceptionDialog("avatarUploadFailed", "Uploading avatar failed",
                            "We require camera permission to be able to capture investors avatar", R.drawable.ic_info);


                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                });

    }

    private boolean imageViewHasImage(@NonNull ImageView imageView) {

        Drawable drawable = imageView.getDrawable();

        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;

    }

    private int getImageViewDrawableId(ImageView imageView) {

        Integer integer = (Integer) imageView.getTag();

        integer = integer == null ? 0 : integer;

        return integer;
    }

    private void initializeShowProgressDialog() {

        if (progressDialog == null) {

            progressDialog = new ProgressDialog(requireActivity());

            progressDialog.show();

            progressDialog.setContentView(R.layout.custom_progress_dialog_layout);

            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            progressDialog.setCancelable(false);

        } else if (!progressDialog.isShowing()) {

            progressDialog.show();

        }

    }

    private void dismissProgressDialog() {

        progressDialog.dismiss();

    }

    private void showErrorExceptionDialog(String statType, String stat, String tvMessage, int drawableId) {

        if (errorExceptionDialog == null) {

            errorExceptionDialog = new Dialog(requireActivity());
            errorExceptionDialog.setContentView(R.layout.error_exception_dialog_layout);

            errorExceptionDialog.setCancelable(false);

            errorExceptionDialog.show();

        } else if (!errorExceptionDialog.isShowing()) {

            errorExceptionDialog.show();

        }

        // Setting dialog background to transparent
        (errorExceptionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Setting size of the dialog
        errorExceptionDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imvErrorExceptionDialog = errorExceptionDialog.findViewById(R.id.imv_error_exception_dialog);
        TextView tvErrorExceptionDialogMessage = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_message);
        final TextView tvErrorExceptionDialogStatsForNerdsType = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds_type);
        final TextView tvErrorExceptionDialogStatsForNerds = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds);
        MaterialButton btnErrorExceptionCancel = errorExceptionDialog.findViewById(R.id.btn_error_exception_cancel);
        MaterialButton btnErrorExceptionRetry = errorExceptionDialog.findViewById(R.id.btn_error_exception_retry);

        imvErrorExceptionDialog.setImageDrawable(ContextCompat.getDrawable(requireActivity(), drawableId));
        tvErrorExceptionDialogMessage.setText(tvMessage);

        if (!TextUtils.isEmpty(statType)) {

            tvErrorExceptionDialogStatsForNerdsType.setText(statType);

        }

        if (!TextUtils.isEmpty(stat)) {

            tvErrorExceptionDialogStatsForNerds.setText(stat);

        }

        imvErrorExceptionDialog.setOnClickListener(v -> {

            tvErrorExceptionDialogStatsForNerdsType.setVisibility(View.VISIBLE);

            tvErrorExceptionDialogStatsForNerds.setVisibility(View.VISIBLE);

        });


        if (statType.equalsIgnoreCase("addingInvestorException")) {

            btnErrorExceptionRetry.setOnClickListener(v -> {

                errorExceptionDialog.dismiss();

                investorValidation();

            });

            btnErrorExceptionCancel.setOnClickListener(v ->
                    errorExceptionDialog.dismiss());

        } else if (statType.equalsIgnoreCase("cameraPermissionDenied")) {


            btnErrorExceptionRetry.setOnClickListener(v -> {

                errorExceptionDialog.dismiss();

                checkAndRequestCameraPermissions();

            });

            btnErrorExceptionCancel.setOnClickListener(v ->

                    errorExceptionDialog.dismiss());

        } else if (statType.equalsIgnoreCase("cameraPermissionDeniedNoRetry")) {


            btnErrorExceptionRetry.setText(R.string.close);

            btnErrorExceptionRetry.setOnClickListener(v ->

                    errorExceptionDialog.dismiss());

            btnErrorExceptionCancel.setEnabled(false);
        }

    }
}