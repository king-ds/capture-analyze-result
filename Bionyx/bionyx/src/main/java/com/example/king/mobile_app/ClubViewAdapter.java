package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class ClubViewAdapter extends AppCompatActivity {

    // other variables
    private boolean success;
    private int counter;

    // String variables
    private String transactionid, disorder, uploaded, image, position, bl, cn, h, sn, yn, tn, stat, diseases, first_name, last_name, email;
    private String A1, A2, A3, A4, A5, A6, A7, is_answered;

    // GUI staff
    private TextView txtstatus, txtdiseases;
    private TextView answer_1, answer_2, answer_3, answer_4, answer_5, answer_6, answer_7;
    private TextView final_answer_1, final_answer_2, final_answer_3, final_answer_4, final_answer_5, final_answer_6, final_answer_7;
    private Button okay;
    private ImageView imagenail;
    private View focusView;
    private Bitmap bitmap;
    private SweetAlertDialog pDialog;

    // URL server
    private String UPDATE_ANSWER = "http://" + currentIp + "/api/updateAnswer/";

    // Image loader
    HistoryPhotoLoader historyPhotoLoader = new HistoryPhotoLoader(this);

    //Decimal format
    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from unhealthyviewxml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.clubview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counter = 0;
        success = false;

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.email = prefs.getString("email", "");
        Intent club_intent = getIntent();
        // Get the result of transaction id
        transactionid = club_intent.getStringExtra("transactionid");
        uploaded = club_intent.getStringExtra("uploaded");
        disorder = club_intent.getStringExtra("disorder");
        stat = club_intent.getStringExtra("status");
        diseases = club_intent.getStringExtra("diseases");
        image = club_intent.getStringExtra("image");

        A1 = club_intent.getStringExtra("answer_1");
        A2 = club_intent.getStringExtra("answer_2");
        A3 = club_intent.getStringExtra("answer_3");
        A4 = club_intent.getStringExtra("answer_4");
        A5 = club_intent.getStringExtra("answer_5");
        A6 = club_intent.getStringExtra("answer_6");
        A7 = club_intent.getStringExtra("answer_7");
        is_answered = club_intent.getStringExtra("isAnswered");

        bitmap = club_intent.getParcelableExtra("bitmapImage");

        UPDATE_ANSWER+= transactionid+"/";

        final_answer_1 = findViewById(R.id.tvA1);
        final_answer_2 = findViewById(R.id.tvA2);
        final_answer_3 = findViewById(R.id.tvA3);
        final_answer_4 = findViewById(R.id.tvA4);
        final_answer_5 = findViewById(R.id.tvA5);
        final_answer_6 = findViewById(R.id.tvA6);
        final_answer_7 = findViewById(R.id.tvA7);

        txtstatus = findViewById(R.id.tvStatus);
        txtdiseases = findViewById(R.id.tvDiseases);
        imagenail = findViewById(R.id.image);

        setDetails();

        if(is_answered.equals("true")) {
            setAnswer();
        }

        getSupportActionBar().setTitle("Transaction #: "+transactionid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setAnswer(){

        final_answer_1.setText(A1);
        final_answer_2.setText(A2);
        final_answer_3.setText(A3);
        final_answer_4.setText(A4);
        final_answer_5.setText(A5);
        final_answer_6.setText(A6);
        final_answer_7.setText(A7);
    }

    private void setDetails(){

        txtdiseases.setText(diseases);
        txtstatus.setText(stat);
        historyPhotoLoader.DisplayImage(image, imagenail);
    }

    private void generatePDF(){

        Rectangle pagesize = PageSize.LETTER;
        Document document = new Document(pagesize);
        Date date = new Date();
        String strDataFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDataFormat);
        final String formattedDate = dateFormat.format(date);

        try {

            //PDF CREATION
            //logo
            Resources res = this.getResources();
            Drawable drawable = res.getDrawable(R.drawable.logo_bionyx);
            BitmapDrawable bitDw = ((BitmapDrawable)drawable);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image logo = Image.getInstance(stream.toByteArray());
            logo.scaleAbsolute(173,61);
            logo.setAbsolutePosition(220f,700f);

            String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bionyx/" +last_name +"_"+formattedDate+ ".pdf";
            System.out.println(dest);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
            //font types
            Font header = new Font(Font.FontFamily.COURIER, 22, Font.BOLD);
            Font info = new Font(Font.FontFamily.COURIER,14,Font.BOLD);
            Font font = new Font(Font.FontFamily.COURIER,12);
            Font answer = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
            Paragraph space = new Paragraph();
            for (int i = 0; i < 4; i++){
                space.add(new Paragraph(" "));
            }
            document.open();
            document.add(logo);
            document.add(space);
            //contents (name)
            Paragraph name = new Paragraph("Name: "+last_name+", "+first_name, info);
            document.add(name);

            Paragraph emails = new Paragraph("Email: "+email, info);
            document.add(emails);
            //label result
            Paragraph resultsLbl = new Paragraph("ASSESSMENT RESULTS", header);
            resultsLbl.setAlignment(Element.ALIGN_CENTER);
            document.add(space);
            document.add(resultsLbl);

            Paragraph imgLbl = new Paragraph("Captured Image",info);
            imgLbl.setAlignment(Element.ALIGN_CENTER);
            document.add(space);
            document.add(space);
            document.add(imgLbl);
            //captured image
            System.out.println(image);
            Image captured = Image.getInstance(new URL(image));
            captured.rotate();
            captured.scaleAbsolute(160f,100f);
            captured.setAbsolutePosition(220f,405f);
            document.add(captured);

            Paragraph disorder = new Paragraph("Disorder: Club Nails", info);
            document.add(space);
            document.add(disorder);

            document.add(new Paragraph("Diseases: Heart Disease", info));
            document.add(new Paragraph("Status: Unhealthy", info));
            document.add(space);
            String note = "**This procedure is similar to other laboratory tests that are requested by physicians.  " +
                    "It is meant to help assist in arriving at an impression or a diagnosis; however, the final disposition of the patient remains with the doctor.  " +
                    "Whatever result this app shows, it is best to consult a physician. **";
            Paragraph reminder = new Paragraph(note);
            reminder.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(reminder);
            document.add(space);

            Paragraph questionnaire = new Paragraph("Questionnaire", info);
            document.add(questionnaire);

            String A1 = final_answer_1.getText().toString();
            System.out.println(A1);
            Paragraph question1 = new Paragraph("1.) Were you born with this problem?", font);
            Paragraph answer1 = new Paragraph("Answer: "+A1, answer);
            document.add(question1);
            document.add(answer1);

            String A2 = final_answer_2.getText().toString();
            System.out.println(A2);
            Paragraph question2 = new Paragraph("2.) Does anyone in your family have:" +
                    "\n\ta.) Nail problems" +
                    "\n\tb.) History of heart problems, lung problems, liver problems, etc.", font);
            Paragraph answer2 = new Paragraph("Answer: "+A2, answer);
            document.add(question2);
            document.add(answer2);

            String A3 = final_answer_3.getText().toString();
            System.out.println(A3);
            Paragraph question3 = new Paragraph("3.) When did you first ever have a problem like this?", font);
            Paragraph answer3 = new Paragraph("Answer: "+A3, answer);
            document.add(question3);
            document.add(answer3);

            String A4 = final_answer_4.getText().toString();
            System.out.println(A4);
            Paragraph question4 = new Paragraph("4.) How has this changed from beginning to now?", font);
            Paragraph answer4 = new Paragraph("Answer: "+A4, answer);
            document.add(question4);
            document.add(answer4);

            String A5 = final_answer_5.getText().toString();
            System.out.println(A5);
            Paragraph question5 = new Paragraph("5.) . Does the skin ever have a blue color?", font);
            Paragraph answer5 = new Paragraph("Answer: "+A5, answer);
            document.add(question5);
            document.add(answer5);

            String A6 = final_answer_6.getText().toString();
            System.out.println(A6);
            Paragraph question6 = new Paragraph("6.) Do you think it is getting worse?", font);
            Paragraph answer6 = new Paragraph("Answer: "+A6, answer);
            document.add(question6);
            document.add(answer6);

            String A7 = final_answer_7.getText().toString();
            System.out.println(A7);
            Paragraph question7 = new Paragraph("7.) What other symptoms do you have?", font);
            Paragraph answer7 = new Paragraph("Answer: "+A7, answer);
            document.add(question7);
            document.add(answer7);

            document.close();
            success = true;
        } catch (DocumentException e) {
            e.printStackTrace();
            success = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
    }

    /*
    Action bar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_item, menu);
        return true;
    }

    /*
  Boolean for selected options in menu
   */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_item:
                if(counter == 0) {
                    if(is_answered.equals("false")) {
                        openClubNailsQuestionnaire();
                    }else {
                        final SweetAlertDialog dialog = new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.NORMAL_TYPE);
                        dialog.setTitleText("Confirmation")
                                .setContentText("Do you want to generate it as PDF?")
                                .setConfirmText("Save")
                                .setCancelText("Don't save")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                        pDialog = new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
                                        pDialog.show();
                                        pDialog.setCancelable(false);
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                generatePDF();
                                                if (success) {
                                                    pDialog.dismiss();
                                                    ClubViewAdapter.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Awesome!")
                                                                    .setContentText("Your transaction has been saved as PDF")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismiss();
                                                                            counter = +1;
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    });
                                                } else {
                                                    new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Oh snap!")
                                                            .setContentText("An error has occured while generating a PDF")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismiss();
                                                                    counter = +1;
                                                                }
                                                            })
                                                            .show();
                                                }
                                            }
                                        });
                                        thread.start();
                                        Handler handler = new Handler() {
                                            public void handleMessage(android.os.Message msg) {
                                                pDialog.dismiss();

                                            }

                                            ;
                                        };
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }else {
                    ClubViewAdapter.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("You have already generated the PDF!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetErrors(){

        answer_1.setError(null);
        answer_2.setError(null);
        answer_3.setError(null);
        answer_4.setError(null);
        answer_5.setError(null);
        answer_6.setError(null);
        answer_7.setError(null);
    }

    private void openClubNailsQuestionnaire(){
        AlertDialog.Builder club_questionnaire = new AlertDialog.Builder(ClubViewAdapter.this);
        View ClubQuestionView = getLayoutInflater().inflate(R.layout.club_nails_questionnaire, null);
        club_questionnaire.setView(ClubQuestionView);
        final AlertDialog view_club_questionnaire = club_questionnaire.create();
        view_club_questionnaire.show();

        answer_1 = view_club_questionnaire.findViewById(R.id.etAnswer1);
        answer_2 = view_club_questionnaire.findViewById(R.id.etAnswer2);
        answer_3 = view_club_questionnaire.findViewById(R.id.etAnswer3);
        answer_4 = view_club_questionnaire.findViewById(R.id.etAnswer4);
        answer_5 = view_club_questionnaire.findViewById(R.id.etAnswer5);
        answer_6 = view_club_questionnaire.findViewById(R.id.etAnswer6);
        answer_7 = view_club_questionnaire.findViewById(R.id.etAnswer7);

        okay = view_club_questionnaire.findViewById(R.id.btnSubmit);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String A1 = answer_1.getText().toString();
                String A2 = answer_2.getText().toString();
                String A3 = answer_3.getText().toString();
                String A4 = answer_4.getText().toString();
                String A5 = answer_5.getText().toString();
                String A6 = answer_6.getText().toString();
                String A7 = answer_7.getText().toString();

                System.out.println(A1);

                boolean cancel = false;
                resetErrors();
                if (TextUtils.isEmpty(A1)) {
                    answer_1.setError("This field cannot be blank");
                    focusView = answer_1;
                    cancel = true;
                } else if (TextUtils.isEmpty(A2)) {
                    answer_2.setError("This field cannot be blank");
                    focusView = answer_2;
                    cancel = true;
                } else if (TextUtils.isEmpty(A3)) {
                    answer_3.setError("This field cannot be blank");
                    focusView = answer_3;
                    cancel = true;
                } else if (TextUtils.isEmpty(A4)) {
                    answer_4.setError("This field cannot be blank");
                    focusView = answer_4;
                    cancel = true;
                } else if (TextUtils.isEmpty(A5)) {
                    answer_5.setError("This field cannot be blank");
                    focusView = answer_5;
                    cancel = true;
                } else if (TextUtils.isEmpty(A6)) {
                    answer_6.setError("This field cannot be blank");
                    focusView = answer_6;
                    cancel = true;
                } else if (TextUtils.isEmpty(A7)) {
                    answer_7.setError("This field cannot be blank");
                    focusView = answer_7;
                    cancel = true;
                }
                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        new UpdateQuestionnaireAnswer().execute();
                        view_club_questionnaire.dismiss();
                }
            }
        });
    }

    private class UpdateQuestionnaireAnswer extends AsyncTask<Void, Void, Void> {

        boolean isUpdated = false;
        int response_code = 0;
        String response_message;
        final String A1 = answer_1.getText().toString();
        final String A2 = answer_2.getText().toString();
        final String A3 = answer_3.getText().toString();
        final String A4 = answer_4.getText().toString();
        final String A5 = answer_5.getText().toString();
        final String A6 = answer_6.getText().toString();
        final String A7 = answer_7.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Sending");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONfunctions parser = new JSONfunctions();
            JSONObject update = parser.getCLubAnswerObject(true, A1, A2, A3, A4, A5, A6, A7);
            String message = update.toString();
            System.out.println(message);

            try {
                URL myUrl = new URL(UPDATE_ANSWER);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("PUT");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(message.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                System.out.println(os);
                os.write(message.getBytes());
                os.flush();
                os.close();

                //Connect to url
                connection.connect();
                response_code = connection.getResponseCode();
                response_message = connection.getResponseMessage();
                if (response_code == 200) {
                    isUpdated = true;
                    Log.e("ClubViewAdapter", "Server response message : " + response_message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String message;

            if(isUpdated){
                pDialog.dismiss();
                ClubViewAdapter.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Awesome!")
                                .setContentText("You have submitted the answer")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        final_answer_1.setText(answer_1.getText());
                                        final_answer_2.setText(answer_2.getText());
                                        final_answer_3.setText(answer_3.getText());
                                        final_answer_4.setText(answer_4.getText());
                                        final_answer_5.setText(answer_5.getText());
                                        final_answer_6.setText(answer_6.getText());
                                        final_answer_7.setText(answer_7.getText());
                                        is_answered = "true";
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }else{

                pDialog.dismiss();
                ClubViewAdapter.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SweetAlertDialog(ClubViewAdapter.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error!")
                                .setContentText("Please check your internet connection")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }
        }
    }
}